package com.saveethataskdoor.app.leave.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saveethataskdoor.app.OnLeaveClickListener;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.model.Leave;

import java.util.ArrayList;

public class LeaveListAdapter extends RecyclerView.Adapter<LeaveListAdapter.ViewHolder> {

    ArrayList<Leave> leaveList = new ArrayList<>();
    private OnLeaveClickListener onLeaveClickListener;

    public LeaveListAdapter(ArrayList<Leave> leaveList,
                            OnLeaveClickListener onLeaveClickListener) {
        this.leaveList = leaveList;
        this.onLeaveClickListener = onLeaveClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leave_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(leaveList.get(position));

        holder.itemView.setOnClickListener(view -> {
            onLeaveClickListener.onLeaveClick(leaveList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return leaveList.size();
    }

    public void notifyData(ArrayList<Leave> leaveList) {
        this.leaveList = leaveList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvLeaveType, tvLeaveDate, tvStatus, tvRejected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLeaveType = itemView.findViewById(R.id.tvLeaveType);
            tvLeaveDate = itemView.findViewById(R.id.tvLeaveDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRejected = itemView.findViewById(R.id.tvRejected);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(Leave leave) {
            String year = "";
            if (leave.getYear() == 1)
                year = leave.getYear() + "st year";
            else if (leave.getYear() == 2)
                year = leave.getYear() + "nd year";
            else if (leave.getYear() == 3)
                year = leave.getYear() + "rd year";
            else if (leave.getYear() == 4)
                year = leave.getYear() + "th year";

            tvName.setText("Student Name : " + leave.getName() + " - " +
                    leave.getUserId() + " (" + year + ")");
            tvLeaveType.setText(leave.getLeaveType());
            if (leave.getLeaveType().equals("Fever")) {
                tvLeaveDate.setText("Date : " + leave.getDate());
            } else {
                tvLeaveDate.setText("Start Date : " + leave.getStartDate() + " - End date : " + leave.getEndDate());
            }
            tvRejected.setVisibility(View.GONE);
            if (leave.getStatus() == 100)
                tvStatus.setText("Leave Requested");
            else if (leave.getStatus() == 101)
                tvStatus.setText("Forwarded to HOD");
            else if (leave.getStatus() == 102)
                tvStatus.setText("Forwarded to Principal");
            else if (leave.getStatus() == 103)
                tvStatus.setText("Leave Approved");
            else if (leave.getStatus() == 104) {
                tvStatus.setText("Leave Rejected");
                tvRejected.setVisibility(View.VISIBLE);
            }
            tvRejected.setText("By : " + leave.getRejectedBy() + " Reason : " + leave.getRejectedReason());
        }
    }
}
