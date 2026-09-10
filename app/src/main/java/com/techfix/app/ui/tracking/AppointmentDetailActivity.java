package com.techfix.app.ui.tracking;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.data.local.AppDatabase;
import com.techfix.app.data.local.entities.Appointment;
import com.techfix.app.data.local.entities.Branch;
import com.techfix.app.data.local.entities.RepairService;
import com.techfix.app.data.local.entities.Technician;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AppointmentDetailActivity extends AppCompatActivity {

    public static final String EXTRA_APPOINTMENT_ID = "appointment_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        String appointmentId = getIntent().getStringExtra(EXTRA_APPOINTMENT_ID);
        if (appointmentId == null) {
            finish();
            return;
        }

        loadDetail(appointmentId);
    }

    private void loadDetail(String appointmentId) {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        Executors.newSingleThreadExecutor().execute(() -> {
            Appointment appointment = db.appointmentDao().getAppointmentById(appointmentId);
            if (appointment == null) {
                runOnUiThread(this::finish);
                return;
            }

            RepairService service = db.repairServiceDao().getServiceById(appointment.repairServiceId);
            Branch branch = db.branchDao().getBranchById(appointment.branchId);
            Technician technician = appointment.technicianId != null
                    ? db.technicianDao().getTechnicianById(appointment.technicianId)
                    : null;

            runOnUiThread(() -> bindData(appointment, service, branch, technician));
        });
    }

    private void bindData(Appointment appointment, RepairService service, Branch branch, Technician technician) {
        ImageView photo = findViewById(R.id.detailPhoto);
        TextView status = findViewById(R.id.detailStatus);
        TextView serviceName = findViewById(R.id.detailServiceName);
        TextView branchName = findViewById(R.id.detailBranchName);
        TextView branchAddress = findViewById(R.id.detailBranchAddress);
        TextView technicianText = findViewById(R.id.detailTechnician);
        TextView requestDate = findViewById(R.id.detailRequestDate);
        TextView price = findViewById(R.id.detailPrice);

        if (appointment.deviceImageUrl != null) {
            photo.setImageURI(Uri.parse(appointment.deviceImageUrl));
        }

        status.setText(appointment.status);
        status.setBackgroundColor(getStatusColor(appointment.status));

        serviceName.setText(service != null ? service.name : "Unknown Service");
        branchName.setText(branch != null ? branch.name : "Unknown Branch");
        branchAddress.setText(branch != null ? branch.address : "");
        technicianText.setText(technician != null ? technician.name : "Not yet assigned");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        requestDate.setText(dateFormat.format(new Date(appointment.requestDate)));

        price.setText(service != null ? String.format("Rs. %.2f", service.basePrice) : "N/A");
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "Completed": return Color.parseColor("#4CAF50");
            case "InProgress": return Color.parseColor("#2196F3");
            case "Assigned": return Color.parseColor("#9C27B0");
            default: return Color.parseColor("#FF9800");
        }
    }
}