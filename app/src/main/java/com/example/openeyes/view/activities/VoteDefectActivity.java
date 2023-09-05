package com.example.openeyes.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivityVoteDefectBinding;
import com.example.openeyes.recorder.AndroidAudioPlayer;
import com.example.openeyes.utility.MySharedPreferences;
import com.example.openeyes.utility.SnackBarHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VoteDefectActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityVoteDefectBinding binding;
    private String defectEmail = null;
    private String defectUuid = null;
    private StorageReference fStorage;
    private DatabaseReference fDatabase;
    private ValueEventListener valueEventListener1;
    private ValueEventListener valueEventListener2;
    private ArrayList<SlideModel> imageList = new ArrayList<>();
    private boolean isDownloadCancelled = false;
    private AndroidAudioPlayer player;
    private boolean isPlaying = false;
    private String audioUrl = null;
    private int countDownTimer = 0;
    private int numberOfImage = 0;
    private int addedImages = 0;
    private float rate = 0;
    private int likes = 0;
    private String howIsVoting = null;
    MySharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_defect);

        sharedPreferences = new MySharedPreferences(this);
        howIsVoting = sharedPreferences.getEncodedUserEmail();

        defectEmail = getIntent().getStringExtra("email");
        defectUuid = getIntent().getStringExtra("uuid");

        player = new AndroidAudioPlayer(this);

        binding.lottieAudioWaveVoteDefect.pauseAnimation();
        binding.viewTxtLayoutVoteDefectDescription.setOnClickListener(null);
        binding.constLayoutVoteDefectLoading.setOnClickListener(null);
        binding.imgPlayPauseAudioVoteDefect.setOnClickListener(this);
        binding.btnVoteDefect.setOnClickListener(this);
        binding.toolbarVoteDefect.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        binding.txtVoteDefectShow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (numberOfImage != 0 && addedImages == numberOfImage) {
                    binding.imgSliderVoteDefect.setImageList(imageList);
                    binding.constLayoutVoteDefectLoading.setVisibility(View.GONE);

                }
            }
        });

        fStorage = FirebaseStorage.getInstance().getReference();
        fDatabase = FirebaseDatabase.getInstance().getReference();

        fDatabase.child("Defect").child(defectEmail).child(defectUuid).addValueEventListener(valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.txtVoteDefectLocation.setText(snapshot.child("location").getValue(String.class));
                    binding.txtVoteDefectPerson.setText(snapshot.child("nickName").getValue(String.class));
                    binding.txtVoteDefectDate.setText(snapshot.child("date").getValue(String.class));
                    binding.txtVoteDefectCategory.setText(snapshot.child("category").getValue(String.class));
                    binding.edtVoteDefectDescription.setText(snapshot.child("description").getValue(String.class));
                    binding.txtVoteDefectNumber.setText("Votes: " + snapshot.child("likes").getValue(Integer.class));
                    rate = snapshot.child("rate").getValue(Float.class);
                    likes = snapshot.child("likes").getValue(Integer.class);

                    int haveImage = 0;
                    int haveAudio = 0;
                    haveImage = snapshot.child("haveImage").getValue(Integer.class);
                    haveAudio = snapshot.child("haveAudio").getValue(Integer.class);
                    numberOfImage = haveImage;

                    // Checking for image
                    if (haveImage != 0) {
                        for (int i = 0; i < haveImage; i++) {
                            fStorage.child(defectEmail).child(defectUuid).child("images").child("image" + i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (!isDownloadCancelled) {
                                        imageList.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
                                        addedImages++;
                                        binding.txtVoteDefectShow.setText(addedImages + "");

                                    }
                                }
                            });
                        }

                    } else {
                        binding.cardViewVoteDefectSlider.setVisibility(View.GONE);
                        binding.constLayoutVoteDefectLoading.setVisibility(View.GONE);

                    }

                    // Checking for audio
                    if (haveAudio != 0) {
                        fStorage.child(defectEmail).child(defectUuid).child("audio").child("audioFile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                audioUrl = uri.toString();
                                binding.txtAudioRecorderTimeVoteDefect.setText(prepareTimeText(getAudioDurationInSeconds(audioUrl)));

                            }
                        });

                    } else {
                        binding.constLayoutVoteDefectRecordAudio.setVisibility(View.GONE);

                    }

                } else {
                    binding.constLayoutVoteDefectLoading.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                binding.constLayoutVoteDefectLoading.setVisibility(View.GONE);

            }
        });

        fDatabase.child("UserDefect").child(defectUuid).child(howIsVoting).addValueEventListener(valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User has already voted
                    binding.ratingBarVoteDefect.setVisibility(View.GONE);
                    binding.btnVoteDefect.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /****************************************************/

    @Override
    protected void onPause() {
        super.onPause();
        fDatabase.removeEventListener(valueEventListener1);
        fDatabase.removeEventListener(valueEventListener2);
        isDownloadCancelled = true;

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.imgPlayPauseAudioVoteDefect.getId()) {
            if (!isPlaying) {
                if (audioUrl != null) {
                    isPlaying = true;
                    binding.imgPlayPauseAudioVoteDefect.setImageDrawable(getDrawable(R.drawable.svg_pause_circle));
                    binding.lottieAudioWaveVoteDefect.playAnimation();
                    player.playUrl(audioUrl);
                    runTimer();

                }
            } else {
                isPlaying = false;
                binding.imgPlayPauseAudioVoteDefect.setImageDrawable(getDrawable(R.drawable.svg_play_circle));
                binding.lottieAudioWaveVoteDefect.pauseAnimation();
                player.stop();

            }
        } else if (view.getId() == binding.btnVoteDefect.getId()) {
            if (binding.ratingBarVoteDefect.getNumStars() != 0) {
                binding.constLayoutVoteDefectLoading.setVisibility(View.VISIBLE);
                Map<String, Object> updates = new HashMap<>();
                likes++;
                rate = calculateRate(rate, likes, binding.ratingBarVoteDefect.getNumStars());
                updates.put("likes", likes);
                updates.put("rate", rate);
                fDatabase.child("Defect").child(defectEmail).child(defectUuid).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        fDatabase.child("UserDefect").child(defectUuid).child(defectEmail).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                binding.constLayoutVoteDefectLoading.setVisibility(View.GONE);
                                finish();

                            }
                        });
                    }
                });

            } else {
                SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.submit_vote_error));

            }
        }

    }

    public int getAudioDurationInSeconds(String audioUrl) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(audioUrl, new HashMap<String, String>());

        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        try {
            retriever.release();

        } catch (IOException e) {
            throw new RuntimeException(e);

        }

        if (duration != null) {
            long durationMillis = Long.parseLong(duration);
            // Convert duration from milliseconds to seconds
            return (int) (durationMillis / 1000);

        }

        return 0; // Default to 0 seconds if duration retrieval fails

    }

    private String prepareTimeText(int time) {
        int min = (time % 3600) / 60;
        int sec = time % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec);

    }

    private void runTimer() {
        Handler handler = new Handler();
        countDownTimer = getAudioDurationInSeconds(audioUrl);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isPlaying)
                    return;

                binding.txtAudioRecorderTimeVoteDefect.setText(prepareTimeText(countDownTimer));

                if (--countDownTimer >= 0 && isPlaying) {
                    handler.postDelayed(this, 1000L);
                    return;

                }

                binding.lottieAudioWaveVoteDefect.pauseAnimation();
                isPlaying = false;
                binding.txtAudioRecorderTimeVoteDefect.setText(prepareTimeText(getAudioDurationInSeconds(audioUrl)));

            }
        }, 0L);

    }

    private float calculateRate(float previousRate, int numberOfLikes, int numberOfGivenStars) {
        float newRate = ((previousRate * numberOfLikes) + numberOfGivenStars) / (numberOfLikes + 1);
        return Float.parseFloat(String.format("%.1f", newRate));

    }

}