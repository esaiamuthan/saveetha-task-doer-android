package com.saveethataskdoor.app;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.home.HODHomeActivity;
import com.saveethataskdoor.app.home.HomeActivity;
import com.saveethataskdoor.app.home.PrincipalHomeActivity;
import com.saveethataskdoor.app.home.StaffHomeActivity;
import com.saveethataskdoor.app.home.hod.HODActivity;
import com.saveethataskdoor.app.home.principal.PrincipalActivity;
import com.saveethataskdoor.app.home.staff.StaffActivity;
import com.saveethataskdoor.app.login.LoginActivity;
import com.saveethataskdoor.app.utils.PreferenceManager;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(() -> {
            if (currentUser != null) {
                if (PreferenceManager.getProfileType(this).equals("Student")) {
                    Intent loginValidateIntent = new Intent(MainActivity.this, HomeActivity.class);
                    loginValidateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginValidateIntent);
                } else if (PreferenceManager.getProfileType(this).equals("Staff")) {
                    Intent loginValidateIntent = new Intent(MainActivity.this, StaffActivity.class);
                    loginValidateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginValidateIntent);
                } else if (PreferenceManager.getProfileType(this).equals("HOD")) {
                    Intent loginValidateIntent = new Intent(MainActivity.this, HODActivity.class);
                    loginValidateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginValidateIntent);
                } else if (PreferenceManager.getProfileType(this).equals("Principal")) {
                    Intent loginValidateIntent = new Intent(MainActivity.this, PrincipalActivity.class);
                    loginValidateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginValidateIntent);
                }
            } else
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }, 1000);
    }
}
