package com.saveethataskdoor.app.food;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.model.Cart;
import com.saveethataskdoor.app.model.Cart;

import java.util.ArrayList;

public class OrdersProductAdapter extends RecyclerView.Adapter<OrdersProductAdapter.ViewHolder> {

    ArrayList<Cart> cartList = new ArrayList<>();

    public OrdersProductAdapter(ArrayList<Cart> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_product_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(cartList.get(position));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void notifyData(ArrayList<Cart> cartList) {
        this.cartList = cartList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(Cart store) {
            tvName.setText(store.getName());
            tvAmount.setText(
                    "Rs." + store.getQuantity() * store.getAmount() + " (" + store.getQuantity() + " product(s))"
            );
        }
    }
}
