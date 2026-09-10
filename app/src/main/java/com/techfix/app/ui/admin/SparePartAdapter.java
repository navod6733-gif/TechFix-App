package com.techfix.app.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.data.local.entities.SparePart;

import java.util.ArrayList;
import java.util.List;

public class SparePartAdapter extends RecyclerView.Adapter<SparePartAdapter.ViewHolder> {

    private List<SparePart> items = new ArrayList<>();

    public void setItems(List<SparePart> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_spare_part, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SparePart p = items.get(position);
        holder.name.setText(p.name);
        holder.quantity.setText("Qty: " + p.quantity);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.partName);
            quantity = itemView.findViewById(R.id.partQuantity);
        }
    }
}