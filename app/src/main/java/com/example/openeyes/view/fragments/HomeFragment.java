package com.example.openeyes.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.openeyes.R;
import com.example.openeyes.databinding.FragmentHomeBinding;
import com.example.openeyes.utility.PermissionManager;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FragmentHomeBinding binding;
    private PermissionManager permissionManager;
    private double latitude, longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

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

        if (!permissionManager.hasLocationPermission()) {
            permissionManager.requestLocationPermissions();
        }

        binding.mapViewHome.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapViewHome.setMultiTouchControls(true);
        binding.mapViewHome.setBuiltInZoomControls(false);

        return binding.getRoot();

    }

    /****************************************************/

    @Override
    public void onResume() {
        super.onResume();
        // this will refresh the osmdroid configuration on resuming.
        // if you make changes to the configuration, use
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        // Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
        binding.mapViewHome.onResume(); // Needed for compass, my location overlays, v6.0.0 and up

    }

    @Override
    public void onPause() {
        super.onPause();
        // this will refresh the osmdroid configuration on resuming.
        // if you make changes to the configuration, use
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        // Configuration.getInstance().save(requireContext(), prefs);
        binding.mapViewHome.onPause(); // Needed for compass, my location overlays, v6.0.0 and up

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        if (permissionManager.hasLocationPermission()) {
            if (isGpsEnable()) {
                LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
                String holder = locationManager.getBestProvider(new Criteria(), false);
                locationManager.requestLocationUpdates(holder, 12000, 7, new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        IMapController mapController = binding.mapViewHome.getController();
                        mapController.setZoom(20.0);
                        GeoPoint startPoint = new GeoPoint(latitude, longitude);
                        mapController.setCenter(startPoint);

                        Marker marker = new Marker(binding.mapViewHome);
                        marker.setPosition(new GeoPoint(latitude, longitude));
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                        binding.mapViewHome.getOverlays().add(marker);

                    }
                });

            } else {
                showSnackBar(requireContext().getString(R.string.gps_not_enable));

            }

        }
    }

    public boolean isGpsEnable() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);

        snackbar.setAction(getString(R.string.enable), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToGpsSetting();

                    }
                })
                .setActionTextColor(requireContext().getColor(R.color.blue_semi_dark))
                .setTextColor(requireContext().getColor(R.color.blue_dark))
                .setBackgroundTint(requireContext().getColor(R.color.gray2))
                .show();

    }

    private void goToGpsSetting() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);

    }

}