package com.techfix.app.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class AppUser {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String email;
    public String phone;
    public String passwordHash; // never store plain text passwords
}