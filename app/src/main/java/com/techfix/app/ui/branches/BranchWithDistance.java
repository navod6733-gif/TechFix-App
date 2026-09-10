package com.techfix.app.ui.branches;

import com.techfix.app.data.local.entities.Branch;

public class BranchWithDistance {
    public Branch branch;
    public float distanceKm;

    public BranchWithDistance(Branch branch, float distanceKm) {
        this.branch = branch;
        this.distanceKm = distanceKm;
    }
}