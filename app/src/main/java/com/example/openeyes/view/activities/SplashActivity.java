package com.example.openeyes.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivitySplashBinding;
import com.example.openeyes.utility.Constants;
import com.example.openeyes.utility.MySharedPreferences;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySplashBinding binding;
    private MySharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        mySharedPreferences = new MySharedPreferences(this);
        if (mySharedPreferences.isLoggedIn()) {
            binding.constLayoutSplash2.setVisibility(View.INVISIBLE);

            new Handler().postDelayed(() -> {
                goToHomeActivity();

            }, Constants.SPLASH_DISPLAY_LENGTH);

        }

        binding.btnSplashSignIn.setOnClickListener(this);
        binding.btnSplashSignUp.setOnClickListener(this);

    }

    /****************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.btnSplashSignIn.getId()) {
            goToSignInActivity();

        } else if (view.getId() == binding.btnSplashSignUp.getId()) {
            goToSignUpActivity();

        }
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(
                R.anim.fade_in_new,
                R.anim.fade_out_new
        );
        finish();

    }

    private void goToSignInActivity() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );
        finish();

    }

    private void goToSignUpActivity() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );
        finish();

    }

}