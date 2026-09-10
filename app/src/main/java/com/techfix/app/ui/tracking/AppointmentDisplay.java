package com.techfix.app.ui.tracking;

import com.techfix.app.data.local.entities.Appointment;

public class AppointmentDisplay {
    public Appointment appointment;
    public String serviceName;
    public String branchName;

    public AppointmentDisplay(Appointment appointment, String serviceName, String branchName) {
        this.appointment = appointment;
        this.serviceName = serviceName;
        this.branchName = branchName;
    }
}