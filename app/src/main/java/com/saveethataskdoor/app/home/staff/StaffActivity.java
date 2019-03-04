package com.saveethataskdoor.app.home.staff;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityStaffBinding;
import com.saveethataskdoor.app.food.FoodShopActivity;
import com.saveethataskdoor.app.food.MyOrderActivity;
import com.saveethataskdoor.app.home.StaffHomeActivity;
import com.saveethataskdoor.app.login.LoginActivity;
import com.saveethataskdoor.app.model.User;
import com.saveethataskdoor.app.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StaffActivity extends BaseActivity {

    private ActivityStaffBinding binding;
    private User currentUserInfo;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String[] yearsArray = new String[0];
    Integer[] yearsIntArray = new Integer[0];
    private static final String TAG = StaffActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_staff);
        initUI();


        getFirebaseToken();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        mAuth = FirebaseAuth.getInstance();

        binding.contentStaff.tvLeaveLetter.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Please select year")
                    .setSingleChoiceItems(yearsArray, 0, null)
                    .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        Intent intent = new Intent(this, StaffHomeActivity.class);
                        intent.putExtra("year", yearsIntArray[selectedPosition]);
                        startActivity(intent);
                    })
                    .show();
        });

        binding.contentStaff.tvComplaint.setOnClickListener(v -> {
            CommonUtils.sendMail(this);
        });
        binding.contentStaff.tvOrderFood.setOnClickListener(v -> {
            startActivity(new Intent(this, FoodShopActivity.class));
        });
        binding.contentStaff.tvMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, MyOrderActivity.class));
        });

        if (currentUserInfo == null) {
            binding.linearProgress.setVisibility(View.VISIBLE);

            db.collection("saveetha")
                    .document(Objects.requireNonNull(mAuth.getUid()))
                    .get().addOnSuccessListener(documentSnapshot -> {
                currentUserInfo = documentSnapshot.toObject(User.class);
                binding.linearProgress.setVisibility(View.GONE);

                yearsArray = new String[currentUserInfo.getYearList().size()];
                yearsIntArray = new Integer[currentUserInfo.getYearList().size()];

                for (int i = 0; i < currentUserInfo.getYearList().size(); i++) {
                    int year = currentUserInfo.getYearList().get(i);
                    String currentYear = "";
                    if (year == 1)
                        currentYear = "First year";
                    else if (year == 2)
                        currentYear = "Second year";
                    else if (year == 3)
                        currentYear = "Third year";
                    else if (year == 4)
                        currentYear = "Fourth year";
                    yearsArray[i] = currentYear;

                    yearsIntArray[i] = year;
                }

                if (currentUserInfo.isClassAdviser())
                    binding.contentStaff.tvLeaveLetter.setVisibility(View.VISIBLE);
                else
                    binding.contentStaff.tvLeaveLetter.setVisibility(View.GONE);
            });
        }

        binding.contentStaff.tvTrackBus.setOnClickListener(v -> {
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
        user.put("name", currentUserInfo.getName());
        user.put("userId", currentUserInfo.getUserId());
        user.put("department", currentUserInfo.getDepartment());
        user.put("year", currentUserInfo.getYearList().get(0));
        user.put("uId", mAuth.getUid());

        user.put("createdAt", System.currentTimeMillis());

        db.collection("tokens")
                .add(user)
                .addOnSuccessListener(task -> {
        });
    }
}
