package com.techfix.app.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.data.local.AppDatabase;
import com.techfix.app.data.local.entities.Branch;
import com.techfix.app.data.local.entities.Technician;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ManageTechniciansActivity extends AppCompatActivity {

    private EditText nameInput, specialtyInput;
    private Spinner branchSpinner;
    private RecyclerView recyclerView;
    private TechnicianAdapter adapter;
    private List<Branch> branches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_technicians);

        nameInput = findViewById(R.id.techNameInput);
        specialtyInput = findViewById(R.id.techSpecialtyInput);
        branchSpinner = findViewById(R.id.techBranchSpinner);
        recyclerView = findViewById(R.id.technicianRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TechnicianAdapter();
        recyclerView.setAdapter(adapter);

        findViewById(R.id.addTechnicianButton).setOnClickListener(v -> addTechnician());

        loadBranchesAndTechnicians();
    }

    private void loadBranchesAndTechnicians() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        Executors.newSingleThreadExecutor().execute(() -> {
            branches = db.branchDao().getAllBranchesSync();
            List<String> branchNames = new ArrayList<>();
            for (Branch b : branches) branchNames.add(b.name);

            List<Technician> allTechs = db.technicianDao().getAllTechniciansSync();

            runOnUiThread(() -> {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, branchNames);
                branchSpinner.setAdapter(spinnerAdapter);
                adapter.setItems(allTechs);
            });
        });
    }

    private void addTechnician() {
        String name = nameInput.getText().toString().trim();
        String specialty = specialtyInput.getText().toString().trim();
        int branchPos = branchSpinner.getSelectedItemPosition();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(specialty) || branchPos < 0 || branches.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Technician t = new Technician();
        t.id = UUID.randomUUID().toString();
        t.name = name;
        t.specialty = specialty;
        t.branchId = branches.get(branchPos).id;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(getApplicationContext()).technicianDao().insert(t);
            runOnUiThread(() -> {
                nameInput.setText("");
                specialtyInput.setText("");
                Toast.makeText(this, "Technician added", Toast.LENGTH_SHORT).show();
                loadBranchesAndTechnicians();
            });
        });
    }
}