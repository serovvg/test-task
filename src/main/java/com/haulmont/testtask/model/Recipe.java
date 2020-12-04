package com.haulmont.testtask.model;

import java.sql.Date;

public class Recipe {
    private long id;
    private String description;
    private Patient patient;
    private Doctor doctor;
    private Date creationDate;
    private short validity;
    private Priority priority;

    public Recipe() {
    }

    public Recipe( String description, Patient patient, Doctor doctor, Date creationDate, short validity, Priority priority) {
        this(-1, description, patient, doctor, creationDate, validity, priority);
    }

    public Recipe(long id, String description, Patient patient, Doctor doctor, Date creationDate, short validity, Priority priority) {
        this.id = id;
        this.description = description;
        this.patient = patient;
        this.doctor = doctor;
        this.creationDate = creationDate;
        this.validity = validity;
        this.priority = priority;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public short getValidity() {
        return validity;
    }

    public void setValidity(short validity) {
        this.validity = validity;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", patientId=" + patient.getId() +
                ", doctorId=" + doctor.getId() +
                ", creationDate=" + creationDate +
                ", validity=" + validity +
                ", priority=" + priority +
                '}';
    }

    @Override
    public Recipe clone() {
        return new Recipe(id, description, patient, doctor, creationDate, validity, priority);
    }
}
