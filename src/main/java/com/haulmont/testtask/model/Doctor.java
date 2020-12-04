package com.haulmont.testtask.model;

public class Doctor {
    private long id;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String specialization;
    private int recipesQuantity;

    public Doctor() {
    }

    public Doctor(String lastName, String firstName, String patronymic, String specialization) {
        this(-1, lastName,firstName, patronymic, specialization);
    }

    public Doctor(long id, String lastName, String firstName, String patronymic, String specialization) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.specialization = specialization;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getRecipesQuantity() {
        return recipesQuantity;
    }

    public void setRecipesQuantity(int recipesQuantity) {
        this.recipesQuantity = recipesQuantity;
    }

    @Override
    public String toString() {
        return lastName + ' ' + firstName.substring(0,1) + ". " + (patronymic.length()>0 ? patronymic.substring(0,1)+ "., " : " ") + specialization;
    }

    @Override
    public Doctor clone() {
        return new Doctor(id, lastName,firstName, patronymic, specialization);
    }

}
