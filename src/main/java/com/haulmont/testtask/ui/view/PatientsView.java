package com.haulmont.testtask.ui.view;

import com.haulmont.testtask.dao.PatientDAO;
import com.haulmont.testtask.model.Patient;
import com.haulmont.testtask.ui.forms.PatientEditWindow;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

public class PatientsView extends VerticalLayout implements View {

    private final Grid grid;
    private final PatientDAO patientDAO;
    private Patient selectedPatient;

    public PatientsView() {
        this.patientDAO = PatientDAO.getInstance();

        Button addButton = new Button("Добавить", FontAwesome.PLUS);
        addButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        addButton.addClickListener(e -> showEditWindow());

        Button editButton = new Button("Изменить", FontAwesome.EDIT);
        editButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        editButton.setEnabled(false);
        editButton.addClickListener(e -> showEditWindow(selectedPatient));

        Button deleteButton = new Button("Удалить", FontAwesome.TRASH);
        deleteButton.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteButton.setEnabled(false);
        deleteButton.addClickListener(e -> delete());

        HorizontalLayout buttons = new HorizontalLayout(addButton, editButton, deleteButton);
        buttons.setSpacing(true);

        grid = new Grid();
        grid.setColumns("lastName", "firstName", "patronymic", "phoneNumber");
        grid.getColumn("lastName").setHeaderCaption("Фамилия");
        grid.getColumn("firstName").setHeaderCaption("Имя");
        grid.getColumn("patronymic").setHeaderCaption("Отчество");
        grid.getColumn("phoneNumber").setHeaderCaption("Телефон");
        grid.setSizeFull();
        grid.addSelectionListener(event -> {
            if (event.getSelected().isEmpty()) {
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            } else {
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
                selectedPatient = (Patient) event.getSelected().iterator().next();
            }
        });


        this.addComponents(grid, buttons);
        this.setExpandRatio(grid, 1);
        this.setSizeFull();
        this.setSpacing(true);
        this.setMargin(true);
        update();
    }

    private void showEditWindow() {
        PatientEditWindow patientEditWindow = new PatientEditWindow(this);
        UI.getCurrent().addWindow(patientEditWindow);
    }

    private void showEditWindow(Patient selectedPatient) {
        PatientEditWindow patientEditWindow = new PatientEditWindow(selectedPatient, this);
        UI.getCurrent().addWindow(patientEditWindow);
    }

    private void delete() {
        try {
            patientDAO.delete(selectedPatient.getId());
            update();
        } catch (SQLIntegrityConstraintViolationException e) {
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }

    }


    public void update() {
        ArrayList<Patient> patients = patientDAO.readAll();
        grid.setContainerDataSource(new BeanItemContainer<>(Patient.class, patients));
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
