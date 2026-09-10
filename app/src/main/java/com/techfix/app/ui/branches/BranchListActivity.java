package com.techfix.app.ui.branches;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.techfix.app.R;
import com.techfix.app.data.local.AppDatabase;
import com.techfix.app.data.local.entities.Branch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;

public class BranchListActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private RecyclerView recyclerView;
    private BranchAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_list);

        recyclerView = findViewById(R.id.branchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BranchAdapter();
        recyclerView.setAdapter(adapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkPermissionAndLoad();
    }

    private void checkPermissionAndLoad() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            loadBranchesWithLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadBranchesWithLocation();
            } else {
                Toast.makeText(this, "Location permission denied — showing branches unsorted", Toast.LENGTH_LONG).show();
                loadBranchesWithoutLocation();
            }
        }
    }

    private void loadBranchesWithLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    loadAndSortBranches(location);
                } else {
                    Toast.makeText(this, "Couldn't get current location — showing unsorted", Toast.LENGTH_SHORT).show();
                    loadBranchesWithoutLocation();
                }
            });
        } catch (SecurityException e) {
            loadBranchesWithoutLocation();
        }
    }

    private void loadAndSortBranches(Location userLocation) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Branch> branches = getAllBranchesSync();

            List<BranchWithDistance> withDistance = new ArrayList<>();
            for (Branch b : branches) {
                float[] result = new float[1];
                Location.distanceBetween(
                        userLocation.getLatitude(), userLocation.getLongitude(),
                        b.latitude, b.longitude,
                        result
                );
                float distanceKm = result[0] / 1000f;
                withDistance.add(new BranchWithDistance(b, distanceKm));
            }

            Collections.sort(withDistance, Comparator.comparingDouble(bd -> bd.distanceKm));

            runOnUiThread(() -> adapter.setItems(withDistance));
        });
    }

    private void loadBranchesWithoutLocation() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Branch> branches = getAllBranchesSync();
            List<BranchWithDistance> withDistance = new ArrayList<>();
            for (Branch b : branches) {
                withDistance.add(new BranchWithDistance(b, -1));
            }
            runOnUiThread(() -> adapter.setItems(withDistance));
        });
    }

    private List<Branch> getAllBranchesSync() {
        // Simple synchronous fetch since BranchDao.getAllBranches() returns LiveData;
        // for a one-time list load we query directly here instead.
        return AppDatabase.getInstance(getApplicationContext())
                .branchDao().getAllBranchesSync();
    }
}