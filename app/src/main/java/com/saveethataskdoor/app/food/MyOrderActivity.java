package com.saveethataskdoor.app.food;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import com.saveethataskdoor.app.databinding.ActivityMyOrderBinding;
import com.saveethataskdoor.app.leave.LeaveListActivity;
import com.saveethataskdoor.app.model.Leave;
import com.saveethataskdoor.app.model.Order;
import com.saveethataskdoor.app.model.Product;

import java.util.ArrayList;
import java.util.Objects;

public class MyOrderActivity extends BaseActivity implements
        OnOrderClickListener {

    private ActivityMyOrderBinding binding;

    ArrayList<Order> orderArrayList = new ArrayList<>();

    OrdersAdapter ordersAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String TAG = MyOrderActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_order);
        initUI();
    }


    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding.contentMyOrder.rvMyOrder.setLayoutManager(new LinearLayoutManager(this));
        ordersAdapter = new OrdersAdapter(orderArrayList, this);
        binding.contentMyOrder.rvMyOrder.setAdapter(ordersAdapter);
        mAuth = FirebaseAuth.getInstance();

        getOrders();
    }

    private void getOrders() {
        binding.linearProgress.setVisibility(View.VISIBLE);

        db.collection("orders")
                .whereEqualTo("uId", mAuth.getUid())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Order leave = document.toObject(Order.class);
                            leave.setOrderId(document.getId());
                            orderArrayList.add(leave);
                        }
                        ordersAdapter.notifyData(orderArrayList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        ordersAdapter.notifyData(orderArrayList);
                    }
                    binding.linearProgress.setVisibility(View.GONE);

                });
    }

    @Override
    public void onOrderClick(Order order) {
        ProductInfoFragment productInfoFragment = ProductInfoFragment.newInstance(order);
        productInfoFragment.show(getSupportFragmentManager(), "productInfoFragment");
    }
}
