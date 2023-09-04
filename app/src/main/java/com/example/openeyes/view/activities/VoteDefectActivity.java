package com.example.openeyes.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivityVoteDefectBinding;

public class VoteDefectActivity extends AppCompatActivity {

    private ActivityVoteDefectBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_defect);

        binding.viewTxtLayoutVoteDefectDescription.setOnClickListener(null);
        binding.toolbarVoteDefect.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

    }
}