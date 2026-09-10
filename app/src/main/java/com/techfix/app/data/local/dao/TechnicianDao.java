package com.techfix.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.techfix.app.data.local.entities.Technician;

import java.util.List;

@Dao
public interface TechnicianDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Technician technician);

    @Update
    void update(Technician technician);

    @Delete
    void delete(Technician technician);

    @Query("SELECT * FROM technicians WHERE branchId = :branchId")
    LiveData<List<Technician>> getTechniciansForBranch(String branchId);

    @Query("SELECT * FROM technicians WHERE id = :id")
    Technician getTechnicianById(String id);

    @Query("SELECT * FROM technicians")
    List<Technician> getAllTechniciansSync();
}
