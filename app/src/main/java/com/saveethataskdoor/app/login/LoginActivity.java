package com.saveethataskdoor.app.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityLoginBinding;
import com.saveethataskdoor.app.home.HomeActivity;

import java.util.Objects;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.tbLogin);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        binding.tvRegister.setOnClickListener(this);
        binding.cvLoginConnexion.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRegister:
                hideKeyboard();
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.cvLoginConnexion:
                hideKeyboard();
                if (checkFields()) {
                    callLoginAPI();
                }
                break;
        }
    }

    private void callLoginAPI() {
        binding.linearProgress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(binding.etLoginUserName.getText().toString(),
                binding.etLoginPassword.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");

                        binding.linearProgress.setVisibility(View.GONE);
                        Intent loginValidateIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        loginValidateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginValidateIntent);
                        finish();
                    } else {
                        binding.linearProgress.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            Toast.makeText(LoginActivity.this, "Weak password.",
                                    Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(LoginActivity.this, "Invalid email address.",
                                    Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthUserCollisionException e) {
                            Toast.makeText(LoginActivity.this, "User already exists.",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "" + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean checkFields() {
        boolean validated = true;
        if (binding.etLoginUserName.getText().toString().isEmpty()) {
            binding.etLoginUserName.setError("Please enter email address");
            validated = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etLoginUserName.getText().toString()).matches()) {
            binding.etLoginUserName.setError("Please enter valid email address");
            validated = false;
        } else if (binding.etLoginPassword.getText().toString().isEmpty()) {
            binding.etLoginPassword.setError("Please enter your password");
            validated = false;
        }
        return validated;
    }
}
