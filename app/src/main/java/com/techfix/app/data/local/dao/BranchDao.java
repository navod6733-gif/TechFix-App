package com.techfix.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
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
}