package com.example.openeyes.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

    }
}