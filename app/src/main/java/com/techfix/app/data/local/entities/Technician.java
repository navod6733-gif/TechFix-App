package com.techfix.app.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "technicians")
public class Technician {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String branchId;
    public String specialty;
}
