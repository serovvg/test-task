package com.haulmont.testtask.ui.view;

import com.haulmont.testtask.dao.DoctorDAO;
import com.haulmont.testtask.model.Doctor;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;

import java.util.ArrayList;

public class DocStatsWindow extends Window {
    public DocStatsWindow() {
        DoctorDAO doctorDAO = DoctorDAO.getInstance();
        Label label = new Label("Статистика");
        label.setSizeUndefined();
        ArrayList<Doctor> doctors = doctorDAO.readAll();

        Grid grid = new Grid();
        grid.setContainerDataSource(new BeanItemContainer<>(Doctor.class, doctors));
        grid.getColumn("specialization").setHeaderCaption("Специализация");
        grid.setColumns("specialization", "lastName", "firstName", "patronymic", "recipesQuantity");
        grid.getColumn("lastName").setHeaderCaption("Фамилия");
        grid.getColumn("firstName").setHeaderCaption("Имя");
        grid.getColumn("patronymic").setHeaderCaption("Отчество");
        grid.getColumn("recipesQuantity").setHeaderCaption("Количество рецептов");
        grid.setWidth(95, Unit.PERCENTAGE);
        grid.setHeight(100, Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout(label, grid);
        content.setExpandRatio(grid,1);
        content.setSizeFull();
        content.setComponentAlignment(label, Alignment.TOP_CENTER);
        content.setComponentAlignment(grid, Alignment.BOTTOM_CENTER);
        content.setMargin(true);
        content.setSpacing(true);

        setContent(content);
        setHeight(80, Unit.PERCENTAGE);
        setWidth(80, Unit.PERCENTAGE);

        setClosable(true);
        setDraggable(false);
        setResizable(false);
        setModal(true);
    }
}