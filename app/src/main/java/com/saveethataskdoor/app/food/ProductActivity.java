package com.saveethataskdoor.app.food;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.database.DataBaseHandler;
import com.saveethataskdoor.app.databinding.ActivityProductBinding;
import com.saveethataskdoor.app.model.Product;
import com.saveethataskdoor.app.model.Store;

import java.util.ArrayList;

public class ProductActivity extends BaseActivity
        implements OnProductClickListener, OnCartCountListener {

    private ActivityProductBinding binding;

    private FirebaseAuth mAuth;

    private String TAG = ProductActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Product> productArrayList = new ArrayList<>();

    ProductAdapter productAdapter;

    Store store;

    DataBaseHandler dataBaseHandler;
    OnCartCountListener onCartCountListener;

    MenuItem itemCart;
    TextView cart_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_product);
        initUI();
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
    public void initUI() {
        store = (Store) getIntent().getSerializableExtra("store");

        dataBaseHandler = new DataBaseHandler(this);
        onCartCountListener = this;

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(productArrayList, this,
                onCartCountListener, dataBaseHandler);
        binding.rvProducts.setAdapter(productAdapter);

        binding.linearProgress.setVisibility(View.VISIBLE);
    }

    private void getProducts() {
        db.collection("stores")
                .document(store.getDocumentId())
                .collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Product product = document.toObject(Product.class);

                            product.setStoreId(store.getStoreId());
                            product.setStorename(store.getName());

                            productArrayList.add(product);
                        }
                        productAdapter.notifyData(productArrayList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        productAdapter.notifyData(productArrayList);
                    }
                    binding.linearProgress.setVisibility(View.GONE);
                });
    }

    @Override
    public void onProductClick(Product product) {
    }

    @Override
    public void onCartCountUpdate() {
        if (cart_count != null)
            cart_count.setText(String.valueOf(dataBaseHandler.getCartCount()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.cart, menu);
        itemCart = menu.findItem(R.id.itemCart);
        MenuItemCompat.setActionView(itemCart, R.layout.cart_count);

        RelativeLayout relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(itemCart);
        cart_count = relativeLayout.findViewById(R.id.tvCartCount);
        onCartCountUpdate();

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductActivity.this, CartActivity.class);
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getProducts();
    }
}
