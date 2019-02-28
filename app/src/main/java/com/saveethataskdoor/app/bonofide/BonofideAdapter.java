package com.saveethataskdoor.app.bonofide;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.model.Bonofide;

import java.util.ArrayList;

public class BonofideAdapter extends RecyclerView.Adapter<BonofideAdapter.ViewHolder> {

    ArrayList<Bonofide> storeArrayList = new ArrayList<>();
    private BonofideClickListener onBonofideListener;

    public BonofideAdapter(ArrayList<Bonofide> storeArrayList,
                           BonofideClickListener onBonofideListener) {
        this.storeArrayList = storeArrayList;
        this.onBonofideListener = onBonofideListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bonofide_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(storeArrayList.get(position));

        holder.itemView.setOnClickListener(view -> {
            onBonofideListener.onBonofideClick(storeArrayList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return storeArrayList.size();
    }

    public void notifyData(ArrayList<Bonofide> storeArrayList) {
        this.storeArrayList = storeArrayList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvUser;
        TextView otherInfo;
        TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvUser = itemView.findViewById(R.id.tvUser);
            otherInfo = itemView.findViewById(R.id.otherInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(Bonofide store) {
            tvName.setText("Subject : " + store.getSubject());
            tvUser.setText("Name : " + store.getName());
            otherInfo.setText("College Id : " + store.getCollegeId());
            if (store.getStatus() == 100)
                tvStatus.setText("Bonofide request created");
            else if (store.getStatus() == 101)
                tvStatus.setText("Bonofide approved");
        }
    }
}
