package com.example.openeyes.view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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
import com.example.openeyes.model.Defect2;
import com.example.openeyes.utility.SnackBarHandler;
import com.example.openeyes.view.activities.AddDefectActivity;
import com.example.openeyes.view.activities.VoteDefectActivity;
import com.google.android.gms.tasks.OnFailureListener;
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
    private ArrayList<Defect2> itemsDefect = new ArrayList<>();
    private StorageReference fStorage;
    private DatabaseReference fDatabase;
    private ValueEventListener valueEventListener1;
    private ValueEventListener valueEventListener2;
    private int numberOfReportedDefects = 0;
    private int numberOfGottenDefects = 0;
    private boolean showRecycler = false;
    private boolean isDownloadCancelled = false;
    private ReportedDefectAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                    if (showRecycler) {
                        initDefectsRecycler(itemsDefect);
                        binding.constLayoutListView.setVisibility(View.GONE);

                    } else {
                        getImageData();

                    }

                }
            }
        });

        fStorage = FirebaseStorage.getInstance().getReference();
        fDatabase = FirebaseDatabase.getInstance().getReference("Defect");

        return binding.getRoot();

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
        if (adapter != null) {
            adapter.clearData();
            adapter = null;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.constLayoutListView.setVisibility(View.VISIBLE);
        binding.constLayoutNullDefects.setVisibility(View.GONE);
        isDownloadCancelled = false;

        getTextData();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.fabAddDefect.getId()) {
            goToAddDefectActivity();

        }
    }

    private void initDefectsRecycler(ArrayList<Defect2> reportedDefects) {
        adapter = new ReportedDefectAdapter(requireContext(), reportedDefects, new ReportedDefectAdapter.OnItemClickListener() {
            @Override
            public void onHoleItemClicked(String defectUuid, String defectEmail) {
                Intent intent = new Intent(requireContext(), VoteDefectActivity.class);
                intent.putExtra("uuid", defectUuid);
                intent.putExtra("email", defectEmail);
                startActivity(intent);
                requireActivity().overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                );

            }
        });
        binding.recyclerDefects.setAdapter(adapter);
        binding.recyclerDefects.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

    }

    private void goToAddDefectActivity() {
        Intent intent = new Intent(requireContext(), AddDefectActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );

    }

    private void getTextData() {
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
                SnackBarHandler.snackBarHideAction3(requireContext(), binding.getRoot(), getString(R.string.error_occurred));
                binding.constLayoutListView.setVisibility(View.GONE);

            }
        });

        // For getting the actual text data.
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

                                    Defect2 defect = new Defect2(
                                            childSnapshot2.child("location").getValue(String.class),
                                            childSnapshot2.child("category").getValue(String.class),
                                            childSnapshot2.child("description").getValue(String.class),
                                            childSnapshot2.child("likes").getValue(Integer.class),
                                            childSnapshot2.child("rate").getValue(Float.class),
                                            childSnapshot2.child("haveImage").getValue(Integer.class),
                                            childSnapshot2.child("haveAudio").getValue(Integer.class),
                                            uuid,
                                            email
                                    );
                                    itemsDefect.add(defect);

                                    numberOfGottenDefects++;
                                    binding.txtReadyShowRecycler.setText(numberOfGottenDefects + "");

                                }
                            }
                        }
                    }
                } else {
                    binding.constLayoutListView.setVisibility(View.GONE);
                    binding.constLayoutNullDefects.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SnackBarHandler.snackBarHideAction3(requireContext(), binding.getRoot(), getString(R.string.error_occurred));
                binding.constLayoutListView.setVisibility(View.GONE);
            }
        });

    }

    private void getImageData() {
        numberOfGottenDefects = 0;
        binding.txtReadyShowRecycler.setText(numberOfGottenDefects + "");
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
                            binding.txtReadyShowRecycler.setText(numberOfGottenDefects + "");

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        SnackBarHandler.snackBarHideAction3(requireContext(), binding.getRoot(), getString(R.string.error_occurred));
                        binding.constLayoutListView.setVisibility(View.GONE);

                    }
                });

            } else {
                // We don't have image
                String imageHolderUrl = "https://user-images.githubusercontent.com/2351721/31314483-7611c488-ac0e-11e7-97d1-3cfc1c79610e.png";
                item.setFirstImage(imageHolderUrl);
                itemsDefect.set(i, item);
                numberOfGottenDefects++;
                binding.txtReadyShowRecycler.setText(numberOfGottenDefects + "");

            }
        }

    }

}