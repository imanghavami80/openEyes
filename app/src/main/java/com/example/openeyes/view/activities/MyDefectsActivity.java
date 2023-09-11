package com.example.openeyes.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.openeyes.R;
import com.example.openeyes.adapter.MyDefectsAdapter;
import com.example.openeyes.databinding.ActivityMyDefectsBinding;
import com.example.openeyes.model.Defect2;
import com.example.openeyes.utility.MySharedPreferences;
import com.example.openeyes.utility.SnackBarHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyDefectsActivity extends AppCompatActivity {

    private ActivityMyDefectsBinding binding;
    private MySharedPreferences sharedPreferences;
    private ArrayList<Defect2> itemsDefect = new ArrayList<>();
    private StorageReference fStorage;
    private DatabaseReference fDatabase;
    private ValueEventListener valueEventListener1;
    private ValueEventListener valueEventListener2;
    private int numberOfReportedDefects = 0;
    private int numberOfGottenDefects = 0;
    private boolean showRecycler = false;
    private boolean isDownloadCancelled = false;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_defects);

        sharedPreferences = new MySharedPreferences(this);
        userEmail = sharedPreferences.getEncodedUserEmail();
        binding.constLayoutMyDefectsLoading.setOnClickListener(null);
        binding.constLayoutNullMyDefects.setOnClickListener(null);
        binding.toolbarMyDefects.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        binding.txtReadyShowRecyclerMyDefects.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().equals(Integer.toString(numberOfReportedDefects))) {
                    if (showRecycler) {
                        initDefectsRecycler(itemsDefect);
                        binding.constLayoutMyDefectsLoading.setVisibility(View.GONE);

                    } else {
                        getImageData();

                    }

                }
            }
        });

        fStorage = FirebaseStorage.getInstance().getReference();
        fDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );

    }

    /****************************************************/


    @Override
    public void onPause() {
        super.onPause();
        fDatabase.removeEventListener(valueEventListener1);
        fDatabase.removeEventListener(valueEventListener2);
        showRecycler = false;
        isDownloadCancelled = true;
        itemsDefect.clear();
        numberOfReportedDefects = 0;
        numberOfGottenDefects = 0;

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.constLayoutMyDefectsLoading.setVisibility(View.VISIBLE);
        isDownloadCancelled = false;
        getTextData();

    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }


    private void initDefectsRecycler(ArrayList<Defect2> reportedDefects) {
        MyDefectsAdapter adapter = new MyDefectsAdapter(this, reportedDefects, new MyDefectsAdapter.OnItemClickListener() {
            @Override
            public void onDeleteItemClicked(String defectUuid, int haveImage, int haveAudio) {
                // Delete from defect table in real time database.
                fDatabase.child("Defect").child(userEmail).child(defectUuid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Delete from userDefect table in real time database.
                            fDatabase.child("UserDefect").child(defectUuid).removeValue();

                            // Delete other data like image and audio in storage.
                            if (haveAudio != 0) {
                                fStorage.child(userEmail).child(defectUuid).child("audio").child("audioFile").delete();

                            }

                            for (int i = 0; i < haveImage; i++) {
                                fStorage.child(userEmail).child(defectUuid).child("images").child("image" + i).delete();

                            }

                            // Deleting the folders in storage.
                            fStorage.child(userEmail).child(defectUuid).child("audio").delete();
                            fStorage.child(userEmail).child(defectUuid).child("images").delete();
                            fStorage.child(userEmail).child(defectUuid).delete();

                            SnackBarHandler.snackBarHideAction3(getApplicationContext(), binding.getRoot(), getString(R.string.defect_deleted));
                            reload();


                        } else {
                            SnackBarHandler.snackBarHideAction3(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));

                        }
                    }
                });
            }
        });
        binding.recyclerMyDefects.setAdapter(adapter);
        binding.recyclerMyDefects.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    private void getTextData() {
        // For getting the number of reported defects.
        fDatabase.child("Defect").child(userEmail).addValueEventListener(valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot1 : snapshot.getChildren()) {
                        if (childSnapshot1.exists()) {
                            numberOfReportedDefects++;

                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SnackBarHandler.snackBarHideAction3(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                binding.constLayoutMyDefectsLoading.setVisibility(View.GONE);

            }
        });

        // For getting the actual text data.
        fDatabase.child("Defect").child(userEmail).addValueEventListener(valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot1 : snapshot.getChildren()) {
                        if (childSnapshot1.exists()) {
                            String uuid = childSnapshot1.getKey();

                            Defect2 defect = new Defect2(
                                    childSnapshot1.child("location").getValue(String.class),
                                    childSnapshot1.child("category").getValue(String.class),
                                    childSnapshot1.child("description").getValue(String.class),
                                    childSnapshot1.child("likes").getValue(Integer.class),
                                    childSnapshot1.child("rate").getValue(Float.class),
                                    childSnapshot1.child("haveImage").getValue(Integer.class),
                                    childSnapshot1.child("haveAudio").getValue(Integer.class),
                                    uuid,
                                    userEmail
                            );
                            itemsDefect.add(defect);

                            numberOfGottenDefects++;
                            binding.txtReadyShowRecyclerMyDefects.setText(numberOfGottenDefects + "");

                        }
                    }
                } else {
                    binding.constLayoutMyDefectsLoading.setVisibility(View.GONE);
                    binding.constLayoutNullMyDefects.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SnackBarHandler.snackBarHideAction3(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                binding.constLayoutMyDefectsLoading.setVisibility(View.GONE);
            }
        });

    }

    private void getImageData() {
        numberOfGottenDefects = 0;
        binding.txtReadyShowRecyclerMyDefects.setText(numberOfGottenDefects + "");
        showRecycler = true;

        for (int i = 0; i < itemsDefect.size(); i++) {
            Defect2 item = itemsDefect.get(i);

            if (item.getHaveImage() != 0) {
                // We have image
                int witchItem = i;
                fStorage.child(item.getEmail()).child(item.getUuid()).child("images").child("image0").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!isDownloadCancelled) {
                            item.setFirstImage(uri.toString());
                            itemsDefect.set(witchItem, item);
                            numberOfGottenDefects++;
                            binding.txtReadyShowRecyclerMyDefects.setText(numberOfGottenDefects + "");

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        SnackBarHandler.snackBarHideAction3(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                        binding.constLayoutMyDefectsLoading.setVisibility(View.GONE);

                    }
                });

            } else {
                // We don't have image
                String imageHolderUrl = "https://user-images.githubusercontent.com/2351721/31314483-7611c488-ac0e-11e7-97d1-3cfc1c79610e.png";
                item.setFirstImage(imageHolderUrl);
                itemsDefect.set(i, item);
                numberOfGottenDefects++;
                binding.txtReadyShowRecyclerMyDefects.setText(numberOfGottenDefects + "");

            }
        }

    }

}