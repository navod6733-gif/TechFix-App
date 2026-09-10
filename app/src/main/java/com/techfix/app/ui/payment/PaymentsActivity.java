package com.techfix.app.ui.payment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.data.local.AppDatabase;
import com.techfix.app.data.local.entities.Appointment;
import com.techfix.app.data.local.entities.Branch;
import com.techfix.app.data.local.entities.Payment;
import com.techfix.app.data.local.entities.RepairService;
import com.techfix.app.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class PaymentsActivity extends AppCompatActivity implements PaymentAdapter.Listener {

    private AppDatabase db;
    private PaymentAdapter adapter;
    private TextView noPaymentsText;
    private RecyclerView recyclerView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        db = AppDatabase.getInstance(getApplicationContext());
        userId = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .getString(LoginActivity.KEY_USER_ID, null);

        recyclerView = findViewById(R.id.paymentRecyclerView);
        noPaymentsText = findViewById(R.id.noPaymentsText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter(this);
        recyclerView.setAdapter(adapter);

        if (userId == null) return;

        db.appointmentDao().getAppointmentsForUser(userId).observe(this, this::rebuildList);
    }

    private void rebuildList(List<Appointment> appointments) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<PaymentDisplay> displayList = new ArrayList<>();

            for (Appointment a : appointments) {
                if (!"Completed".equals(a.status)) continue; // only completed repairs need payment

                RepairService service = db.repairServiceDao().getServiceById(a.repairServiceId);
                Branch branch = db.branchDao().getBranchById(a.branchId);
                Payment existing = db.paymentDao().getPaymentForAppointment(a.id);

                displayList.add(new PaymentDisplay(
                        a,
                        service != null ? service.name : "Unknown",
                        branch != null ? branch.name : "Unknown",
                        service != null ? service.basePrice : 0,
                        existing
                ));
            }

            runOnUiThread(() -> {
                adapter.setItems(displayList);
                boolean empty = displayList.isEmpty();
                noPaymentsText.setVisibility(empty ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
            });
        });
    }

    @Override
    public void onPayClicked(PaymentDisplay item) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Payment payment = new Payment();
            payment.id = UUID.randomUUID().toString();
            payment.appointmentId = item.appointment.id;
            payment.amount = item.amount;
            payment.status = "Paid";
            payment.date = System.currentTimeMillis();

            db.paymentDao().insert(payment);

            item.appointment.paymentId = payment.id;
            db.appointmentDao().update(item.appointment);

            runOnUiThread(() -> {
                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                rebuildList(db.appointmentDao().getAllAppointments().getValue());
            });
        });
    }
}