package com.saveethataskdoor.app.food;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.model.Order;
import com.saveethataskdoor.app.model.Order;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    ArrayList<Order> storeArrayList = new ArrayList<>();
    private OnOrderClickListener onOrderListener;

    public OrdersAdapter(ArrayList<Order> storeArrayList,
                         OnOrderClickListener onOrderListener) {
        this.storeArrayList = storeArrayList;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(storeArrayList.get(position));

        holder.itemView.setOnClickListener(view -> {
            onOrderListener.onOrderClick(storeArrayList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return storeArrayList.size();
    }

    public void notifyData(ArrayList<Order> storeArrayList) {
        this.storeArrayList = storeArrayList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderId;
        TextView tvCount;
        TextView tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCount = itemView.findViewById(R.id.tvCount);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(Order store) {
            tvOrderId.setText("#" + store.getOrderId() + " - " + store.getStoreName());
            tvTotal.setText("Rs." + store.getTotal());
            tvCount.setText(store.getCount() + " products");
        }
    }
}
