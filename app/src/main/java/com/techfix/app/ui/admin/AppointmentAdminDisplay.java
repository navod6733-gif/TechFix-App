package com.techfix.app.ui.admin;

import com.techfix.app.data.local.entities.Appointment;
import com.techfix.app.data.local.entities.Technician;

import java.util.List;

public class AppointmentAdminDisplay {
    public Appointment appointment;
    public String serviceName;
    public String branchName;
    public String customerName;
    public List<Technician> availableTechnicians;

    public AppointmentAdminDisplay(Appointment appointment, String serviceName,
                                   String branchName, String customerName,
                                   List<Technician> availableTechnicians) {
        this.appointment = appointment;
        this.serviceName = serviceName;
        this.branchName = branchName;
        this.customerName = customerName;
        this.availableTechnicians = availableTechnicians;
    }
}