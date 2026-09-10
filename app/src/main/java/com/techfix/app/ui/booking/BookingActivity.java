package com.techfix.app.ui.booking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.techfix.app.MainActivity;
import com.techfix.app.R;
import com.techfix.app.data.local.AppDatabase;
import com.techfix.app.data.local.entities.Appointment;
import com.techfix.app.data.local.entities.Branch;
import com.techfix.app.data.local.entities.DeviceCategory;
import com.techfix.app.data.local.entities.RepairService;
import com.techfix.app.ui.auth.LoginActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

public class BookingActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 2001;
    private static final int CAMERA_CAPTURE_REQUEST = 2002;

    private Spinner categorySpinner, serviceSpinner, branchSpinner;

    private EditText serviceSearchInput;

    private TextView priceText;
    private ImageView photoPreview;
    private Button takePhotoButton, submitButton;
    private ProgressBar progressBar;

    private List<DeviceCategory> categories = new ArrayList<>();
    private List<RepairService> allServices = new ArrayList<>();
    private List<RepairService> filteredServices = new ArrayList<>();
    private List<Branch> branches = new ArrayList<>();

    private String photoFilePath = null;
    private Uri photoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        categorySpinner = findViewById(R.id.categorySpinner);
        serviceSpinner = findViewById(R.id.serviceSpinner);
        branchSpinner = findViewById(R.id.branchSpinner);
        priceText = findViewById(R.id.priceText);
        photoPreview = findViewById(R.id.devicePhotoPreview);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        submitButton = findViewById(R.id.submitBookingButton);
        progressBar = findViewById(R.id.bookingProgress);
        serviceSearchInput = findViewById(R.id.serviceSearchInput);

        loadData();

        serviceSearchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterServicesBySearch(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        takePhotoButton.setOnClickListener(v -> checkCameraPermissionAndOpen());
        submitButton.setOnClickListener(v -> submitBooking());

        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < filteredServices.size()) {
                    RepairService selected = filteredServices.get(position);
                    priceText.setText(String.format("Estimated Price: Rs. %.2f", selected.basePrice));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterServicesForCategory(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            categories = db.deviceCategoryDao().getAllCategoriesSync();
            allServices = db.repairServiceDao().getAllServicesSync();
            branches = db.branchDao().getAllBranchesSync();

            runOnUiThread(() -> {
                List<String> categoryNames = new ArrayList<>();
                for (DeviceCategory c : categories) categoryNames.add(c.name);
                ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, categoryNames);
                categorySpinner.setAdapter(catAdapter);

                List<String> branchNames = new ArrayList<>();
                for (Branch b : branches) branchNames.add(b.name);
                ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, branchNames);
                branchSpinner.setAdapter(branchAdapter);

                if (!categories.isEmpty()) filterServicesForCategory(0);
            });
        });
    }

    private void filterServicesForCategory(int categoryPosition) {
        if (categoryPosition >= categories.size()) return;
        String categoryId = categories.get(categoryPosition).id;

        filteredServices = new ArrayList<>();
        for (RepairService s : allServices) {
            if (s.deviceCategoryId.equals(categoryId)) filteredServices.add(s);
        }

        List<String> serviceNames = new ArrayList<>();
        for (RepairService s : filteredServices) serviceNames.add(s.name);

        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, serviceNames);
        serviceSpinner.setAdapter(serviceAdapter);

        if (!filteredServices.isEmpty()) {
            priceText.setText(String.format("Estimated Price: Rs. %.2f", filteredServices.get(0).basePrice));
        }
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST
                && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "Camera permission is required to attach a photo", Toast.LENGTH_LONG).show();
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            photoFilePath = photoFile.getAbsolutePath();
            photoUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider", photoFile);

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_REQUEST);
        } catch (IOException e) {
            Toast.makeText(this, "Couldn't open camera: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new java.util.Date());
        String fileName = "REPAIR_" + timeStamp;
        File storageDir = getExternalFilesDir("Pictures");
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_REQUEST && resultCode == RESULT_OK) {
            photoPreview.setImageURI(photoUri);
        }
    }

    private void submitBooking() {
        if (categories.isEmpty() || filteredServices.isEmpty() || branches.isEmpty()) {
            Toast.makeText(this, "Please wait for data to load", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .getString(LoginActivity.KEY_USER_ID, null);
        if (userId == null) {
            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show();
            return;
        }

        int servicePos = serviceSpinner.getSelectedItemPosition();
        int branchPos = branchSpinner.getSelectedItemPosition();

        if (servicePos < 0 || servicePos >= filteredServices.size() ||
                branchPos < 0 || branchPos >= branches.size()) {
            Toast.makeText(this, "Please select a service and branch", Toast.LENGTH_SHORT).show();
            return;
        }

        RepairService selectedService = filteredServices.get(servicePos);
        Branch selectedBranch = branches.get(branchPos);

        submitButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            Appointment appointment = new Appointment();
            appointment.id = UUID.randomUUID().toString();
            appointment.userId = userId;
            appointment.branchId = selectedBranch.id;
            appointment.repairServiceId = selectedService.id;
            appointment.technicianId = null;
            appointment.deviceImageUrl = photoFilePath;
            appointment.status = "Pending";
            appointment.requestDate = System.currentTimeMillis();
            appointment.paymentId = null;

            AppDatabase.getInstance(getApplicationContext()).appointmentDao().insert(appointment);

            runOnUiThread(() -> {
                Toast.makeText(this, "Repair booked successfully!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });

        });
    }
        private void filterServicesBySearch(String query) {
            if (query.trim().isEmpty()) {
                // fall back to category filtering when search is cleared
                int categoryPos = categorySpinner.getSelectedItemPosition();
                if (categoryPos >= 0) filterServicesForCategory(categoryPos);
                return;
            }

            List<RepairService> matches = new ArrayList<>();
            String lowerQuery = query.toLowerCase();
            for (RepairService s : allServices) {
                if (s.name.toLowerCase().contains(lowerQuery)) {
                    matches.add(s);
                }
            }

            filteredServices = matches;
            List<String> serviceNames = new ArrayList<>();
            for (RepairService s : matches) serviceNames.add(s.name);

            ArrayAdapter<String> serviceAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, serviceNames);
            serviceSpinner.setAdapter(serviceAdapter);

            if (!matches.isEmpty()) {
                priceText.setText(String.format("Estimated Price: Rs. %.2f", matches.get(0).basePrice));
            } else {
                priceText.setText("No matching services found");
            }

    }


}