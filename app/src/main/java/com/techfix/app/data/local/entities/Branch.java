package com.techfix.app.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "branches")
public class Branch {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String address;
    public double latitude;
    public double longitude;
    public String phone;
}
