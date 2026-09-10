package com.techfix.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.techfix.app.data.local.entities.Branch;

import java.util.List;

@Dao
public interface BranchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Branch branch);

    @Update
    void update(Branch branch);

    @Delete
    void delete(Branch branch);

    @Query("SELECT * FROM branches")
    LiveData<List<Branch>> getAllBranches();

    @Query("SELECT * FROM branches WHERE id = :id")
    Branch getBranchById(String id);

    @Query("SELECT * FROM branches")
    List<Branch> getAllBranchesSync();
}