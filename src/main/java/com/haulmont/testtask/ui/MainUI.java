package com.haulmont.testtask.ui;

import com.haulmont.testtask.dao.RecipeDAO;
import com.haulmont.testtask.ui.view.DoctorsView;
import com.haulmont.testtask.ui.view.PatientsView;
import com.haulmont.testtask.ui.view.RecipesView;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

@Theme(ValoTheme.THEME_NAME)
public class MainUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        RecipeDAO recipeDAO = RecipeDAO.getInstance();


        Label menuTitle = new Label("Меню");
        menuTitle.addStyleName(ValoTheme.MENU_TITLE);

        Button doctorsMenuItem = new Button("Врачи");
        doctorsMenuItem.addStyleName(ValoTheme.BUTTON_LINK);
        doctorsMenuItem.addStyleName(ValoTheme.MENU_ITEM);
        doctorsMenuItem.addClickListener(clickEvent -> getNavigator().navigateTo("doctors"));

        Button patientsMenuItem = new Button("Пациенты");
        patientsMenuItem.addStyleName(ValoTheme.BUTTON_LINK);
        patientsMenuItem.addStyleName(ValoTheme.MENU_ITEM);
        patientsMenuItem.addClickListener(clickEvent -> getNavigator().navigateTo("patients"));

        Button recipesMenuItem = new Button("Рецепты");
        recipesMenuItem.addStyleName(ValoTheme.BUTTON_LINK);
        recipesMenuItem.addStyleName(ValoTheme.MENU_ITEM);
        recipesMenuItem.addClickListener(clickEvent -> getNavigator().navigateTo("recipes"));


        CssLayout menu = new CssLayout(menuTitle, doctorsMenuItem, patientsMenuItem, recipesMenuItem);
        menu.addStyleName(ValoTheme.MENU_ROOT);
        menu.setSizeUndefined();

        VerticalLayout viewContainer = new VerticalLayout();
        viewContainer.setSizeFull();

        HorizontalLayout main = new HorizontalLayout(menu, viewContainer);
        main.setSizeFull();
        main.setExpandRatio(viewContainer, 1);

        setContent(main);
        setSizeFull();

        Navigator navigator = new Navigator(this, viewContainer);
        navigator.addView("", new DoctorsView());
        navigator.addView("doctors", new DoctorsView());
        navigator.addView("patients", new PatientsView());
        navigator.addView("recipes", new RecipesView(recipeDAO));

        navigator.navigateTo("doctors");

    }
}

