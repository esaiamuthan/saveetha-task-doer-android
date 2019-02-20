package com.saveethataskdoor.app.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityRegisterBinding;
import com.saveethataskdoor.app.home.HomeActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    private String TAG = RegisterActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.tbRegister);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideKeyboard();
                finish();
                break;
            case R.id.action_submit:
                hideKeyboard();
                if (checkFields()) {
                    callRegisterAPi();
                }
                break;
        }
        return true;
    }

    private void callRegisterAPi() {
        binding.linearProgress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(binding.etREmail.getText().toString(),
                binding.etRPassword.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");

                        createUser();
                    } else {
                        binding.linearProgress.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            Toast.makeText(RegisterActivity.this, "Weak password.",
                                    Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(RegisterActivity.this, "Invalid email address.",
                                    Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthUserCollisionException e) {
                            Toast.makeText(RegisterActivity.this, "User already exists.",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "" + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUser() {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("name", binding.etRName.getText().toString());
        user.put("type", binding.sptRType.getSelectedItemPosition());
        user.put("userId", binding.etRCollegeId.getText().toString());
        user.put("email", binding.etREmail.getText().toString());
        user.put("department", binding.etRDepartment.getSelectedItemPosition());
        user.put("year", binding.spYear.getSelectedItemPosition());

        // Add a new document with a generated ID

        db.collection("saveetha")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .set(user)
                .addOnSuccessListener(documentReference -> {
                    binding.linearProgress.setVisibility(View.GONE);
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(throable -> {
                    binding.linearProgress.setVisibility(View.GONE);

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure" + throable.getMessage());
                    try {
                        throw Objects.requireNonNull(throable);
                    } catch (FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(RegisterActivity.this, "Weak password.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(RegisterActivity.this, "Invalid email address.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(RegisterActivity.this, "User already exists.",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public boolean checkFields() {
        boolean validated = true;
        if (binding.sptRType.getSelectedItemPosition() == 0) {
            showMessage("Please select user type");
            validated = false;
        } else if (binding.etRName.getText().toString().isEmpty()) {
            binding.etRName.setError("Please enter user name");
            validated = false;
        } else if (binding.etRCollegeId.getText().toString().isEmpty()) {
            binding.etRCollegeId.setError("Please enter college ID");
            validated = false;
        } else if (binding.etREmail.getText().toString().isEmpty()) {
            binding.etREmail.setError("Please enter email address");
            validated = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etREmail.getText().toString()).matches()) {
            binding.etREmail.setError("Please enter valid email address");
            validated = false;
        } else if (binding.etRPassword.getText().toString().isEmpty()) {
            binding.etRPassword.setError("Please enter your password");
            validated = false;
        } else if (binding.etRPassword.getText().toString().length() < 6) {
            binding.etRPassword.setError("Password should be 6 characters");
            validated = false;
        } else if (binding.etRCnfPassword.getText().toString().isEmpty()) {
            binding.etRCnfPassword.setError("Please enter confirm password");
            validated = false;
        } else if (binding.etRCnfPassword.getText().toString().length() < 6) {
            binding.etRCnfPassword.setError("Password should be 6 characters");
            validated = false;
        } else if (!binding.etRPassword.getText().toString().equals(binding.etRCnfPassword.getText().toString())) {
            binding.etRCnfPassword.setError("Password and confirm password not matched");
            validated = false;
        } else if (binding.etRDepartment.getSelectedItemPosition() == 0) {
            showMessage("Please select your department");
            validated = false;
        } else if (binding.spYear.getSelectedItemPosition() == 0) {
            showMessage("Please select your year");
            validated = false;
        }
        return validated;
    }
}
