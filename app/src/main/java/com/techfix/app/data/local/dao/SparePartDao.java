package com.techfix.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.techfix.app.data.local.entities.SparePart;
import java.util.List;

@Dao
public interface SparePartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SparePart part);

    @Update
    void update(SparePart part);

    @Delete
    void delete(SparePart part);

    @Query("SELECT * FROM spare_parts WHERE branchId = :branchId")
    LiveData<List<SparePart>> getPartsForBranch(String branchId);

    @Query("SELECT * FROM spare_parts WHERE id = :id")
    SparePart getPartById(String id);
}
