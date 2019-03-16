package com.saveethataskdoor.app.attendance;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saveethataskdoor.app.MainActivity;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityAttendanceBinding;
import com.saveethataskdoor.app.home.staff.StaffActivity;
import com.saveethataskdoor.app.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AttendanceActivity extends BaseActivity implements
        AttendanceFragment.OnAttendanceListener {

    private static final String TAG = AttendanceActivity.class.getSimpleName();

    ActivityAttendanceBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    HashMap<Integer, Boolean> attendanceMap = new HashMap<>();
    int rollNumber;

    private Menu mMenu;

    private ArrayList<Integer> presentList = new ArrayList<>();
    private ArrayList<Integer> absentList = new ArrayList<>();

    private User currentUserInfo;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_attendance);

        initUI();

        binding.viewPager.disableScroll(true);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), 10, attendanceMap);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setCurrentItem(0);

        binding.ivForward.setOnClickListener(view -> {
            if (!attendanceMap.containsKey(rollNumber)) {
                Toast.makeText(AttendanceActivity.this, "Please check the present or absent", Toast.LENGTH_SHORT).show();
            } else {
                int currentItem = binding.viewPager.getCurrentItem() + 1;
                if (currentItem <= adapter.getCount() - 1) {
                    if (currentItem == adapter.getCount() - 1) {
                        binding.viewPager.setCurrentItem(currentItem);
                        binding.ivForward.setVisibility(View.INVISIBLE);
                        onAttendanceForwardNumber();

                        mMenu.findItem(R.id.action_submit).setVisible(true);
                    } else {
                        binding.viewPager.setCurrentItem(currentItem);
                        onAttendanceForwardNumber();

                        mMenu.findItem(R.id.action_submit).setVisible(false);
                    }
                    if (currentItem > 0)
                        binding.ivBackward.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.ivBackward.setOnClickListener(view -> {
            int currentItem = binding.viewPager.getCurrentItem() - 1;
            if (currentItem == 0) {
                binding.viewPager.setCurrentItem(currentItem);
                binding.ivBackward.setVisibility(View.INVISIBLE);
                onAttendanceBackwardNumber();

                mMenu.findItem(R.id.action_submit).setVisible(false);
            } else {
                binding.viewPager.setCurrentItem(currentItem);
                onAttendanceBackwardNumber();

                mMenu.findItem(R.id.action_submit).setVisible(false);
            }
            if (currentItem > 0)
                binding.ivForward.setVisibility(View.VISIBLE);
        });
        binding.ivBackward.setVisibility(View.INVISIBLE);
        binding.ivForward.setVisibility(View.VISIBLE);

        onAttendanceForwardNumber();

        if (currentUserInfo == null) {
            binding.linearProgress.setVisibility(View.VISIBLE);

            db.collection("saveetha")
                    .document(Objects.requireNonNull(mAuth.getUid()))
                    .get().addOnSuccessListener(documentSnapshot -> {
                currentUserInfo = documentSnapshot.toObject(User.class);

                binding.linearProgress.setVisibility(View.GONE);

            });
        }
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Period " + Objects.requireNonNull(getIntent().getExtras()).getInt("period"));

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAttendanceClicked(boolean isPresent) {
        attendanceMap.put(rollNumber, isPresent);
        findViewById(R.id.iv_forward).performClick();
    }

    public void onAttendanceForwardNumber() {
        this.rollNumber = binding.viewPager.getCurrentItem() + 1;
        binding.tvStudent.setText(String.format("%d", rollNumber));
    }

    public void onAttendanceBackwardNumber() {
        this.rollNumber = binding.viewPager.getCurrentItem() + 1;
        binding.tvStudent.setText(String.format("%d", rollNumber));
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_submit, menu);

        this.mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_submit:
                if (attendanceMap.size() > 0) {
                    for (Integer key : attendanceMap.keySet()) {
                        boolean value = attendanceMap.get(key);
                        if (value) {
                            presentList.add(key);
                        } else {
                            absentList.add(key);
                        }
                    }
                    saveData();
                }
                return true;
        }
        return false;
    }

    private void saveData() {

        binding.linearProgress.setVisibility(View.VISIBLE);
        Map<String, Object> user = new HashMap<>();
        user.put("period", Objects.requireNonNull(getIntent().getExtras()).getInt("period"));
        user.put("year", getIntent().getExtras().getInt("year"));
        user.put("date", Objects.requireNonNull(getIntent().getExtras().getString("date")));
        user.put("absent", absentList);
        user.put("present", presentList);
        user.put("department", currentUserInfo.getDepartment());
        user.put("uId", Objects.requireNonNull(mAuth.getUid()));
        user.put("createdAt", System.currentTimeMillis());
        user.put("isClosed", true);

        db.collection("attendance")
                .add(user)
                .addOnCompleteListener(taskNew -> {
                    Log.w(TAG, "getInstanceId failed" + taskNew);
                    binding.linearProgress.setVisibility(View.GONE);

                    Intent loginValidateIntent = new Intent(this, StaffActivity.class);
                    loginValidateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginValidateIntent);
                    finish();

                })
                .addOnFailureListener(tasknew -> {
                    Log.w(TAG, "getInstanceId failed" + tasknew);
                    binding.linearProgress.setVisibility(View.GONE);
                });
        ;
    }

    @Override
    public void onBackPressed() {
        if (binding.viewPager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            binding.ivBackward.performClick();
    }
}
