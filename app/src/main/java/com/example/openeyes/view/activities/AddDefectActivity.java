package com.example.openeyes.view.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.openeyes.R;
import com.example.openeyes.adapter.AddDefectImagesAdapter;
import com.example.openeyes.databinding.ActivityAddDefectBinding;
import com.example.openeyes.model.Defect;
import com.example.openeyes.recorder.AndroidAudioPlayer;
import com.example.openeyes.recorder.AndroidAudioRecorder;
import com.example.openeyes.utility.Constants;
import com.example.openeyes.utility.MySharedPreferences;
import com.example.openeyes.utility.PermissionManager;
import com.example.openeyes.utility.SnackBarHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddDefectActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAddDefectBinding binding;
    private MySharedPreferences mySharedPreferences;
    private PermissionManager permissionManager;
    private AndroidAudioRecorder recorder;
    private AndroidAudioPlayer player;
    private File audioFile = null;
    private boolean isRecording, isPlaying = false;
    private int counter, countDownTimer = 0;
    private ArrayList<Uri> itemsDefectImages = new ArrayList<>();
    private Uri capturedImageUri;
    private double lat, lon = 0.0;
    private String randomId = UUID.randomUUID().toString();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_defect);

        recorder = new AndroidAudioRecorder(this);
        player = new AndroidAudioPlayer(this);

        mySharedPreferences = new MySharedPreferences(this);

        setCategory();
        binding.lottieAudioWave.pauseAnimation();
        binding.imgRecordStopAudio.setOnClickListener(this);
        binding.imgPlayAudio.setOnClickListener(this);
        binding.imgPauseAudio.setOnClickListener(this);
        binding.viewTxtLayoutAddDefectAddress.setOnClickListener(this);
        binding.txtBtnUploadDefectPhoto.setOnClickListener(this);
        binding.btnAddDefect.setOnClickListener(this);
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
                    permissionManager.requestRecordAudioPermission();
                    permissionManager.requestCameraPermission();

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
                    binding.imgPlayAudio.setEnabled(false);
                    binding.imgPauseAudio.setEnabled(false);
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
                    binding.imgPlayAudio.setEnabled(true);
                    binding.imgPauseAudio.setEnabled(true);
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

        } else if (view.getId() == binding.viewTxtLayoutAddDefectAddress.getId()) {
            goToGetLocationActivity();

        } else if (view.getId() == binding.txtBtnUploadDefectPhoto.getId()) {
            if (permissionManager.hasCameraPermission()) {
                if (itemsDefectImages.size() < 3) {
                    takePhoto();

                } else {
                    SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.max_image_uploaded));

                }
            } else {
                permissionManager.requestCameraPermission();

            }
        } else if (view.getId() == binding.btnAddDefect.getId()) {
            checkErrorAndAction();

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

                if (++counter < 61 && isRecording) {
                    handler.postDelayed(this, 1000L);
                    return;

                }

                binding.imgRecordStopAudio.callOnClick();

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

    private void goToGetLocationActivity() {
        Intent intent = new Intent(AddDefectActivity.this, GetLocationActivity.class);
        someActivityResultLauncherForMap.launch(intent);

    }

    private void getLatLon(Intent data) {
        lat = data.getDoubleExtra(Constants.DEFECT_LATITUDE, 0.0);
        lon = data.getDoubleExtra(Constants.DEFECT_LONGITUDE, 0.0);
        binding.edtAddDefectAddress.setText(data.getStringExtra(Constants.DEFECT_ADDRESS));

    }

    private void initRecyclerDefectImages(ArrayList<Uri> items) {
        AddDefectImagesAdapter addDefectImagesAdapter = new AddDefectImagesAdapter(this, items);
        binding.recyclerUploadDefectPhoto.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerUploadDefectPhoto.setAdapter(addDefectImagesAdapter);

    }

    private void createImageDataForUpload(Uri uri) {
        itemsDefectImages.add(uri);
        initRecyclerDefectImages(itemsDefectImages);

    }

    private void takePhoto() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "defectImage");
        values.put(MediaStore.Images.Media.DESCRIPTION, "fromTheCamera");
        capturedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        someActivityResultLauncherForCamera.launch(intent);

    }

    private ActivityResultLauncher<Intent> someActivityResultLauncherForMap = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        getLatLon(result.getData());

                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> someActivityResultLauncherForCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK /* && result.getData() != null*/) {
                        createImageDataForUpload(capturedImageUri);

                    }
                }
            }
    );

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(new Date());

    }

    private boolean checkErrorEdtAddDefectAddress() {
        if (Objects.requireNonNull(binding.edtAddDefectAddress.getText()).toString().trim().isEmpty()) {
            binding.txtLayoutAddDefectAddress.setError(getString(R.string.error_empty_field));
            return false;

        } else {
            binding.txtLayoutAddDefectAddress.setError(null);
            return true;

        }
    }

    private boolean checkErrorAutoTextAddDefectCategory() {
        String[] categories = getResources().getStringArray(R.array.category);
        String selectedCategory = binding.autoTextAddDefectCategory.getText().toString().trim();

        if (selectedCategory.isEmpty()) {
            binding.spinnerAddDefectCategory.setError(getString(R.string.error_empty_field));
            return false;

        }

        for (String category : categories) {
            if (category.equals(selectedCategory)) {
                binding.spinnerAddDefectCategory.setError(null);
                return true;

            }
        }

        binding.spinnerAddDefectCategory.setError(getString(R.string.error_category_not_accepted));
        return false;

    }

    private boolean checkErrorEdtAddDefectDescription() {
        if (Objects.requireNonNull(binding.edtAddDefectDescription.getText()).toString().trim().isEmpty()) {
            binding.txtLayoutAddDefectDescription.setError(getString(R.string.error_empty_field));
            return false;

        } else {
            binding.txtLayoutAddDefectDescription.setError(null);
            return true;

        }
    }

    private void handleError() {

        binding.edtAddDefectAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.txtLayoutAddDefectAddress.setError(getString(R.string.error_empty_field));

                } else {
                    binding.txtLayoutAddDefectAddress.setError(null);

                }
            }
        });

        binding.edtAddDefectDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.txtLayoutAddDefectDescription.setError(getString(R.string.error_empty_field));

                } else {
                    binding.txtLayoutAddDefectDescription.setError(null);

                }
            }
        });

        binding.autoTextAddDefectCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.spinnerAddDefectCategory.setError(getString(R.string.error_empty_field));

                } else {
                    binding.spinnerAddDefectCategory.setError(null);

                }
            }
        });

    }

    private void checkErrorAndAction() {
        handleError();

        if (checkErrorEdtAddDefectAddress() && checkErrorAutoTextAddDefectCategory() && checkErrorEdtAddDefectDescription()) {
            // Set uploading view visible.
            binding.constLayoutAddDefectUploading.setVisibility(View.VISIBLE);
            binding.btnAddDefect.setEnabled(false);

            // Get all defect information.
            String defectAddress = binding.edtAddDefectAddress.getText().toString().trim();
            String defectCategory = binding.autoTextAddDefectCategory.getText().toString().trim();
            String defectDescription = binding.edtAddDefectDescription.getText().toString().trim();
            String defectUUID = randomId;
            String defectDate = getCurrentDate();
            String userEmail = mySharedPreferences.getEncodedUserEmail();

            StorageReference fStorage = FirebaseStorage.getInstance().getReference(userEmail);
            DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("Defect");

            // Saving other details.
            Defect newDefect = new Defect(defectUUID, defectAddress, lat, lon, defectCategory, defectDescription, defectDate, 0, 0.0f);

            // We have to store the data level by level. Others -(if successful)-> audio -(if successful)-> images.
            fDatabase.child(userEmail).setValue(newDefect).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        // Saving the audio.
                        if (counter != 0) {
                            // We have an recorded audio file...
                            fStorage.child(defectUUID).child("audio").child("audioFile").putFile(Uri.fromFile(audioFile)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        // Saving the images.
                                        for (int i = 0; i < itemsDefectImages.size(); i++) {
                                            fStorage.child(defectUUID).child("images").child("image" + i).putFile(itemsDefectImages.get(0)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        binding.constLayoutAddDefectUploading.setVisibility(View.GONE);
                                                        binding.btnAddDefect.setEnabled(true);
                                                        finish();

                                                    } else {
                                                        SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                                                        binding.constLayoutAddDefectUploading.setVisibility(View.GONE);
                                                        binding.btnAddDefect.setEnabled(true);

                                                    }
                                                }
                                            });
                                        }

                                    } else {
                                        SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                                        binding.constLayoutAddDefectUploading.setVisibility(View.GONE);
                                        binding.btnAddDefect.setEnabled(true);

                                    }
                                }
                            });
                        }

                    } else {
                        SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                        binding.constLayoutAddDefectUploading.setVisibility(View.GONE);
                        binding.btnAddDefect.setEnabled(true);

                    }
                }
            });

        }
    }

}