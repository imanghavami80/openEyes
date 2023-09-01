package com.example.openeyes.view.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.openeyes.R;
import com.example.openeyes.adapter.ReportedDefectAdapter;
import com.example.openeyes.databinding.FragmentDefectsBinding;
import com.example.openeyes.model.Defect;
import com.example.openeyes.utility.SnackBarHandler;
import com.example.openeyes.view.activities.AddDefectActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DefectsFragment extends Fragment implements View.OnClickListener {

    private FragmentDefectsBinding binding;
    private ArrayList<Defect> itemsDefect = new ArrayList<>();
    private ArrayList<String> itemsDefectImages = new ArrayList<>();
    private StorageReference fStorage;
    private DatabaseReference fDatabase;
    private ValueEventListener valueEventListener1;
    private ValueEventListener valueEventListener2;
    private int numberOfReportedDefects = 0;
    private int numberOfGottenDefects = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_defects, container, false);

        binding.fabAddDefect.setOnClickListener(this);
        binding.constLayoutListView.setOnClickListener(null);

        binding.txtReadyShowRecycler.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().equals(Integer.toString(numberOfReportedDefects))) {
                    initDefectsRecycler(itemsDefect, itemsDefectImages);
                    binding.constLayoutListView.setVisibility(View.GONE);

                }
            }
        });

        fStorage = FirebaseStorage.getInstance().getReference();
        fDatabase = FirebaseDatabase.getInstance().getReference("Defect");

//        getData();

        return binding.getRoot();

    }

    /****************************************************/

    @Override
    public void onPause() {
        super.onPause();
        fDatabase.removeEventListener(valueEventListener1);
        fDatabase.removeEventListener(valueEventListener2);
        itemsDefect.clear();
        itemsDefectImages.clear();
        numberOfReportedDefects = 0;
        numberOfGottenDefects = 0;

    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.fabAddDefect.getId()) {
            goToAddDefectActivity();

        }
    }

    private ActivityResultLauncher<Intent> someActivityResultLauncherForAddDefect = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        getData();

                    }
                }
            }
    );

    private void initDefectsRecycler(ArrayList<Defect> reportedDefects, ArrayList<String> reportedDefectsImages) {
        ReportedDefectAdapter adapter = new ReportedDefectAdapter(requireContext(), reportedDefects, reportedDefectsImages);
        binding.recyclerDefects.setAdapter(adapter);
        binding.recyclerDefects.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

    }

    private void goToAddDefectActivity() {
        Intent intent = new Intent(requireContext(), AddDefectActivity.class);
//        someActivityResultLauncherForAddDefect.launch(intent);
        startActivity(intent);
    }

    private void getData() {
        // For getting the number of reported defects.
        fDatabase.addValueEventListener(valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot1 : snapshot.getChildren()) {
                        if (childSnapshot1.exists()) {
                            for (DataSnapshot childSnapshot2 : childSnapshot1.getChildren()) {
                                if (childSnapshot2.exists()) {
                                    numberOfReportedDefects++;

                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // For getting the actual data.
        fDatabase.addValueEventListener(valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot1 : snapshot.getChildren()) {
                        if (childSnapshot1.exists()) {
                            String email = childSnapshot1.getKey();

                            for (DataSnapshot childSnapshot2 : childSnapshot1.getChildren()) {
                                if (childSnapshot2.exists()) {
                                    String uuid = childSnapshot2.getKey();

                                    Defect defect = new Defect(
                                            childSnapshot2.child("location").getValue(String.class),
                                            childSnapshot2.child("category").getValue(String.class),
                                            childSnapshot2.child("description").getValue(String.class),
                                            childSnapshot2.child("likes").getValue(Integer.class),
                                            childSnapshot2.child("rate").getValue(Float.class),
                                            childSnapshot2.child("haveImage").getValue(Integer.class),
                                            childSnapshot2.child("haveAudio").getValue(Integer.class)
                                    );
                                    itemsDefect.add(defect);

                                    if (defect.getHaveImage() != 0) {
                                        fStorage.child(email).child(uuid).child("images").child("image0").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                itemsDefectImages.add(uri.toString());
                                                numberOfGottenDefects++;
                                                binding.txtReadyShowRecycler.setText(String.format("%d", numberOfGottenDefects));

                                            }
                                        });

                                    } else {
                                        fStorage.child("imageHolder.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                itemsDefectImages.add(uri.toString());
                                                numberOfGottenDefects++;
                                                binding.txtReadyShowRecycler.setText(String.format("%d", numberOfGottenDefects));

                                            }
                                        });
//                                        String imageHolderUrl = "https://user-images.githubusercontent.com/2351721/31314483-7611c488-ac0e-11e7-97d1-3cfc1c79610e.png";
//                                        itemsDefectImages.add(imageHolderUrl);
//                                        numberOfGottenDefects++;
//                                        binding.txtReadyShowRecycler.setText(String.format("%d", numberOfGottenDefects));

                                    }

                                }
                            }
                        }
                    }
                } else {
                    binding.constLayoutListView.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SnackBarHandler.snackBarHideAction3(requireContext(), binding.getRoot(), getString(R.string.error_occurred));
                binding.constLayoutListView.setVisibility(View.GONE);
            }
        });

    }

}