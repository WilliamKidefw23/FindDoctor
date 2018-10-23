package com.kid.william.finddoctor.Model.entidad;

public class DoctorInfo {

    private String email;
    private String name;
    private String lastName;
    private int phone;
    private String password;
    private String speciality;

    protected DoctorInfo(){

    }

    public DoctorInfo(String email, String name, String lastName, int phone, String password, String speciality) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.password = password;
        this.speciality = speciality;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
}
