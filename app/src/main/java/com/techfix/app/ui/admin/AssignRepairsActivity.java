package com.techfix.app.ui.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.data.local.AppDatabase;
import com.techfix.app.data.local.entities.AppUser;
import com.techfix.app.data.local.entities.Appointment;
import com.techfix.app.data.local.entities.Branch;
import com.techfix.app.data.local.entities.RepairService;
import com.techfix.app.data.local.entities.Technician;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AssignRepairsActivity extends AppCompatActivity implements AssignRepairAdapter.Listener {

    private AssignRepairAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_repairs);

        db = AppDatabase.getInstance(getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.assignRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AssignRepairAdapter(this);
        recyclerView.setAdapter(adapter);

        db.appointmentDao().getAllAppointments().observe(this, this::rebuildDisplayList);
    }

    private void rebuildDisplayList(List<Appointment> appointments) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AppointmentAdminDisplay> displayList = new ArrayList<>();

            for (Appointment a : appointments) {
                RepairService service = db.repairServiceDao().getServiceById(a.repairServiceId);
                Branch branch = db.branchDao().getBranchById(a.branchId);
                AppUser customer = db.appUserDao().getUserById(a.userId);

                List<Technician> allTechs = db.technicianDao().getAllTechniciansSync();
                List<Technician> branchTechs = new ArrayList<>();
                for (Technician t : allTechs) {
                    if (t.branchId.equals(a.branchId)) branchTechs.add(t);
                }

                displayList.add(new AppointmentAdminDisplay(
                        a,
                        service != null ? service.name : "Unknown",
                        branch != null ? branch.name : "Unknown",
                        customer != null ? customer.name : "Unknown",
                        branchTechs
                ));
            }

            runOnUiThread(() -> adapter.setItems(displayList));
        });
    }

    @Override
    public void onAssign(AppointmentAdminDisplay item, Technician selectedTechnician) {
        Executors.newSingleThreadExecutor().execute(() -> {
            item.appointment.technicianId = selectedTechnician.id;
            item.appointment.status = "Assigned";
            db.appointmentDao().update(item.appointment);

            runOnUiThread(() ->
                    Toast.makeText(this, "Assigned to " + selectedTechnician.name, Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public void onAdvanceStatus(AppointmentAdminDisplay item) {
        String next;
        switch (item.appointment.status) {
            case "Pending": next = "Assigned"; break;
            case "Assigned": next = "InProgress"; break;
            case "InProgress": next = "Completed"; break;
            default: next = item.appointment.status; // already Completed
        }

        String finalNext = next;
        Executors.newSingleThreadExecutor().execute(() -> {
            item.appointment.status = finalNext;
            db.appointmentDao().update(item.appointment);

            runOnUiThread(() ->
                    Toast.makeText(this, "Status updated to " + finalNext, Toast.LENGTH_SHORT).show());
        });
    }
}