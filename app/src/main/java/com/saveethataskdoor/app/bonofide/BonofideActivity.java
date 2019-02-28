package com.saveethataskdoor.app.bonofide;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityBonofideBinding;
import com.saveethataskdoor.app.login.RegisterActivity;
import com.saveethataskdoor.app.model.Bonofide;
import com.saveethataskdoor.app.model.Leave;

import java.util.ArrayList;

public class BonofideActivity extends BaseActivity
        implements BonofideClickListener {

    private ActivityBonofideBinding binding;

    private FirebaseAuth mAuth;

    private String TAG = BonofideActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Bonofide> bonofideArrayList = new ArrayList<>();

    BonofideAdapter bonofideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bonofide);
        initUI();

        FloatingActionButton fab = findViewById(R.id.fab);

        if (getIntent().getExtras().getString("profile").equals("principal")) {
            findViewById(R.id.fab).setVisibility(View.GONE);
        } else
            findViewById(R.id.fab).setVisibility(View.VISIBLE);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(BonofideActivity.this, ApplyBonofideActivity.class);
            startActivityForResult(intent, 1000);
        });
    }


    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        binding.contentBonofide.rvBonofide.setLayoutManager(new LinearLayoutManager(this));
        bonofideAdapter = new BonofideAdapter(bonofideArrayList, this);
        binding.contentBonofide.rvBonofide.setAdapter(bonofideAdapter);

        getBonofideList();
    }

    private void getBonofideList() {
        binding.linearProgress.setVisibility(View.VISIBLE);

        Query query;
        if (getIntent().getExtras().getString("profile").equals("principal")) {
            query = db.collection("bonofide")
                    .whereEqualTo("year", getIntent().getExtras().getInt("year"));
        } else {
            query = db.collection("bonofide")
                    .whereEqualTo("uId", mAuth.getUid());
        }
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bonofideArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Bonofide bonofide = document.toObject(Bonofide.class);
                            bonofide.setDocumentId(document.getId());
                            bonofideArrayList.add(bonofide);
                        }
                        bonofideAdapter.notifyData(bonofideArrayList);
                    } else {
                        bonofideAdapter.notifyData(bonofideArrayList);
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    binding.linearProgress.setVisibility(View.GONE);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == 1001) {
                getBonofideList();
            }
        }
    }

    @Override
    public void onBonofideClick(Bonofide bonofide) {
        Intent intent = new Intent(this, BonofideReviewActivity.class);
        intent.putExtra("bonofide", bonofide);
        startActivity(intent);
    }
}
