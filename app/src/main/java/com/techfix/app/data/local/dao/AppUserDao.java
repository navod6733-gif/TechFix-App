package com.techfix.app.data.local.dao;

import androidx.room.*;
import com.techfix.app.data.local.entities.AppUser;

@Dao
public interface AppUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppUser user);

    @Update
    void update(AppUser user);

    @Query("SELECT * FROM users WHERE id = :id")
    AppUser getUserById(String id);

    @Query("SELECT * FROM users WHERE email = :email")
    AppUser getUserByEmail(String email);
}
