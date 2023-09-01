package com.example.openeyes.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivityGetLocationBinding;
import com.example.openeyes.utility.Constants;
import com.example.openeyes.utility.PermissionManager;
import com.example.openeyes.utility.SnackBarHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class GetLocationActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityGetLocationBinding binding;
    private PermissionManager permissionManager;
    private static double latitude, longitude = 0.0;
    private IMapController mapController;
    private Marker userLocationMarker;
    private Marker selectedLocationMarker;
    private RequestQueue queue = null;
    private static final String TAG = "OpenStreetMapUtils";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_location);

        binding.cardViewGetLocationGps.setOnClickListener(this);
        binding.fabGetLocation.setOnClickListener(this);
        binding.toolbarGetLocation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        permissionManager = new PermissionManager(this, new EasyPermissions.PermissionCallbacks() {
            @Override
            public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

            }

            @Override
            public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
                if (EasyPermissions.somePermissionPermanentlyDenied(GetLocationActivity.this, perms)) {
                    new AppSettingsDialog.Builder(GetLocationActivity.this).build().show();
                } else {
                    permissionManager.requestLocationPermissions();

                }
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

            }
        });

        if (!permissionManager.hasLocationPermission()) {
            permissionManager.requestLocationPermissions();

        }

        // Show snack bar to tell how the page will work.
        SnackBarHandler.snackBarHideAction2(this, binding.getRoot(), getString(R.string.hole_to_get));

        // Initialize the map.
        binding.mapViewGetLocation.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapViewGetLocation.setMultiTouchControls(true);
        binding.mapViewGetLocation.setBuiltInZoomControls(false);

        // Set a random location on map for first view.
        mapController = binding.mapViewGetLocation.getController();
        mapController.setZoom(11.0);
        mapController.setCenter(new GeoPoint(35.7443, 51.4435));

        binding.mapViewGetLocation.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                mapController.setCenter(p);

                binding.mapViewGetLocation.getOverlays().remove(selectedLocationMarker);

                selectedLocationMarker = new Marker(binding.mapViewGetLocation);
                selectedLocationMarker.setPosition(p);
                selectedLocationMarker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.svg_defect_location));
                binding.mapViewGetLocation.getOverlays().add(selectedLocationMarker);

                binding.fabGetLocation.setVisibility(View.VISIBLE);

                return true;
            }
        }));

    }

    /****************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.cardViewGetLocationGps.getId()) {
            setCurrentLocation();

        } else if (view.getId() == binding.fabGetLocation.getId()) {
            getLocationAddress(selectedLocationMarker.getPosition().getLatitude(), selectedLocationMarker.getPosition().getLongitude());

        }
    }

    private boolean isGpsEnable() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    @SuppressLint("MissingPermission")
    private void setCurrentLocation() {
        if (permissionManager.hasLocationPermission()) {
            if (isGpsEnable()) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                String holder = locationManager.getBestProvider(new Criteria(), false);
                locationManager.requestLocationUpdates(holder, 12000, 7, new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        mapController.setZoom(18.0);
                        mapController.setCenter(new GeoPoint(latitude, longitude));

                        binding.mapViewGetLocation.getOverlays().remove(userLocationMarker);

                        try {
                            userLocationMarker = new Marker(binding.mapViewGetLocation);
                            userLocationMarker.setPosition(new GeoPoint(latitude, longitude));
                            userLocationMarker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.svg_current_location));
                            binding.mapViewGetLocation.getOverlays().add(userLocationMarker);

                        } catch (Exception e) {
                        }
                    }
                });

            } else {
                SnackBarHandler.snackBarEnableGpsAction(
                        getApplicationContext(),
                        binding.getRoot(),
                        getString(R.string.gps_not_enable)
                );

            }
        } else {
            permissionManager.requestLocationPermissions();

        }
    }

    private void getLocationAddress(double lat, double lon) {
        queue = Volley.newRequestQueue(this);
        String url = Constants.NOMINATIM_BASE_URL + "format=json&lat=" + lat + "&lon=" + lon + "&zoom=18";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String address = response.getString("display_name");
                    getIntent().putExtra(Constants.DEFECT_LATITUDE, lat);
                    getIntent().putExtra(Constants.DEFECT_LONGITUDE, lon);
                    getIntent().putExtra(Constants.DEFECT_ADDRESS, address);
                    setResult(RESULT_OK, getIntent());
                    overridePendingTransition(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    );
                    finish();

                } catch (JSONException e) {
                    SnackBarHandler.snackBarHideAction3(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SnackBarHandler.snackBarHideAction3(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));

            }
        });

        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (queue != null) {
            queue.cancelAll(TAG);

        }
    }

}