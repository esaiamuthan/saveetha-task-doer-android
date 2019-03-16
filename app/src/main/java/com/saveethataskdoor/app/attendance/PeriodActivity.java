package com.saveethataskdoor.app.attendance;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityPeriodBinding;
import com.saveethataskdoor.app.model.Attendance;
import com.saveethataskdoor.app.model.Leave;
import com.saveethataskdoor.app.model.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class PeriodActivity extends BaseActivity {

    ActivityPeriodBinding binding;
    private static final String TAG = PeriodActivity.class.getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User currentUserInfo;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_period);

        initUI();

        binding.period1.setOnClickListener(view -> openAttendance(1));
        binding.period2.setOnClickListener(view -> openAttendance(2));
        binding.period3.setOnClickListener(view -> openAttendance(3));
        binding.period4.setOnClickListener(view -> openAttendance(4));
        binding.period5.setOnClickListener(view -> openAttendance(5));
        binding.period6.setOnClickListener(view -> openAttendance(6));
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
    }

    private void openAttendance(int period) {
        binding.linearProgress.setVisibility(View.VISIBLE);

        db.collection("attendance")
                .whereEqualTo("year", Objects.requireNonNull(getIntent().getExtras()).getInt("year"))
                .whereEqualTo("date", getIntent().getExtras().getString("date"))
                .whereEqualTo("period", period)
                .limit(1)
                .get().addOnCompleteListener(task -> {
            binding.linearProgress.setVisibility(View.GONE);
            Log.w(TAG, "attendance : " + task.isSuccessful());

            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    if (getIntent().getExtras().getBoolean("history")) {
                        Toast.makeText(this, "No records found", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(this, AttendanceActivity.class);
                        intent.putExtra("year", getIntent().getExtras().getInt("year"));
                        intent.putExtra("period", period);
                        intent.putExtra("date", getIntent().getExtras().getString("date"));
                        intent.putExtra("history", getIntent().getExtras().getBoolean("history"));
                        startActivity(intent);
                    }
                } else {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Attendance attendance = document.toObject(Attendance.class);
                        showDAlert(attendance);
                        break;
                    }
                }
            }
        });
    }

    private void showDAlert(Attendance attendance) {
        String message = "";

        StringBuilder content = new StringBuilder();
        Collections.sort(attendance.getPresent(), Integer::compareTo);

        for (int i = 0; i < attendance.getPresent().size(); i++) {
            content.append(attendance.getPresent().get(i)).append(",");
        }
        if (content.length() > 0)
            content = new StringBuilder(content.substring(0, content.length() - 1));

        message += "Present : " + content + "\n\n";

        content = new StringBuilder();
        Collections.sort(attendance.getAbsent(), Integer::compareTo);

        for (int i = 0; i < attendance.getAbsent().size(); i++) {
            content.append(attendance.getAbsent().get(i)).append(",");
        }
        if (content.length() > 0)
            content = new StringBuilder(content.substring(0, content.length() - 1));

        message += "Absent : " + content;

        new AlertDialog.Builder(this)
                .setTitle("Total : " + (attendance.getPresent().size() + attendance.getAbsent().size()))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                    dialog.dismiss();
                })
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}
