package com.example.openeyes.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.openeyes.R;
import com.example.openeyes.databinding.FragmentMapBinding;
import com.example.openeyes.utility.PermissionManager;
import com.example.openeyes.utility.SnackBarHandler;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import java.util.List;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MapFragment extends Fragment implements View.OnClickListener {

    private FragmentMapBinding binding;
    private PermissionManager permissionManager;
    private static double latitude, longitude = 0.0;
    private IMapController mapController;
    private Marker userLocationMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = requireContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

        binding.cardViewHomeGps.setOnClickListener(this);

        permissionManager = new PermissionManager(this, new EasyPermissions.PermissionCallbacks() {
            @Override
            public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

            }

            @Override
            public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
                if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
                    new AppSettingsDialog.Builder(requireActivity()).build().show();
                } else {
                    permissionManager.requestLocationPermissions();

                }
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

            }
        });

        // Initialize the map.
        binding.mapViewHome.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapViewHome.setMultiTouchControls(true);
        binding.mapViewHome.setBuiltInZoomControls(false);

        // Set a random location on map for first view.
        mapController = binding.mapViewHome.getController();
        mapController.setZoom(11.0);
        mapController.setCenter(new GeoPoint(35.7443, 51.4435));

        double[] a = {35.7017, 35.6920, 35.7513, 35.29226, 35.32291};
        double[] b = {51.4501, 51.4294, 51.7209, 47.02382, 46.99302};

        for (int i=0; i<a.length; i++) {
            Marker marker = new Marker(binding.mapViewHome);
            marker.setPosition(new GeoPoint(a[i], b[i]));
            marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.svg_defect_location));
            binding.mapViewHome.getOverlays().add(marker);

        }

        return binding.getRoot();

    }

    /****************************************************/

    @Override
    public void onPause() {
        super.onPause();
        binding.mapViewHome.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!permissionManager.hasLocationPermission()) {
            permissionManager.requestLocationPermissions();
        }

        binding.mapViewHome.onResume();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        if (view.getId() == binding.cardViewHomeGps.getId()) {
            if (permissionManager.hasLocationPermission()) {
                if (isGpsEnable()) {
                    LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
                    String holder = locationManager.getBestProvider(new Criteria(), false);
                    locationManager.requestLocationUpdates(holder, 12000, 7, new android.location.LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            mapController.setZoom(18.0);
                            mapController.setCenter(new GeoPoint(latitude, longitude));

                            binding.mapViewHome.getOverlays().remove(userLocationMarker);

                            userLocationMarker = new Marker(binding.mapViewHome);
                            userLocationMarker.setPosition(new GeoPoint(latitude, longitude));
                            userLocationMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.svg_current_location));
                            binding.mapViewHome.getOverlays().add(userLocationMarker);

                        }
                    });

                } else {
                    SnackBarHandler.snackBarEnableGpsAction(
                            requireContext(),
                            binding.getRoot(),
                            requireContext().getString(R.string.gps_not_enable)
                    );

                }
            } else {
                permissionManager.requestLocationPermissions();

            }
        }
    }

    public boolean isGpsEnable() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

}