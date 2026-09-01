package com.techfix.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.techfix.app.data.local.entities.Payment;
import java.util.List;

@Dao
public interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Payment payment);

    @Update
    void update(Payment payment);

    @Query("SELECT * FROM payments WHERE appointmentId = :appointmentId")
    Payment getPaymentForAppointment(String appointmentId);

    @Query("SELECT * FROM payments")
    LiveData<List<Payment>> getAllPayments();
}
