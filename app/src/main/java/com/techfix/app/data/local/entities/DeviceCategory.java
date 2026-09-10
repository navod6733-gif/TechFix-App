package com.techfix.app.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_categories")
public class DeviceCategory {
    @PrimaryKey
    @NonNull
    public String id;
    public String name; // e.g. "Phone", "Laptop"
}