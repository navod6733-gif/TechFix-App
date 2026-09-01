package com.techfix.app.data.local;


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.techfix.app.data.local.dao.*;
import com.techfix.app.data.local.entities.*;

@Database(
        entities = {
                Branch.class,
                DeviceCategory.class,
                RepairService.class,
                Technician.class,
                SparePart.class,
                AppUser.class,
                Appointment.class,
                Payment.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BranchDao branchDao();
    public abstract DeviceCategoryDao deviceCategoryDao();
    public abstract RepairServiceDao repairServiceDao();
    public abstract TechnicianDao technicianDao();
    public abstract SparePartDao sparePartDao();
    public abstract AppUserDao appUserDao();
    public abstract AppointmentDao appointmentDao();
    public abstract PaymentDao paymentDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "techfix_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}