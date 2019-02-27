package com.saveethataskdoor.app.bonofide;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityApplyBonofideBinding;
import com.saveethataskdoor.app.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplyBonofideActivity extends BaseActivity {

    private ActivityApplyBonofideBinding binding;

    private FirebaseAuth mAuth;

    private String TAG = BonofideActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private User currentUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_apply_bonofide);
        initUI();
    }


    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
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
                    applyBonofide();
                }
                break;
        }
        return true;
    }

    private void applyBonofide() {
        db.collection("saveetha")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnSuccessListener(documentSnapshot -> {
            currentUserInfo = documentSnapshot.toObject(User.class);

            bonofideAPI();
        });
    }

    private void bonofideAPI() {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("uId", mAuth.getUid());
        user.put("name", currentUserInfo.getName());
        user.put("collegeId", currentUserInfo.getUserId());
        user.put("department", currentUserInfo.getDepartment());
        user.put("year", currentUserInfo.getYear());
        user.put("subject", binding.contentApplyBonofide.etSubject.getText().toString());
        user.put("fatherName", binding.contentApplyBonofide.rtFatherName.getText().toString());

        // Add a new document with a generated ID

        db.collection("bonofide")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    binding.linearProgress.setVisibility(View.GONE);
                    finish();
                })
                .addOnFailureListener(throable -> {
                    binding.linearProgress.setVisibility(View.GONE);

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure" + throable.getMessage());
                    try {
                        throw Objects.requireNonNull(throable);
                    } catch (FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(ApplyBonofideActivity.this, "Weak password.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(ApplyBonofideActivity.this, "Invalid email address.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(ApplyBonofideActivity.this, "User already exists.",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ApplyBonofideActivity.this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public boolean checkFields() {
        boolean validated = true;
        if (binding.contentApplyBonofide.etSubject.getText().toString().isEmpty()) {
            binding.contentApplyBonofide.etSubject.setError("Please enter subject");
            validated = false;
        } else if (binding.contentApplyBonofide.rtFatherName.getText().toString().isEmpty()) {
            binding.contentApplyBonofide.rtFatherName.setError("Please enter father name");
            validated = false;
        }
        return validated;
    }
}
