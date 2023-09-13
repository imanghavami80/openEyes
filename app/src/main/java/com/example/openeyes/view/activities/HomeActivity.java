package com.example.openeyes.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivityHomeBinding;
import com.example.openeyes.view.fragments.DefectsFragment;
import com.example.openeyes.view.fragments.MapFragment;
import com.example.openeyes.view.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private final String mapFragmentTag = "mapFragment";
    private final String defectsFragmentTag = "defectsFragment";
    private final String profileFragmentTag = "profileFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        Fragment mapFragment = new MapFragment();
        Fragment defectsFragment = new DefectsFragment();
        Fragment profileFragment = new ProfileFragment();

        makeCurrentFragment(mapFragment, mapFragmentTag);

        binding.bottomNavigationHome.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.reportedDefectsMapPage) {
                    makeCurrentFragment(mapFragment, mapFragmentTag);

                } else if (item.getItemId() == R.id.reportedDefectsListPage) {
                    makeCurrentFragment(defectsFragment, defectsFragmentTag);

                } else if (item.getItemId() == R.id.profilePage) {
                    makeCurrentFragment(profileFragment, profileFragmentTag);

                }

                return true;

            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showExitDialog();

    }

    private void makeCurrentFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in_new, R.anim.fade_out_new)
                .replace(R.id.constLayoutHome, fragment, tag)
                .commit();

    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.exit_app_dialog));
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();

        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}