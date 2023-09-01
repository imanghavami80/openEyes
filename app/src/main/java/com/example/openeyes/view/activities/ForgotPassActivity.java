package com.example.openeyes.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivityForgotPassBinding;
import com.example.openeyes.utility.SnackBarHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class ForgotPassActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityForgotPassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pass);

        binding.btnSendLink.setOnClickListener(this);
        binding.constLayoutForgotPasswordProgress.setOnClickListener(null);

    }

    /****************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.btnSendLink.getId()) {
            checkErrorAndAction();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        goToSignInActivity();

    }

    private void goToSignInActivity() {
        Intent intent = new Intent(ForgotPassActivity.this, SignInActivity.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    private boolean checkErrorEdtForgotPasswordEmail() {
        if (Objects.requireNonNull(binding.edtForgotPasswordEmail.getText()).toString().trim().isEmpty()) {
            binding.txtLayoutForgotPasswordEmail.setError(getString(R.string.error_empty_field));
            return false;

        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(binding.edtForgotPasswordEmail.getText()).toString().trim()).matches()) {
                binding.txtLayoutForgotPasswordEmail.setError(null);
                return true;

            } else {
                binding.txtLayoutForgotPasswordEmail.setError(getString(R.string.error_email_format));
                return false;

            }
        }
    }

    private void handleError() {
        binding.edtForgotPasswordEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.txtLayoutForgotPasswordEmail.setError(getString(R.string.error_empty_field));

                } else {
                    binding.txtLayoutForgotPasswordEmail.setError(null);

                }
            }
        });
    }

    private void checkErrorAndAction() {
        handleError();

        if (checkErrorEdtForgotPasswordEmail()) {
            // Set progress bar visible.
            binding.constLayoutForgotPasswordProgress.setVisibility(View.VISIBLE);

            // Getting the entered email by user.
            String enteredEmail = binding.edtForgotPasswordEmail.getText().toString().trim();

            FirebaseAuth fAuth = FirebaseAuth.getInstance();

            fAuth.sendPasswordResetEmail(enteredEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.reset_link_sent));
                        binding.constLayoutForgotPasswordProgress.setVisibility(View.GONE);

                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseNetworkException) {
                            SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                            binding.constLayoutForgotPasswordProgress.setVisibility(View.GONE);

                        } else {
                            SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_reset_link_not_sent));
                            binding.constLayoutForgotPasswordProgress.setVisibility(View.GONE);

                        }
                    }
                }
            });
        }
    }

}