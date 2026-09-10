package com.techfix.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.techfix.app.data.local.entities.DeviceCategory;

import java.util.List;

@Dao
public interface DeviceCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DeviceCategory category);

    @Update
    void update(DeviceCategory category);

    @Delete
    void delete(DeviceCategory category);

    @Query("SELECT * FROM device_categories")
    LiveData<List<DeviceCategory>> getAllCategories();

    @Query("SELECT * FROM device_categories WHERE id = :id")
    DeviceCategory getCategoryById(String id);

    @Query("SELECT * FROM device_categories")
    List<DeviceCategory> getAllCategoriesSync();
}