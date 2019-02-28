package com.saveethataskdoor.app.food;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.model.Store;

import java.util.ArrayList;

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.ViewHolder> {

    ArrayList<Store> storeArrayList = new ArrayList<>();
    private OnStoreClickListener onStoreListener;

    public StoresAdapter(ArrayList<Store> storeArrayList,
                         OnStoreClickListener onStoreListener) {
        this.storeArrayList = storeArrayList;
        this.onStoreListener = onStoreListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_store_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(storeArrayList.get(position));

        holder.itemView.setOnClickListener(view -> {
            onStoreListener.onStoreClick(storeArrayList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return storeArrayList.size();
    }

    public void notifyData(ArrayList<Store> storeArrayList) {
        this.storeArrayList = storeArrayList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(Store store) {
            tvName.setText(store.getName());
        }
    }
}
