package com.techfix.app.ui.payment;

import com.techfix.app.data.local.entities.Appointment;
import com.techfix.app.data.local.entities.Payment;

public class PaymentDisplay {
    public Appointment appointment;
    public String serviceName;
    public String branchName;
    public double amount;
    public Payment existingPayment; // null if not yet paid

    public PaymentDisplay(Appointment appointment, String serviceName, String branchName,
                          double amount, Payment existingPayment) {
        this.appointment = appointment;
        this.serviceName = serviceName;
        this.branchName = branchName;
        this.amount = amount;
        this.existingPayment = existingPayment;
    }
}