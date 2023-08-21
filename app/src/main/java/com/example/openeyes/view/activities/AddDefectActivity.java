package com.example.openeyes.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivityAddDefectBinding;
import com.example.openeyes.utility.PermissionManager;
import com.example.openeyes.utility.SnackBarHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddDefectActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAddDefectBinding binding;
    private PermissionManager permissionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_defect);

        binding.lottieAudioWave.pauseAnimation();
        binding.imgRecordStopAudio.setOnClickListener(this);
        binding.toolbarAddDefect.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        permissionManager = new PermissionManager(AddDefectActivity.this, new EasyPermissions.PermissionCallbacks() {
            @Override
            public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

            }

            @Override
            public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
                if (EasyPermissions.somePermissionPermanentlyDenied(AddDefectActivity.this, perms)) {
                    new AppSettingsDialog.Builder(AddDefectActivity.this).build().show();
                } else {
                    permissionManager.requestLocationPermissions();

                }
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

            }
        });

    }

    /****************************************************/

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        if (view.getId() == binding.imgRecordStopAudio.getId()) {
            if (permissionManager.hasRecordAudioPermission()) {

            } else {
                permissionManager.requestRecordAudioPermission();

            }
        }

    }


}