package com.saveethataskdoor.app.food;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.database.DataBaseHandler;
import com.saveethataskdoor.app.databinding.ActivityCartBinding;
import com.saveethataskdoor.app.databinding.ActivityProductBinding;
import com.saveethataskdoor.app.food.CartActivity;
import com.saveethataskdoor.app.home.HomeActivity;
import com.saveethataskdoor.app.model.Cart;
import com.saveethataskdoor.app.model.Product;
import com.saveethataskdoor.app.model.Store;
import com.saveethataskdoor.app.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends BaseActivity
        implements OnCartListener {

    private ActivityCartBinding binding;

    private FirebaseAuth mAuth;

    private String TAG = CartActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    MenuItem itemCart;
    DataBaseHandler dataBaseHandler;
    OnCartListener cartListener;
    MyCartAdapter myCartAdapter;
    TextView cart_count;
    private User currentUserInfo;

    Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        initUI();
    }


    @Override
    public void initUI() {
        store = (Store) getIntent().getSerializableExtra("store");

        dataBaseHandler = new DataBaseHandler(this);
        cartListener = this;

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        binding.contentCart.placeOrder.setOnClickListener(v -> {
            placeOrder();
        });

        db.collection("saveetha")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnSuccessListener(documentSnapshot -> {
            currentUserInfo = documentSnapshot.toObject(User.class);
        });
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

    DocumentReference reference;

    private void placeOrder() {
        binding.linearProgress.setVisibility(View.VISIBLE);

        Map<String, Object> user = new HashMap<>();
        user.put("name", currentUserInfo.getName());
        user.put("userId", currentUserInfo.getUserId());
        user.put("department", currentUserInfo.getDepartment());
        user.put("year", currentUserInfo.getYearList().get(0));
        user.put("uId", mAuth.getUid());

        user.put("storeId", store.getStoreId());
        user.put("storeName", store.getName());

        user.put("createdAt", System.currentTimeMillis());

        user.put("count", dataBaseHandler.getCartCount());
        user.put("total", dataBaseHandler.getTotal());

        db.collection("orders")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    reference = documentReference;
                    addProducts(0);

                })
                .addOnFailureListener(throable -> {
                    binding.linearProgress.setVisibility(View.GONE);

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure" + throable.getMessage());
                    try {
                        throw Objects.requireNonNull(throable);
                    } catch (FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(CartActivity.this, "Weak password.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(CartActivity.this, "Invalid email address.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(CartActivity.this, "User already exists.",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(CartActivity.this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addProducts(int position) {
        final int[] currentPosition = {position};
        Cart cart = dataBaseHandler.getCartDetails().get(currentPosition[0]);

        reference.collection("products").add(cart)
                .addOnSuccessListener(documentReference -> {
                    binding.linearProgress.setVisibility(View.GONE);

                    currentPosition[0] = currentPosition[0] + 1;
                    if (currentPosition[0] <= (dataBaseHandler.getCartDetails().size() - 1)) {
                        addProducts(currentPosition[0]);
                    } else {
                        binding.linearProgress.setVisibility(View.GONE);
                        Toast.makeText(this, "Order created successfully", Toast.LENGTH_SHORT).show();

                        dataBaseHandler.deleteCart();

                        Intent loginValidateIntent = new Intent(this, HomeActivity.class);
                        loginValidateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginValidateIntent);
                        finish();
                    }
                })
                .addOnFailureListener(throable -> {
                    binding.linearProgress.setVisibility(View.GONE);

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure" + throable.getMessage());
                    try {
                        throw Objects.requireNonNull(throable);
                    } catch (FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(CartActivity.this, "Weak password.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(CartActivity.this, "Invalid email address.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(CartActivity.this, "User already exists.",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(CartActivity.this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart, menu);
        itemCart = menu.findItem(R.id.itemCart);
        MenuItemCompat.setActionView(itemCart, R.layout.cart_count);

        RelativeLayout relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(itemCart);
        cart_count = relativeLayout.findViewById(R.id.tvCartCount);
        cart_count.setText(String.valueOf(dataBaseHandler.getCartCount()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCartDelete(Cart cart, int position) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // continue with delete
                    dataBaseHandler.removeFromCart(String.valueOf(cart.getCartId()));
                    myCartAdapter.notifyData(position);
                    onCartCountUpdate();
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onCartCountUpdate() {
        cart_count.setText(String.valueOf(dataBaseHandler.getCartCount()));

        totalCalculation();
    }

    private void totalCalculation() {
        ((TextView) findViewById(R.id.total)).setText(
                String.format("Rs.%d", dataBaseHandler.getTotal())
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        totalCalculation();
        loadCartItems();
    }

    private void loadCartItems() {
        binding.contentCart.vCart.setLayoutManager(new LinearLayoutManager(this));
        myCartAdapter = new MyCartAdapter(dataBaseHandler.getCartDetails(), dataBaseHandler, cartListener);
        binding.contentCart.vCart.setAdapter(myCartAdapter);

        if (myCartAdapter.getItemCount() > 0)
            binding.contentCart.placeOrder.setEnabled(true);
        else
            binding.contentCart.placeOrder.setEnabled(false);
    }
}
