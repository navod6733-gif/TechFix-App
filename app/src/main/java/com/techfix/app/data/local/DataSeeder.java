package com.techfix.app.data.local;

import android.content.Context;

import com.techfix.app.data.local.entities.Branch;
import com.techfix.app.data.local.entities.DeviceCategory;
import com.techfix.app.data.local.entities.RepairService;

import java.util.concurrent.Executors;

public class DataSeeder {
    public static void seedIfEmpty(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            seedBranches(db);
            seedCategoriesAndServices(db);
        });
    }

    private static void seedBranches(AppDatabase db) {
        insertBranchIfMissing(db, "branch_colombo", "TechFix Colombo",
                "123 Galle Road, Colombo 03", 6.9271, 79.8612, "011-2345678");

        insertBranchIfMissing(db, "branch_galle", "TechFix Galle",
                "45 Main Street, Galle Fort", 6.0535, 80.2210, "091-2233445");

        insertBranchIfMissing(db, "branch_kandy", "TechFix Kandy",
                "78 Peradeniya Road, Kandy", 7.2906, 80.6337, "081-2244556");

        insertBranchIfMissing(db, "branch_negombo", "TechFix Negombo",
                "12 Lewis Place, Negombo", 7.2083, 79.8358, "031-2233667");

        insertBranchIfMissing(db, "branch_jaffna", "TechFix Jaffna",
                "56 Hospital Road, Jaffna", 9.6615, 80.0255, "021-2223344");

        insertBranchIfMissing(db, "branch_kurunegala", "TechFix Kurunegala",
                "34 Kandy Road, Kurunegala", 7.4818, 80.3609, "037-2234455");
    }

    private static void insertBranchIfMissing(AppDatabase db, String id, String name,
                                              String address, double lat, double lng, String phone) {
        if (db.branchDao().getBranchById(id) == null) {
            Branch branch = new Branch();
            branch.id = id;
            branch.name = name;
            branch.address = address;
            branch.latitude = lat;
            branch.longitude = lng;
            branch.phone = phone;
            db.branchDao().insert(branch);
        }
    }

    private static void seedCategoriesAndServices(AppDatabase db) {
        if (db.deviceCategoryDao().getCategoryById("cat_phone") == null) {
            DeviceCategory phone = new DeviceCategory();
            phone.id = "cat_phone";
            phone.name = "Phone";
            db.deviceCategoryDao().insert(phone);

            DeviceCategory laptop = new DeviceCategory();
            laptop.id = "cat_laptop";
            laptop.name = "Laptop";
            db.deviceCategoryDao().insert(laptop);

            DeviceCategory tablet = new DeviceCategory();
            tablet.id = "cat_tablet";
            tablet.name = "Tablet";
            db.deviceCategoryDao().insert(tablet);

            DeviceCategory desktop = new DeviceCategory();
            desktop.id = "cat_desktop";
            desktop.name = "Desktop PC";
            db.deviceCategoryDao().insert(desktop);

            DeviceCategory smartwatch = new DeviceCategory();
            smartwatch.id = "cat_smartwatch";
            smartwatch.name = "Smartwatch";
            db.deviceCategoryDao().insert(smartwatch);

            // Phone services
            insertService(db, "svc_phone_screen", "cat_phone", "Screen Replacement", 8500);
            insertService(db, "svc_phone_battery", "cat_phone", "Battery Replacement", 4500);
            insertService(db, "svc_phone_water", "cat_phone", "Water Damage Repair", 6000);
            insertService(db, "svc_phone_charging", "cat_phone", "Charging Port Repair", 3500);
            insertService(db, "svc_phone_camera", "cat_phone", "Camera Module Replacement", 7000);
            insertService(db, "svc_phone_speaker", "cat_phone", "Speaker/Microphone Repair", 3000);
            insertService(db, "svc_phone_software", "cat_phone", "Software/OS Troubleshooting", 2000);

            // Laptop services
            insertService(db, "svc_laptop_screen", "cat_laptop", "Screen Replacement", 15000);
            insertService(db, "svc_laptop_keyboard", "cat_laptop", "Keyboard Replacement", 6000);
            insertService(db, "svc_laptop_hdd", "cat_laptop", "Storage Upgrade (HDD/SSD)", 9000);
            insertService(db, "svc_laptop_battery", "cat_laptop", "Battery Replacement", 8000);
            insertService(db, "svc_laptop_ram", "cat_laptop", "RAM Upgrade", 6500);
            insertService(db, "svc_laptop_fan", "cat_laptop", "Cooling Fan Repair/Cleaning", 3500);
            insertService(db, "svc_laptop_motherboard", "cat_laptop", "Motherboard Repair", 20000);

            // Tablet services
            insertService(db, "svc_tablet_screen", "cat_tablet", "Screen Replacement", 11000);
            insertService(db, "svc_tablet_battery", "cat_tablet", "Battery Replacement", 5500);
            insertService(db, "svc_tablet_charging", "cat_tablet", "Charging Port Repair", 4000);

            // Desktop PC services
            insertService(db, "svc_desktop_psu", "cat_desktop", "Power Supply Replacement", 7500);
            insertService(db, "svc_desktop_gpu", "cat_desktop", "Graphics Card Diagnosis/Repair", 12000);
            insertService(db, "svc_desktop_assembly", "cat_desktop", "Custom PC Assembly", 5000);
            insertService(db, "svc_desktop_virus", "cat_desktop", "Virus Removal & OS Cleanup", 3000);

            // Smartwatch services
            insertService(db, "svc_watch_screen", "cat_smartwatch", "Screen Replacement", 6500);
            insertService(db, "svc_watch_battery", "cat_smartwatch", "Battery Replacement", 4000);
            insertService(db, "svc_watch_strap", "cat_smartwatch", "Strap/Band Repair", 1500);
        }
    }

    private static void insertService(AppDatabase db, String id, String categoryId, String name, double price) {
        RepairService service = new RepairService();
        service.id = id;
        service.deviceCategoryId = categoryId;
        service.name = name;
        service.basePrice = price;
        db.repairServiceDao().insert(service);
    }
}