package com.example.tumorcheck;

public class regdataobj {
    public String Department,PhoneNo,Hospital_type;

//constructor
    public regdataobj(){};

    public regdataobj( String h_type, String h_dep, String h_phone) {

        this.Hospital_type = h_type;

        this.Department = h_dep;
        this.PhoneNo = h_phone;

    }
}
