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
        if (db.branchDao().getBranchById("branch_colombo") == null) {
            Branch colombo = new Branch();
            colombo.id = "branch_colombo";
            colombo.name = "TechFix Colombo";
            colombo.address = "123 Galle Road, Colombo 03";
            colombo.latitude = 6.9271;
            colombo.longitude = 79.8612;
            colombo.phone = "011-2345678";
            db.branchDao().insert(colombo);
        }

        if (db.branchDao().getBranchById("branch_galle") == null) {
            Branch galle = new Branch();
            galle.id = "branch_galle";
            galle.name = "TechFix Galle";
            galle.address = "45 Main Street, Galle Fort";
            galle.latitude = 6.0535;
            galle.longitude = 80.2210;
            galle.phone = "091-2233445";
            db.branchDao().insert(galle);
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

            insertService(db, "svc_phone_screen", "cat_phone", "Screen Replacement", 8500);
            insertService(db, "svc_phone_battery", "cat_phone", "Battery Replacement", 4500);
            insertService(db, "svc_phone_water", "cat_phone", "Water Damage Repair", 6000);

            insertService(db, "svc_laptop_screen", "cat_laptop", "Screen Replacement", 15000);
            insertService(db, "svc_laptop_keyboard", "cat_laptop", "Keyboard Replacement", 6000);
            insertService(db, "svc_laptop_hdd", "cat_laptop", "Storage Upgrade", 9000);

            insertService(db, "svc_tablet_screen", "cat_tablet", "Screen Replacement", 11000);
            insertService(db, "svc_tablet_battery", "cat_tablet", "Battery Replacement", 5500);
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