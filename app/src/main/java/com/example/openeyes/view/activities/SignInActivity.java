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
import com.example.openeyes.databinding.ActivitySignInBinding;
import com.example.openeyes.model.User;
import com.example.openeyes.utility.Constants;
import com.example.openeyes.utility.MySharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private MySharedPreferences mySharedPreferences;
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        mySharedPreferences = new MySharedPreferences(this);

        binding.txtSignInToSignUp2.setOnClickListener(this);
        binding.txtSignInForgotPassword.setOnClickListener(this);
        binding.btnSignIn.setOnClickListener(this);

    }

    /****************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.txtSignInToSignUp2.getId()) {
            goToSignUpActivity();

        } else if (view.getId() == binding.txtSignInForgotPassword.getId()) {
            goToForgotPassActivity();

        } else if (view.getId() == binding.btnSignIn.getId()) {
            checkErrorAndAction();

        }
    }

    private void goToForgotPassActivity() {
        Intent intent = new Intent(SignInActivity.this, ForgotPassActivity.class);
        startActivity(intent);

        finish();

    }

    private void goToSignUpActivity() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);

        finish();

    }

    private void goToHomeActivity() {
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);

        finish();

    }

    private boolean checkErrorEdtSignInEmail() {
        if (Objects.requireNonNull(binding.edtSignInEmail.getText()).toString().trim().isEmpty()) {
            binding.txtLayoutSignInEmail.setError(getString(R.string.error_empty_field));
            return false;

        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(binding.edtSignInEmail.getText()).toString().trim()).matches()) {
                binding.txtLayoutSignInEmail.setError(null);
                return true;

            } else {
                binding.txtLayoutSignInEmail.setError(getString(R.string.error_email_format));
                return false;

            }
        }
    }

    private boolean checkErrorEdtSignInPassword() {
        if (Objects.requireNonNull(binding.edtSignInPassword.getText()).toString().trim().isEmpty()) {
            binding.txtLayoutSignInPassword.setError(getString(R.string.error_empty_field));
            return false;

        } else if (Objects.requireNonNull(binding.edtSignInPassword.getText()).toString().trim().length() < 6) {
            binding.txtLayoutSignInPassword.setError(getString(R.string.error_short_password));
            return false;

        } else {
            binding.txtLayoutSignInPassword.setError(null);
            return true;

        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);

        snackbar.setAction(getString(R.string.hide), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();

                    }
                })
                .setActionTextColor(getColor(R.color.blue_semi_dark))
                .setTextColor(getColor(R.color.blue_dark))
                .setBackgroundTint(getColor(R.color.gray2))
                .show();

    }

    private void handleError() {
        binding.edtSignInEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.txtLayoutSignInEmail.setError(getString(R.string.error_empty_field));

                } else {
                    binding.txtLayoutSignInEmail.setError(null);

                }
            }
        });

        binding.edtSignInPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.txtLayoutSignInPassword.setError(getString(R.string.error_empty_field));

                } else if (editable.toString().trim().length() < 6) {
                    binding.txtLayoutSignInPassword.setError(getString(R.string.error_short_password));

                } else {
                    binding.txtLayoutSignInPassword.setError(null);

                }
            }
        });
    }

    private void checkErrorAndAction() {
        handleError();

        if (checkErrorEdtSignInEmail() && checkErrorEdtSignInPassword()) {
            // Set progress bar visible.
            binding.progressBarSignIn.setVisibility(View.VISIBLE);

            // Getting what user has entered.
            String enteredEmail = binding.edtSignInEmail.getText().toString().trim();
            String enteredPassword = Objects.requireNonNull(binding.edtSignInPassword.getText()).toString().trim();

            // We have to encode the email into a safe key format (. -> , && @ -> -).
            String encodedEmail = enteredEmail.replace(".", ",").replace("@", "-");

            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            DatabaseReference fReference = FirebaseDatabase.getInstance().getReference("User");

            fAuth.signInWithEmailAndPassword(enteredEmail, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // We have to get all user information to show it to save it in shared preferences.
                        Query checkUser = fReference.orderByChild(Constants.USER_EMAIL).equalTo(encodedEmail);
                        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String fullNameFromDB = snapshot.child(encodedEmail).child(Constants.USER_FULL_NAME).getValue(String.class);
                                    String nickNameFromDB = snapshot.child(encodedEmail).child(Constants.USER_NICK_NAME).getValue(String.class);
                                    String emailFromDB = snapshot.child(encodedEmail).child(Constants.USER_EMAIL).getValue(String.class);

                                    User user = new User(fullNameFromDB, nickNameFromDB, emailFromDB);

                                    mySharedPreferences.setUserInfo(user);
                                    mySharedPreferences.setLoggedIn(true);

                                    binding.progressBarSignIn.setVisibility(View.GONE);

                                    goToHomeActivity();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                showSnackBar(getString(R.string.error_occurred));
                                binding.progressBarSignIn.setVisibility(View.GONE);

                            }
                        });

                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseNetworkException) {
                            showSnackBar(getString(R.string.error_occurred));

                        } else {
                            showSnackBar(getString(R.string.error_wrong_user_info));

                        }

                        binding.progressBarSignIn.setVisibility(View.GONE);

                    }
                }
            });
        }
    }

}