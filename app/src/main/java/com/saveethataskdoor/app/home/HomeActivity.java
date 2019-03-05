package com.saveethataskdoor.app.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.bonofide.BonofideActivity;
import com.saveethataskdoor.app.databinding.ActivityHomeBinding;
import com.saveethataskdoor.app.food.FoodShopActivity;
import com.saveethataskdoor.app.food.MyOrderActivity;
import com.saveethataskdoor.app.leave.LeaveListActivity;
import com.saveethataskdoor.app.login.LoginActivity;
import com.saveethataskdoor.app.model.Token;
import com.saveethataskdoor.app.model.User;
import com.saveethataskdoor.app.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding binding;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private User currentUserInfo;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initUI();

        getFirebaseToken();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        mAuth = FirebaseAuth.getInstance();

        binding.contentHome.tvLeaveLetter.setOnClickListener(v -> {
            startActivity(new Intent(this, LeaveListActivity.class));
        });
        binding.contentHome.tvBonofide.setOnClickListener(v -> {
            Intent intent = new Intent(this, BonofideActivity.class);
            intent.putExtra("profile", "student");
            startActivity(intent);
        });

        binding.contentHome.tvComplaint.setOnClickListener(v -> {
            CommonUtils.sendMail(this);
        });
        binding.contentHome.tvOrderFood.setOnClickListener(v -> {
            startActivity(new Intent(this, FoodShopActivity.class));
        });
        binding.contentHome.tvMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, MyOrderActivity.class));
        });

        binding.contentHome.tvTrackBus.setOnClickListener(v -> {
            CommonUtils.availableSoon(this);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;
        }
        return true;
    }

    private void getFirebaseToken() {

        // Get token
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    String msg = getString(R.string.app_name, token);
                    Log.d(TAG, msg);


                    if (currentUserInfo == null) {
                        db.collection("saveetha")
                                .document(Objects.requireNonNull(mAuth.getUid()))
                                .get().addOnSuccessListener(documentSnapshot -> {
                            currentUserInfo = documentSnapshot.toObject(User.class);
                            storeToken(token);
                        });
                    }
                });
        // [END retrieve_current_token]
    }

    private void storeToken(String token) {
        Map<String, Object> user = new HashMap<>();
        user.put("token", token);
        user.put("name", currentUserInfo.getName());
        user.put("userId", currentUserInfo.getUserId());
        user.put("department", currentUserInfo.getDepartment());
        user.put("year", currentUserInfo.getYearList());
        user.put("uId", mAuth.getUid());

        user.put("createdAt", System.currentTimeMillis());

        db.collection("tokens")
                .whereEqualTo("uId", mAuth.getUid())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        db.collection("tokens")
                                .add(user)
                                .addOnSuccessListener(taskNew -> {
                                });
                    } else {
                        Token leave = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            leave = document.toObject(Token.class);
                            leave.setDocumentid(document.getId());

                            db.collection("tokens")
                                    .document(leave.getDocumentid())
                                    .set(user)
                                    .addOnSuccessListener(taskNew -> {
                                    });
                            break;
                        }
                    }

                });
    }
}
