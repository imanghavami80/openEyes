package com.example.openeyes.utility;

import android.Manifest;
import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.example.openeyes.R;

import pub.devrel.easypermissions.EasyPermissions;

public class PermissionManager {

    // Fields
    private Activity activity;
    private Fragment fragment;
    private EasyPermissions.PermissionCallbacks permissionCallback;

    // Constructors
    public PermissionManager(Activity activity, EasyPermissions.PermissionCallbacks permissionCallback) {
        this.activity = activity;
        this.permissionCallback = permissionCallback;

    }

    public PermissionManager(Fragment fragment, EasyPermissions.PermissionCallbacks permissionCallback) {
        this.fragment = fragment;
        this.permissionCallback = permissionCallback;

    }

    // Methods
    public boolean hasLocationPermission() {
        if (this.activity != null) {
            return EasyPermissions.hasPermissions(
                    this.activity.getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            );

        } else if (this.fragment != null) {
            return EasyPermissions.hasPermissions(
                    this.fragment.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            );

        }

        return false;

    }

    public void requestLocationPermissions() {
        if (this.activity != null) {
            EasyPermissions.requestPermissions(
                    this.activity,
                    this.activity.getString(R.string.need_permission),
                    Constants.LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            );

        } else if (this.fragment != null) {
            EasyPermissions.requestPermissions(
                    this.fragment,
                    this.fragment.getString(R.string.need_permission),
                    Constants.LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            );

        }
    }

    public boolean hasRecordAudioPermission() {
        if (this.activity != null) {
            return EasyPermissions.hasPermissions(
                    this.activity.getApplicationContext(),
                    Manifest.permission.RECORD_AUDIO
            );

        } else if (this.fragment != null) {
            return EasyPermissions.hasPermissions(
                    this.fragment.requireContext(),
                    Manifest.permission.RECORD_AUDIO
            );

        }

        return false;

    }

    public void requestRecordAudioPermission() {
        if (this.activity != null) {
            EasyPermissions.requestPermissions(
                    this.activity,
                    this.activity.getString(R.string.need_permission),
                    Constants.RECORD_AUDIO_PERMISSION_REQUEST_CODE,
                    Manifest.permission.RECORD_AUDIO
            );

        } else if (this.fragment != null) {
            EasyPermissions.requestPermissions(
                    this.fragment,
                    this.fragment.getString(R.string.need_permission),
                    Constants.RECORD_AUDIO_PERMISSION_REQUEST_CODE,
                    Manifest.permission.RECORD_AUDIO
            );

        }
    }

    public boolean hasCameraPermission() {
        if (this.activity != null) {
            return EasyPermissions.hasPermissions(
                    this.activity.getApplicationContext(),
                    Manifest.permission.CAMERA
            );

        } else if (this.fragment != null) {
            return EasyPermissions.hasPermissions(
                    this.fragment.requireContext(),
                    Manifest.permission.CAMERA
            );

        }

        return false;

    }

    public void requestCameraPermission() {
        if (this.activity != null) {
            EasyPermissions.requestPermissions(
                    this.activity,
                    this.activity.getString(R.string.need_permission),
                    Constants.CAMERA_PERMISSION_REQUEST_CODE,
                    Manifest.permission.CAMERA
            );

        } else if (this.fragment != null) {
            EasyPermissions.requestPermissions(
                    this.fragment,
                    this.fragment.getString(R.string.need_permission),
                    Constants.CAMERA_PERMISSION_REQUEST_CODE,
                    Manifest.permission.CAMERA
            );

        }
    }

}
