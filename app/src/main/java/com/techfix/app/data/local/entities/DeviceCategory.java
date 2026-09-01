package com.techfix.app.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "device_categories")
public class DeviceCategory {
    @PrimaryKey
    @NonNull
    public String id;
    public String name; // e.g. "Phone", "Laptop"
}