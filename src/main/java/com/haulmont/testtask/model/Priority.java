package com.haulmont.testtask.model;

public enum Priority {
    NORMAL("Нормальный"),
    CITO("Cito (Срочный)"),
    STATIUM("Statim (Немедленный)");

    private String title;
    private String dbTitle;

    Priority(String title) {
        this.title = title;
        switch (title){
            case "Нормальный":
                dbTitle = "NORMAL";
                break;
            case "Cito (Срочный)":
                dbTitle = "CITO";
                break;
            case "Statim (Немедленный)":
                dbTitle = "STATIUM";
                break;
        }
    }

    public String getDbTitle() {
        return dbTitle;
    }

    @Override
    public String toString() {
        return title;
    }
}
