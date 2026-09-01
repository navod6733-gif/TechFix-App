package com.techfix.app.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class AppUser {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String email;
    public String phone;
}
