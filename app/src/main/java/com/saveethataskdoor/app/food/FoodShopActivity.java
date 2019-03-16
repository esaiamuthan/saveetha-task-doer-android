package com.saveethataskdoor.app.food;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityFoodShopBinding;
import com.saveethataskdoor.app.model.Store;

import java.util.ArrayList;

public class FoodShopActivity extends BaseActivity implements OnStoreClickListener {

    private ActivityFoodShopBinding binding;

    private FirebaseAuth mAuth;

    private String TAG = FoodShopActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Store> storeArrayList = new ArrayList<>();

    StoresAdapter storesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_shop);
        initUI();

    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        binding.contentFoodShop.rvShops.setLayoutManager(new LinearLayoutManager(this));
        storesAdapter = new StoresAdapter(storeArrayList, this);
        binding.contentFoodShop.rvShops.setAdapter(storesAdapter);

        getStores();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_my_orders:
                startActivity(new Intent(this, MyOrderActivity.class));
                break;
        }
        return true;
    }

    private void getStores() {
        binding.linearProgress.setVisibility(View.VISIBLE);
        db.collection("stores")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storeArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Store store = document.toObject(Store.class);
                            store.setDocumentId(document.getId());
                            storeArrayList.add(store);
                        }
                        storesAdapter.notifyData(storeArrayList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        storesAdapter.notifyData(storeArrayList);
                    }
                    binding.linearProgress.setVisibility(View.GONE);
                });
    }

    @Override
    public void onStoreClick(Store store) {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("store", store);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_orders, menu);
        return true;
    }
}
