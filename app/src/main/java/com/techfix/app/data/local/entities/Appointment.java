package com.techfix.app.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments")
public class Appointment {
    @PrimaryKey
    @NonNull
    public String id;
    public String userId;
    public String branchId;
    public String repairServiceId;
    public String technicianId;      // null until assigned
    public String deviceImageUrl;    // null until photo attached
    public String status;            // "Pending" / "Assigned" / "InProgress" / "Completed"
    public long requestDate;         // System.currentTimeMillis()
    public String paymentId;         // null until payment made
}