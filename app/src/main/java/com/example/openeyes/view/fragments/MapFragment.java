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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private IMapController mapController;
    private Marker userLocationMarker;
    private DatabaseReference fDatabase;
    private ValueEventListener valueEventListener;

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
        binding.constLayoutMapView.setOnClickListener(this);

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

        fDatabase = FirebaseDatabase.getInstance().getReference("Defect");
        fDatabase.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot1 : snapshot.getChildren()) {
                        if (childSnapshot1.exists()) {
                            for (DataSnapshot childSnapshot2 : childSnapshot1.getChildren()) {
                                if (childSnapshot2.exists()) {
                                    double lat = childSnapshot2.child("latitude").getValue(Double.class);
                                    double lon = childSnapshot2.child("longitude").getValue(Double.class);

                                    Marker marker = new Marker(binding.mapViewHome);
                                    marker.setPosition(new GeoPoint(lat, lon));
                                    marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.svg_defect_location));
                                    binding.mapViewHome.getOverlays().add(marker);

                                }
                            }
                        }
                    }

                    binding.constLayoutMapView.setVisibility(View.GONE);

                } else {
                    binding.constLayoutMapView.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SnackBarHandler.snackBarHideAction3(requireContext(), binding.getRoot(), getString(R.string.error_occurred));
                binding.constLayoutMapView.setVisibility(View.GONE);

            }
        });

        return binding.getRoot();

    }

    /****************************************************/

    @Override
    public void onPause() {
        super.onPause();
        fDatabase.removeEventListener(valueEventListener);
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
                            mapController.setZoom(18.0);
                            mapController.setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));

                            binding.mapViewHome.getOverlays().remove(userLocationMarker);

                            try {
                                userLocationMarker = new Marker(binding.mapViewHome);
                                userLocationMarker.setPosition(new GeoPoint(location.getLatitude(), location.getLongitude()));
                                userLocationMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.svg_current_location));
                                binding.mapViewHome.getOverlays().add(userLocationMarker);

                            } catch (Exception e) {
                            }


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