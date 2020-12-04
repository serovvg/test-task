package com.haulmont.testtask.model;

public class Patient {
    private long id;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String phoneNumber;

    public Patient() {
    }

    public Patient(String lastName, String firstName, String patronymic, String phoneNumber) {
        this(-1, lastName,firstName, patronymic, phoneNumber);
    }

    public Patient(long id, String lastName, String firstName, String patronymic, String phoneNumber) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.phoneNumber = phoneNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String toString() {
        return lastName + ' ' + firstName.substring(0,1) + ". " + (patronymic.length()>0 ? patronymic.substring(0,1)+ "." : "");
    }

    @Override
    public Patient clone() {
        return new Patient(id, lastName,firstName, patronymic, phoneNumber);
    }
}
