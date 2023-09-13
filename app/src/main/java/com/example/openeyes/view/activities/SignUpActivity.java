package com.example.openeyes.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;

import com.example.openeyes.R;
import com.example.openeyes.databinding.ActivitySignUpBinding;
import com.example.openeyes.model.User;
import com.example.openeyes.utility.MySharedPreferences;
import com.example.openeyes.utility.SnackBarHandler;
import com.example.openeyes.utility.UiHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private MySharedPreferences mySharedPreferences;
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        mySharedPreferences = new MySharedPreferences(this);

        binding.txtSignUpToSignIn2.setOnClickListener(this);
        binding.btnSignUp.setOnClickListener(this);
        binding.constLayoutSignUpProgress.setOnClickListener(null);

    }

    /****************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.txtSignUpToSignIn2.getId()) {
            goToSignInActivity();

        } else if (view.getId() == binding.btnSignUp.getId()) {
            checkErrorAndAction();

        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showExitDialog();

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

    private void goToHomeActivity() {
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(
                R.anim.fade_in_new,
                R.anim.fade_out_new
        );
        finish();

    }

    private void goToSignInActivity() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );
        finish();

    }

    private boolean checkErrorEdtSignUpName() {
        if (Objects.requireNonNull(binding.edtSignUpName.getText()).toString().trim().isEmpty()) {
            binding.txtLayoutSignUpName.setError(getString(R.string.error_empty_field));
            return false;

        } else {
            binding.txtLayoutSignUpName.setError(null);
            return true;

        }
    }

    private boolean checkErrorEdtSignUpNickname() {
        if (Objects.requireNonNull(binding.edtSignUpNickname.getText()).toString().trim().isEmpty()) {
            binding.txtLayoutSignUpNickname.setError(getString(R.string.error_empty_field));
            return false;

        } else {
            binding.txtLayoutSignUpNickname.setError(null);
            return true;

        }
    }

    private boolean checkErrorEdtSignUpEmail() {
        if (Objects.requireNonNull(binding.edtSignUpEmail.getText()).toString().trim().isEmpty()) {
            binding.txtLayoutSignUpEmail.setError(getString(R.string.error_empty_field));
            return false;

        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(binding.edtSignUpEmail.getText()).toString().trim()).matches()) {
                binding.txtLayoutSignUpEmail.setError(null);
                return true;

            } else {
                binding.txtLayoutSignUpEmail.setError(getString(R.string.error_email_format));
                return false;

            }
        }
    }

    private boolean checkErrorEdtSignUpPassword() {
        if (Objects.requireNonNull(binding.edtSignUpPassword.getText()).toString().trim().isEmpty()) {
            binding.txtLayoutSignUpPassword.setError(getString(R.string.error_empty_field));
            return false;

        } else if (Objects.requireNonNull(binding.edtSignUpPassword.getText()).toString().trim().length() < 6) {
            binding.txtLayoutSignUpPassword.setError(getString(R.string.error_short_password));
            return false;

        } else {
            binding.txtLayoutSignUpPassword.setError(null);
            return true;

        }
    }

    private boolean checkErrorEdtSignUpPasswordConfirm() {
        if (Objects.requireNonNull(binding.edtSignUpPasswordConfirm.getText()).toString().trim().isEmpty()) {
            binding.txtLayoutSignUpPasswordConfirm.setError(getString(R.string.error_empty_field));
            return false;

        } else if (Objects.requireNonNull(binding.edtSignUpPasswordConfirm.getText()).toString().trim().length() < 6) {
            binding.txtLayoutSignUpPasswordConfirm.setError(getString(R.string.error_short_password));
            return false;

        } else {
            binding.txtLayoutSignUpPasswordConfirm.setError(null);
            return true;

        }
    }

    private void handleError() {
        binding.edtSignUpName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.txtLayoutSignUpName.setError(getString(R.string.error_empty_field));

                } else {
                    binding.txtLayoutSignUpName.setError(null);

                }
            }
        });

        binding.edtSignUpNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.txtLayoutSignUpNickname.setError(getString(R.string.error_empty_field));

                } else {
                    binding.txtLayoutSignUpNickname.setError(null);

                }
            }
        });

        binding.edtSignUpEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.txtLayoutSignUpEmail.setError(getString(R.string.error_empty_field));

                } else {
                    binding.txtLayoutSignUpEmail.setError(null);

                }
            }
        });

        binding.edtSignUpPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.txtLayoutSignUpPassword.setError(getString(R.string.error_empty_field));

                } else if (editable.toString().trim().length() < 6) {
                    binding.txtLayoutSignUpPassword.setError(getString(R.string.error_short_password));

                } else {
                    binding.txtLayoutSignUpPassword.setError(null);

                }
            }
        });

        binding.edtSignUpPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.txtLayoutSignUpPasswordConfirm.setError(getString(R.string.error_empty_field));

                } else if (editable.toString().trim().length() < 6) {
                    binding.txtLayoutSignUpPasswordConfirm.setError(getString(R.string.error_empty_field));

                } else {
                    binding.txtLayoutSignUpPasswordConfirm.setError(null);

                }
            }
        });
    }

    private void checkErrorAndAction() {
        handleError();

        if (checkErrorEdtSignUpName() && checkErrorEdtSignUpNickname() && checkErrorEdtSignUpEmail() && checkErrorEdtSignUpPassword() && checkErrorEdtSignUpPasswordConfirm()) {
            if (Objects.requireNonNull(binding.edtSignUpPassword.getText()).toString().trim().equals(Objects.requireNonNull(binding.edtSignUpPasswordConfirm.getText()).toString().trim())) {
                if (binding.checkBoxSignUp.isChecked()) {
                    // Set progress bar visible.
                    binding.constLayoutSignUpProgress.setVisibility(View.VISIBLE);

                    // Closing the keyboard.
                    UiHandler.keyboardDown(binding.edtSignUpEmail, SignUpActivity.this);

                    // Getting what user has entered.
                    String enteredFullName = Objects.requireNonNull(binding.edtSignUpName.getText()).toString().trim();
                    String enteredNickName = Objects.requireNonNull(binding.edtSignUpNickname.getText()).toString().trim();
                    String enteredEmail = binding.edtSignUpEmail.getText().toString().trim();
                    String enteredPassword = binding.edtSignUpPassword.getText().toString().trim();

                    // We have to encode the email into a safe key format (. -> , && @ -> -).
                    String encodedEmail = enteredEmail.replace(".", ",").replace("@", "-");

                    // Create an user object.
                    User user = new User(enteredFullName, enteredNickName, encodedEmail);

                    FirebaseAuth fAuth = FirebaseAuth.getInstance();
                    DatabaseReference fReference = FirebaseDatabase.getInstance().getReference("User");

                    // Check for same email in firebase authentication.
                    fAuth.fetchSignInMethodsForEmail(enteredEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                if (isNewUser) {
                                    fAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Also adding the user into firebase realtime database and shared preferences.
                                                fReference.child(user.getEmail()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            mySharedPreferences.setUserInfo(user);
                                                            mySharedPreferences.setLoggedIn(true);

                                                            binding.constLayoutSignUpProgress.setVisibility(View.GONE);

                                                            goToHomeActivity();

                                                        } else {
                                                            SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                                                            binding.constLayoutSignUpProgress.setVisibility(View.GONE);

                                                        }
                                                    }
                                                });

                                            } else {
                                                SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                                                binding.constLayoutSignUpProgress.setVisibility(View.GONE);

                                            }
                                        }
                                    });

                                } else {
                                    SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_user_exist));
                                    binding.constLayoutSignUpProgress.setVisibility(View.GONE);

                                }

                            } else {
                                SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_occurred));
                                binding.constLayoutSignUpProgress.setVisibility(View.GONE);

                            }

                        }
                    });

                } else {
                    SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_accept_term));

                }

            } else {
                SnackBarHandler.snackBarHideAction(getApplicationContext(), binding.getRoot(), getString(R.string.error_password_not_same));

            }
        }
    }

}