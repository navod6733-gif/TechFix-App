package com.techfix.app.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "payments")
public class Payment {
    @PrimaryKey
    @NonNull
    public String id;
    public String appointmentId;
    public double amount;
    public String status;  // "Pending" / "Paid"
    public long date;
}
