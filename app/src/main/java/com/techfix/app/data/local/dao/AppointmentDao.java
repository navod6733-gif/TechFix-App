package com.techfix.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.techfix.app.data.local.entities.Appointment;

import java.util.List;

@Dao
public interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Appointment appointment);

    @Update
    void update(Appointment appointment);

    @Delete
    void delete(Appointment appointment);

    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY requestDate DESC")
    LiveData<List<Appointment>> getAppointmentsForUser(String userId);

    @Query("SELECT * FROM appointments WHERE id = :id")
    Appointment getAppointmentById(String id);

    @Query("SELECT * FROM appointments")
    LiveData<List<Appointment>> getAllAppointments();
}
