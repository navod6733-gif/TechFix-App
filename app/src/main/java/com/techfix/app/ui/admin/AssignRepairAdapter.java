package com.techfix.app.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.data.local.entities.Technician;

import java.util.ArrayList;
import java.util.List;

public class AssignRepairAdapter extends RecyclerView.Adapter<AssignRepairAdapter.ViewHolder> {

    public interface Listener {
        void onAssign(AppointmentAdminDisplay item, Technician selectedTechnician);
        void onAdvanceStatus(AppointmentAdminDisplay item);
    }

    private List<AppointmentAdminDisplay> items = new ArrayList<>();
    private final Listener listener;

    public AssignRepairAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<AppointmentAdminDisplay> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assign_repair, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentAdminDisplay item = items.get(position);

        holder.serviceName.setText(item.serviceName);
        holder.customerBranch.setText(item.customerName + " • " + item.branchName);
        holder.status.setText("Status: " + item.appointment.status);

        List<String> techNames = new ArrayList<>();
        for (Technician t : item.availableTechnicians) techNames.add(t.name);
        if (techNames.isEmpty()) techNames.add("No technicians at this branch");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(holder.itemView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, techNames);
        holder.technicianSpinner.setAdapter(spinnerAdapter);

        holder.assignButton.setOnClickListener(v -> {
            int pos = holder.technicianSpinner.getSelectedItemPosition();
            if (pos >= 0 && pos < item.availableTechnicians.size()) {
                listener.onAssign(item, item.availableTechnicians.get(pos));
            }
        });

        holder.advanceButton.setOnClickListener(v -> listener.onAdvanceStatus(item));

        // Disable advance button if nothing assigned yet and still pending
        boolean canAdvance = !item.appointment.status.equals("Completed");
        holder.advanceButton.setEnabled(canAdvance);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, customerBranch, status;
        Spinner technicianSpinner;
        Button assignButton, advanceButton;

        ViewHolder(View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.aServiceName);
            customerBranch = itemView.findViewById(R.id.aCustomerBranch);
            status = itemView.findViewById(R.id.aStatus);
            technicianSpinner = itemView.findViewById(R.id.aTechnicianSpinner);
            assignButton = itemView.findViewById(R.id.aAssignButton);
            advanceButton = itemView.findViewById(R.id.aAdvanceButton);
        }
    }
}
