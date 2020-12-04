package com.haulmont.testtask.ui.view;

import com.haulmont.testtask.dao.DoctorDAO;
import com.haulmont.testtask.model.Doctor;
import com.haulmont.testtask.ui.forms.DoctorEditWindow;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

public class DoctorsView extends VerticalLayout implements View {


    private final Grid grid;
    private final DoctorDAO doctorDAO;
    private Doctor selectedDoctor;

    public DoctorsView(){
        this.doctorDAO = DoctorDAO.getInstance();

        Button addButton = new Button("Добавить", FontAwesome.PLUS);
        addButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        addButton.addClickListener(e -> showEditWindow());

        Button editButton = new Button("Изменить", FontAwesome.EDIT);
        editButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        editButton.setEnabled(false);
        editButton.addClickListener(e -> showEditWindow(selectedDoctor));

        Button deleteButton = new Button("Удалить", FontAwesome.TRASH);
        deleteButton.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteButton.setEnabled(false);
        deleteButton.addClickListener(e -> delete());

        Button docStatsButton = new Button("Показать статистику", FontAwesome.BAR_CHART);
        docStatsButton.addClickListener(e -> showStats());

        HorizontalLayout crudButtons = new HorizontalLayout(addButton, editButton, deleteButton);
        crudButtons.setSpacing(true);

        HorizontalLayout buttons = new HorizontalLayout(crudButtons, docStatsButton);
        buttons.setComponentAlignment(docStatsButton, Alignment.MIDDLE_RIGHT);
        buttons.setWidth(100,Unit.PERCENTAGE);

        grid = new Grid();
        grid.setColumns("lastName", "firstName", "patronymic", "specialization");
        grid.getColumn("lastName").setHeaderCaption("Фамилия");
        grid.getColumn("firstName").setHeaderCaption("Имя");
        grid.getColumn("patronymic").setHeaderCaption("Отчество");
        grid.getColumn("specialization").setHeaderCaption("Специализация");
        grid.setSizeFull();
        grid.addSelectionListener(event -> {
            if (event.getSelected().isEmpty()){
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            } else {
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
                selectedDoctor = (Doctor) event.getSelected().iterator().next();
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
        DoctorEditWindow doctorEditWindow = new DoctorEditWindow(this);
        UI.getCurrent().addWindow(doctorEditWindow);
    }

    private void showEditWindow(Doctor selectedDoctor) {
        DoctorEditWindow doctorEditWindow = new DoctorEditWindow(selectedDoctor, this);
        UI.getCurrent().addWindow(doctorEditWindow);
    }

    private void showStats(){
        DocStatsWindow doctorEditWindow = new DocStatsWindow();
        UI.getCurrent().addWindow(doctorEditWindow);
    }


    private void delete() {
        try {
            doctorDAO.delete(selectedDoctor.getId());
            update();
        }catch (SQLIntegrityConstraintViolationException e){
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }

    }


    public void update(){
        ArrayList<Doctor> doctors = doctorDAO.readAll();
        grid.setContainerDataSource(new BeanItemContainer<>(Doctor.class, doctors));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}