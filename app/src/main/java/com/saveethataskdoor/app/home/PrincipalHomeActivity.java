package com.saveethataskdoor.app.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.saveethataskdoor.app.OnLeaveClickListener;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.ReviewActivity;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityPrincipalHomeBinding;
import com.saveethataskdoor.app.leave.adapter.LeaveListAdapter;
import com.saveethataskdoor.app.login.LoginActivity;
import com.saveethataskdoor.app.model.Leave;
import com.saveethataskdoor.app.model.User;

import java.util.ArrayList;
import java.util.Objects;

public class PrincipalHomeActivity extends BaseActivity
        implements OnLeaveClickListener {

    private FirebaseAuth mAuth;

    private String TAG = PrincipalHomeActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private User currentUserInfo;


    private ArrayList<Leave> leaveList = new ArrayList<>();
    LeaveListAdapter leaveListAdapter;

    private ActivityPrincipalHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_principal_home);
        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        binding.contentPrincipalHome.rvLetters.setLayoutManager(new LinearLayoutManager(this));
        leaveListAdapter = new LeaveListAdapter(leaveList, this);
        binding.contentPrincipalHome.rvLetters.setAdapter(leaveListAdapter);
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

        if (currentUserInfo == null) {
            binding.linearProgress.setVisibility(View.VISIBLE);
            db.collection("saveetha")
                    .document(Objects.requireNonNull(mAuth.getUid()))
                    .get().addOnSuccessListener(documentSnapshot -> {
                currentUserInfo = documentSnapshot.toObject(User.class);
                getLeaveList();
            });
        } else
            getLeaveList();
    }

    private void getLeaveList() {
        db.collection("saveetha")
                .document("leave_forms")
                .collection("leave_letters")
                .whereEqualTo("year", Objects.requireNonNull(getIntent().getExtras()).getInt("year"))
                .orderBy("createdAt", Query.Direction.DESCENDING)
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
                    binding.linearProgress.setVisibility(View.GONE);
                });
    }

    @Override
    public void onLeaveClick(Leave leave) {
        if (leave.getStatus() == 100) {
            Toast.makeText(this, "Waiting for staff approval", Toast.LENGTH_SHORT).show();
        } else if (leave.getStatus() == 101) {
            Toast.makeText(this, "Waiting for HOD approval", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("leave", leave);
            startActivity(intent);
        }
    }

}
