package com.techfix.app.ui.tracking;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface Listener {
        void onAppointmentClicked(AppointmentDisplay item);
    }

    private List<AppointmentDisplay> items = new ArrayList<>();
    private final Listener listener;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

    public AppointmentAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<AppointmentDisplay> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentDisplay item = items.get(position);

        holder.serviceName.setText(item.serviceName);
        holder.branchName.setText(item.branchName);
        holder.requestDate.setText(dateFormat.format(new Date(item.appointment.requestDate)));
        holder.statusBadge.setText(item.appointment.status);
        holder.statusBadge.setBackgroundColor(getStatusColor(item.appointment.status));

        if (item.appointment.deviceImageUrl != null) {
            holder.thumbnail.setImageURI(Uri.parse(item.appointment.deviceImageUrl));
        } else {
            holder.thumbnail.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v -> listener.onAppointmentClicked(item));
    }




    private int getStatusColor(String status) {
        switch (status) {
            case "Completed": return Color.parseColor("#4CAF50");
            case "InProgress": return Color.parseColor("#2196F3");
            case "Assigned": return Color.parseColor("#9C27B0");
            default: return Color.parseColor("#FF9800"); // Pending
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView serviceName, branchName, requestDate, statusBadge;

        ViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.deviceThumbnail);
            serviceName = itemView.findViewById(R.id.serviceName);
            branchName = itemView.findViewById(R.id.branchName);
            requestDate = itemView.findViewById(R.id.requestDate);
            statusBadge = itemView.findViewById(R.id.statusBadge);
        }
    }
}