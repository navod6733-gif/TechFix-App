package com.techfix.app.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "technicians")
public class Technician {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String branchId;
    public String specialty;
}
