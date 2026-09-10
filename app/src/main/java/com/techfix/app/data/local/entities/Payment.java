package com.techfix.app.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
