package com.example.openeyes.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivityHomeBinding;
import com.example.openeyes.view.fragments.AddReportFragment;
import com.example.openeyes.view.fragments.HomeFragment;
import com.example.openeyes.view.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        getSupportFragmentManager().beginTransaction().replace(R.id.constLayoutHome, new HomeFragment()).commit();

        binding.bottomNavigationHome.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                if (item.getItemId() == R.id.homePage) {
                    fragment = new HomeFragment();

                } else if (item.getItemId() == R.id.addReportPage) {
                    fragment = new AddReportFragment();

                } else if (item.getItemId() == R.id.profilePage) {
                    fragment = new ProfileFragment();

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.constLayoutHome, fragment).commit();

                return true;

            }
        });
    }
}