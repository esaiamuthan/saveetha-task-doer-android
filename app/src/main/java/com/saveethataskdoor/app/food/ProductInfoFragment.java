package com.saveethataskdoor.app.food;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseBottomSheetDialogFragment;
import com.saveethataskdoor.app.model.Cart;
import com.saveethataskdoor.app.model.Order;
import com.saveethataskdoor.app.model.Product;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductInfoFragment extends BaseBottomSheetDialogFragment {


    RecyclerView recyclerView;
    OrdersProductAdapter ordersProductAdapter;
    ArrayList<Cart> list = new ArrayList<Cart>();

    private FirebaseAuth mAuth;

    private String TAG = ProductActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    LinearLayout linearProgress;

    Order order;

    public ProductInfoFragment() {
        // Required empty public constructor
    }

    public static ProductInfoFragment newInstance(Order order) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        ProductInfoFragment productInfoFragment = new ProductInfoFragment();
        productInfoFragment.setArguments(bundle);
        return productInfoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        order = (Order) Objects.requireNonNull(getArguments()).getSerializable("order");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvProducts);
        linearProgress = view.findViewById(R.id.linearProgress);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ordersProductAdapter = new OrdersProductAdapter(list);
        recyclerView.setAdapter(ordersProductAdapter);

        mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    @Override
    public void initUI() {
        getProdcuts();
    }

    private void getProdcuts() {
        linearProgress.setVisibility(View.VISIBLE);

        db.collection("orders")
                .document(order.getOrderId())
                .collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Cart product = document.toObject(Cart.class);
                            list.add(product);
                        }
                        ordersProductAdapter.notifyData(list);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        ordersProductAdapter.notifyData(list);
                    }
                    linearProgress.setVisibility(View.GONE);
                });
    }
}
