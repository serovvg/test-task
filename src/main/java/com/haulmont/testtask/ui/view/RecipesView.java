package com.haulmont.testtask.ui.view;

import com.haulmont.testtask.dao.RecipeDAO;
import com.haulmont.testtask.model.Priority;
import com.haulmont.testtask.model.Recipe;
import com.haulmont.testtask.ui.forms.RecipeEditWindow;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class RecipesView extends VerticalLayout implements View {

    private final Grid grid;
    private final RecipeDAO recipeDAO;
    private Recipe selectedRecipe;
    private final TextField descriptionFilterText;
    private final ComboBox priorityFilterText;
    private final TextField patientFilterText;
    private String descriptionFilter = "";
    private Priority priorityFilter = null;
    private String patientFilter = "";

    public RecipesView( RecipeDAO recipeDAO){
        this.recipeDAO = recipeDAO;

        descriptionFilterText = new TextField();
        descriptionFilterText.setInputPrompt("Описание");
        descriptionFilterText.addTextChangeListener(e -> {
            descriptionFilter = e.getText();
            update();
        });

        priorityFilterText = new ComboBox();
        priorityFilterText.setInputPrompt("Приоритет");
        priorityFilterText.addItems(Priority.STATIUM, Priority.CITO, Priority.NORMAL);
        priorityFilterText.addValueChangeListener(e ->{
            priorityFilter = (Priority) e.getProperty().getValue();
            update();
        });

        patientFilterText = new TextField();
        patientFilterText.setInputPrompt("Пациент");
        patientFilterText.addTextChangeListener(e -> {
            patientFilter = e.getText();
            update();
        });

        HorizontalLayout filters = new HorizontalLayout(descriptionFilterText, priorityFilterText, patientFilterText);
        filters.setSpacing(true);

        Button addButton = new Button("Добавить", FontAwesome.PLUS);
        addButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        addButton.addClickListener(e -> showEditWindow());

        Button editButton = new Button("Изменить", FontAwesome.EDIT);
        editButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        editButton.setEnabled(false);
        editButton.addClickListener(e -> showEditWindow(selectedRecipe));

        Button deleteButton = new Button("Удалить", FontAwesome.TRASH);
        deleteButton.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteButton.setEnabled(false);
        deleteButton.addClickListener(e -> delete());

        HorizontalLayout buttons = new HorizontalLayout(addButton, editButton, deleteButton);
        buttons.setSpacing(true);

        grid = new Grid();
        grid.setColumns("description", "patient", "doctor", "creationDate", "validity", "priority");
        grid.getColumn("description").setHeaderCaption("Описание");
        grid.getColumn("patient").setHeaderCaption("Пациент");
        grid.getColumn("doctor").setHeaderCaption("Врач");
        grid.getColumn("creationDate").setHeaderCaption("Дата создания");
        grid.getColumn("validity").setHeaderCaption("Срок действия");
        grid.getColumn("priority").setHeaderCaption("Приоритет");
        grid.setSizeFull();
        grid.addSelectionListener(event -> {
            if (event.getSelected().isEmpty()){
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            } else {
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
                selectedRecipe = (Recipe) event.getSelected().iterator().next();
            }
        });


        this.addComponents(filters, grid, buttons);
        this.setExpandRatio(grid, 1);
        this.setSizeFull();
        this.setSpacing(true);
        this.setMargin(true);
        update();
    }

    private void showEditWindow() {
        RecipeEditWindow recipeEditWindow = new RecipeEditWindow(this);
        UI.getCurrent().addWindow(recipeEditWindow);
    }

    private void showEditWindow(Recipe selectedRecipe) {
        RecipeEditWindow recipeEditWindow = new RecipeEditWindow(selectedRecipe, this);
        UI.getCurrent().addWindow(recipeEditWindow);
    }

    private void delete() {
        recipeDAO.delete(selectedRecipe.getId());
        update();
    }


    public void update(){
        ArrayList<Recipe> recipes = (ArrayList<Recipe>) recipeDAO.readAll()
                .stream()
                .filter(entity -> {
                    return entity.getDescription().toLowerCase().contains(descriptionFilter.toLowerCase()) &&
                            (priorityFilter == null || entity.getPriority() == priorityFilter) &&
                            entity.getPatient().toString().toLowerCase().contains(patientFilter.toLowerCase());
                })
                .collect(Collectors.toList());
        grid.setContainerDataSource(new BeanItemContainer<>(Recipe.class, recipes));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
