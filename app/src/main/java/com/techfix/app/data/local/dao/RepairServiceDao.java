package com.techfix.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.techfix.app.data.local.entities.RepairService;

import java.util.List;

@Dao
public interface RepairServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RepairService service);

    @Update
    void update(RepairService service);

    @Delete
    void delete(RepairService service);

    @Query("SELECT * FROM repair_services")
    LiveData<List<RepairService>> getAllServices();

    @Query("SELECT * FROM repair_services WHERE deviceCategoryId = :categoryId")
    LiveData<List<RepairService>> getServicesForCategory(String categoryId);

    @Query("SELECT * FROM repair_services WHERE id = :id")
    RepairService getServiceById(String id);

    @Query("SELECT * FROM repair_services")
    List<RepairService> getAllServicesSync();
}
