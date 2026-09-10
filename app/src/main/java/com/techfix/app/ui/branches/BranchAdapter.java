package com.techfix.app.ui.branches;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;

import java.util.ArrayList;
import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ViewHolder> {

    private List<BranchWithDistance> items = new ArrayList<>();

    public void setItems(List<BranchWithDistance> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_branch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BranchWithDistance item = items.get(position);
        holder.name.setText(item.branch.name);
        holder.address.setText(item.branch.address);

        if (item.distanceKm >= 0) {
            holder.distance.setText(String.format("%.1f km away", item.distanceKm));
        } else {
            holder.distance.setText("Distance unavailable");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, distance;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.branchName);
            address = itemView.findViewById(R.id.branchAddress);
            distance = itemView.findViewById(R.id.branchDistance);
        }
    }
}