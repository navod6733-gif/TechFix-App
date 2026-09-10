package com.techfix.app.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "repair_services")
public class RepairService {
    @PrimaryKey
    @NonNull
    public String id;
    public String deviceCategoryId;
    public String name;       // e.g. "Screen Replacement"
    public double basePrice;
}
