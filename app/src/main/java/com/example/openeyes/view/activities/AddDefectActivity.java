package com.example.openeyes.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivityAddDefectBinding;
import com.example.openeyes.recorder.AndroidAudioPlayer;
import com.example.openeyes.recorder.AndroidAudioRecorder;
import com.example.openeyes.utility.PermissionManager;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddDefectActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAddDefectBinding binding;
    private PermissionManager permissionManager;
    private AndroidAudioRecorder recorder;
    private AndroidAudioPlayer player;
    private File audioFile = null;
    private boolean isRecording, isPlaying = false;
    private int counter, countDownTimer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_defect);

        recorder = new AndroidAudioRecorder(this);
        player = new AndroidAudioPlayer(this);

        setCategory();
        binding.lottieAudioWave.pauseAnimation();
        binding.imgRecordStopAudio.setOnClickListener(this);
        binding.imgPlayAudio.setOnClickListener(this);
        binding.imgPauseAudio.setOnClickListener(this);
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
                if (!isRecording) {
                    isRecording = true;
                    binding.lottieAudioWave.playAnimation();
                    binding.imgRecordStopAudio.setImageDrawable(getDrawable(R.drawable.svg_stop));

                    File file = new File(getCacheDir(), "audio.mp3");
                    try {
                        recorder.start(file);
                        setTimer();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    audioFile = file;

                } else {
                    isRecording = false;
                    binding.lottieAudioWave.pauseAnimation();
                    binding.imgRecordStopAudio.setImageDrawable(getDrawable(R.drawable.svg_record));

                    recorder.stop();

                }
            } else {
                permissionManager.requestRecordAudioPermission();

            }
        } else if (view.getId() == binding.imgPlayAudio.getId()) {
            if (audioFile != null) {
                isPlaying = true;
                binding.lottieAudioWave.playAnimation();
                player.playFile(audioFile);
                runTimer();

            }

        } else if (view.getId() == binding.imgPauseAudio.getId()) {
            isPlaying = false;
            binding.lottieAudioWave.pauseAnimation();
            player.stop();

        }

    }

    private void setCategory() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.item_category, getResources().getStringArray(R.array.category));
        binding.autoTextAddDefectCategory.setAdapter(categoryAdapter);

    }

    private void setTimer() {
        Handler handler = new Handler();
        counter = 0;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isRecording)
                    return;

                binding.txtAudioRecorderTime.setText(prepareTimeText(counter));

                if (++counter < 120 && isRecording) {
                    handler.postDelayed(this, 1000L);
                    return;

                }

                binding.lottieAudioWave.pauseAnimation();
                binding.imgRecordStopAudio.setImageDrawable(getDrawable(R.drawable.svg_stop));
                isRecording = false;

            }
        }, 0L);

    }

    private void runTimer() {
        Handler handler = new Handler();
        countDownTimer = counter;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isPlaying)
                    return;

                binding.txtAudioRecorderTime.setText(prepareTimeText(countDownTimer));

                if (--countDownTimer >= 0 && isPlaying) {
                    handler.postDelayed(this, 1000L);
                    return;

                }

                binding.lottieAudioWave.pauseAnimation();
                isPlaying = false;
                binding.txtAudioRecorderTime.setText(prepareTimeText(counter));

            }
        }, 0L);

    }

    private String prepareTimeText(int time) {
        int min = (time % 3600) / 60;
        int sec = time % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec);

    }

}