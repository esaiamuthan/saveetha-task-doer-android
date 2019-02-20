package com.saveethataskdoor.app;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.home.HomeActivity;
import com.saveethataskdoor.app.login.LoginActivity;

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
            if (currentUser != null)
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            else
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }, 1000);
    }
}
