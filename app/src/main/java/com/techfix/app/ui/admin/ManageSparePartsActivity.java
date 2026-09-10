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
import com.techfix.app.data.local.entities.SparePart;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ManageSparePartsActivity extends AppCompatActivity {

    private EditText nameInput, quantityInput;
    private Spinner branchSpinner;
    private RecyclerView recyclerView;
    private SparePartAdapter adapter;
    private List<Branch> branches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_spare_parts);

        nameInput = findViewById(R.id.partNameInput);
        quantityInput = findViewById(R.id.partQuantityInput);
        branchSpinner = findViewById(R.id.partBranchSpinner);
        recyclerView = findViewById(R.id.partRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SparePartAdapter();
        recyclerView.setAdapter(adapter);

        findViewById(R.id.addPartButton).setOnClickListener(v -> addPart());

        loadBranchesAndParts();
    }

    private void loadBranchesAndParts() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        Executors.newSingleThreadExecutor().execute(() -> {
            branches = db.branchDao().getAllBranchesSync();
            List<String> branchNames = new ArrayList<>();
            for (Branch b : branches) branchNames.add(b.name);

            List<SparePart> allParts = db.sparePartDao().getAllPartsSync();

            runOnUiThread(() -> {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, branchNames);
                branchSpinner.setAdapter(spinnerAdapter);
                adapter.setItems(allParts);
            });
        });
    }

    private void addPart() {
        String name = nameInput.getText().toString().trim();
        String quantityStr = quantityInput.getText().toString().trim();
        int branchPos = branchSpinner.getSelectedItemPosition();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || branchPos < 0 || branches.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            quantityInput.setError("Enter a valid number");
            return;
        }

        SparePart p = new SparePart();
        p.id = UUID.randomUUID().toString();
        p.name = name;
        p.quantity = quantity;
        p.branchId = branches.get(branchPos).id;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(getApplicationContext()).sparePartDao().insert(p);
            runOnUiThread(() -> {
                nameInput.setText("");
                quantityInput.setText("");
                Toast.makeText(this, "Spare part added", Toast.LENGTH_SHORT).show();
                loadBranchesAndParts();
            });
        });
    }
}