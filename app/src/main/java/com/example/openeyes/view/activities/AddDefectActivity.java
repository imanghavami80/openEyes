package com.example.openeyes.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivityAddDefectBinding;

public class AddDefectActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAddDefectBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_defect);

        binding.toolbarAddDefect.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

    }

    /****************************************************/

    @Override
    public void onClick(View view) {

    }

}