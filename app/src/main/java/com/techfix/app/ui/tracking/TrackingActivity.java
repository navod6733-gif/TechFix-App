package com.techfix.app.ui.tracking;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.data.local.AppDatabase;
import com.techfix.app.data.local.entities.Appointment;
import com.techfix.app.data.local.entities.Branch;
import com.techfix.app.data.local.entities.RepairService;
import com.techfix.app.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TrackingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private TextView emptyText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        recyclerView = findViewById(R.id.appointmentRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(item -> {
            android.content.Intent intent = new android.content.Intent(this, AppointmentDetailActivity.class);
            intent.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, item.appointment.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        String userId = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .getString(LoginActivity.KEY_USER_ID, null);

        if (userId == null) {
            emptyText.setVisibility(View.VISIBLE);
            return;
        }

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        db.appointmentDao().getAppointmentsForUser(userId).observe(this, appointments -> {
            if (appointments == null || appointments.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return;
            }
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            Executors.newSingleThreadExecutor().execute(() -> {
                List<AppointmentDisplay> displayList = new ArrayList<>();
                for (Appointment a : appointments) {
                    RepairService service = db.repairServiceDao().getServiceById(a.repairServiceId);
                    Branch branch = db.branchDao().getBranchById(a.branchId);

                    String serviceName = service != null ? service.name : "Unknown Service";
                    String branchName = branch != null ? branch.name : "Unknown Branch";

                    displayList.add(new AppointmentDisplay(a, serviceName, branchName));
                }
                runOnUiThread(() -> adapter.setItems(displayList));
            });
        });
    }
}