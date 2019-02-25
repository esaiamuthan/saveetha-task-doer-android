package com.saveethataskdoor.app.leave;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.saveethataskdoor.app.OnLeaveClickListener;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.ReviewActivity;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityLeaveListBinding;
import com.saveethataskdoor.app.leave.adapter.LeaveListAdapter;
import com.saveethataskdoor.app.model.Leave;
import com.saveethataskdoor.app.model.User;

import java.util.ArrayList;
import java.util.Objects;

public class LeaveListActivity extends BaseActivity
        implements OnLeaveClickListener {

    private ActivityLeaveListBinding binding;
    private ArrayList<Leave> leaveList = new ArrayList<>();
    LeaveListAdapter leaveListAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String TAG = LeaveListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_leave_list);
        initUI();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(LeaveListActivity.this, LeaveCreateActivity.class)));
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        binding.contentLeaveList.rvLeaveList.setLayoutManager(new LinearLayoutManager(this));
        leaveListAdapter = new LeaveListAdapter(leaveList, this);
        binding.contentLeaveList.rvLeaveList.setAdapter(leaveListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        getLeaveList();
    }

    private void getLeaveList() {
        db.collection("saveetha")
                .document("leave_forms")
                .collection("leave_letters")
                .whereEqualTo("uId", mAuth.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        leaveList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Leave leave = document.toObject(Leave.class);
                            leave.setDocumentId(document.getId());
                            leaveList.add(leave);
                        }
                        leaveListAdapter.notifyData(leaveList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        leaveListAdapter.notifyData(leaveList);
                    }
                });
    }

    @Override
    public void onLeaveClick(Leave leave) {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("leave", leave);
        startActivity(intent);
    }
}
