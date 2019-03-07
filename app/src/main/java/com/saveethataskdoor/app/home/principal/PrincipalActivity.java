package com.saveethataskdoor.app.home.principal;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.bonofide.BonofideActivity;
import com.saveethataskdoor.app.databinding.ActivityHodBinding;
import com.saveethataskdoor.app.databinding.ActivityPrincipalBinding;
import com.saveethataskdoor.app.food.FoodShopActivity;
import com.saveethataskdoor.app.food.MyOrderActivity;
import com.saveethataskdoor.app.home.HODHomeActivity;
import com.saveethataskdoor.app.home.PrincipalHomeActivity;
import com.saveethataskdoor.app.login.LoginActivity;
import com.saveethataskdoor.app.model.Token;
import com.saveethataskdoor.app.model.User;
import com.saveethataskdoor.app.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrincipalActivity extends BaseActivity {

    ActivityPrincipalBinding binding;
    private static final String TAG = PrincipalActivity.class.getSimpleName();
    private User currentUserInfo;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_principal);
        initUI();

        getFirebaseToken();
    }


    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        mAuth = FirebaseAuth.getInstance();

        binding.contentPrincipal.tvLeaveLetter.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Please select year")
                    .setSingleChoiceItems(getResources().getStringArray(R.array.year_array), 0, null)
                    .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        Intent intent = new Intent(this, PrincipalHomeActivity.class);
                        intent.putExtra("year", selectedPosition + 1);
                        startActivity(intent);
                    })
                    .show();
        });
        binding.contentPrincipal.tvBonofide.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Please select year")
                    .setSingleChoiceItems(getResources().getStringArray(R.array.year_array), 0, null)
                    .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();


                        Intent intent = new Intent(this, BonofideActivity.class);
                        intent.putExtra("profile", "principal");
                        intent.putExtra("year", selectedPosition + 1);
                        startActivity(intent);

                    })
                    .show();

        });

        binding.contentPrincipal.tvTrackBus.setOnClickListener(v -> {
            CommonUtils.availableSoon(this);
        });
        binding.contentPrincipal.tvOrderFood.setOnClickListener(v -> {
            startActivity(new Intent(this, FoodShopActivity.class));
        });
        binding.contentPrincipal.tvMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, MyOrderActivity.class));
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
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(this, LoginActivity.class);
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
        user.put("type", currentUserInfo.getType());
        user.put("name", currentUserInfo.getName());
        user.put("userId", currentUserInfo.getUserId());
        user.put("department", currentUserInfo.getDepartment());
        user.put("year", new ArrayList<Integer>());
        user.put("uId", mAuth.getUid());

        user.put("createdAt", System.currentTimeMillis());

        db.collection("tokens")
                .document(mAuth.getUid())
                .set(user)
                .addOnCompleteListener(taskNew -> {
                    Log.w(TAG, "getInstanceId failed" + taskNew);

                })
                .addOnFailureListener(tasknew -> {
                    Log.w(TAG, "getInstanceId failed" + tasknew);
                });;
    }
}
