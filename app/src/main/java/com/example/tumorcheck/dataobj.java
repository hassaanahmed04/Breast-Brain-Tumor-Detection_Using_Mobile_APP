package com.example.tumorcheck;

public class dataobj {
    long reportNo;
    String patientName;
    long date;
    String gender;
    double age;
    double phoneNo;
    double cnic;
    String address;
    String tumorResult;

    //Registeration details variables





    public dataobj() {
    }



    public long getReportNo() {
        return reportNo;
    }



    public void setReportNo(long reportNo) {
        this.reportNo = reportNo;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public double getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(double phoneNo) {
        this.phoneNo = phoneNo;
    }

    public double getCnic() {
        return cnic;
    }

    public void setCnic(double cnic) {
        this.cnic = cnic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTumorResult() {
        return tumorResult;
    }

    public void setTumorResult(String tumorResult) {
        this.tumorResult = tumorResult;
    }

}
