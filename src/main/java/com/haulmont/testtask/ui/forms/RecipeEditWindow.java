package com.haulmont.testtask.ui.forms;

import com.haulmont.testtask.dao.DoctorDAO;
import com.haulmont.testtask.dao.PatientDAO;
import com.haulmont.testtask.dao.RecipeDAO;
import com.haulmont.testtask.model.Doctor;
import com.haulmont.testtask.model.Patient;
import com.haulmont.testtask.model.Priority;
import com.haulmont.testtask.model.Recipe;
import com.haulmont.testtask.ui.view.RecipesView;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.ShortRangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class RecipeEditWindow extends Window {
    private final TextArea description = new TextArea ("Описание");
    private final ComboBox patient = new ComboBox("Пациент");
    private final ComboBox doctor = new ComboBox("Врач");
    private final DateField creationDate = new DateField("Дата создания");
    private final TextField validity = new TextField("Срок действия");
    private final ComboBox priority = new ComboBox("Приоритет");
    private final Button okButton;


    private final RecipeDAO recipeDAO;
    private final PatientDAO patientDAO;
    private final DoctorDAO doctorDAO;
    private Recipe recipe;
    private RecipesView recipesView;

    public RecipeEditWindow(){
        this.recipeDAO = RecipeDAO.getInstance();
        this.patientDAO = PatientDAO.getInstance();
        this.doctorDAO = DoctorDAO.getInstance();

        description.setSizeFull();

        patient.addItems(patientDAO.readAll());
        patient.setSizeFull();
        patient.setNullSelectionAllowed(false);

        doctor.addItems(doctorDAO.readAll());
        doctor.setSizeFull();
        doctor.setNullSelectionAllowed(false);

        creationDate.setSizeFull();
        validity.setSizeFull();


        priority.addItems(Priority.STATIUM, Priority.CITO, Priority.NORMAL);
        priority.setSizeFull();
        priority.setNullSelectionAllowed(false);


        VerticalLayout content = new VerticalLayout();

        okButton = new Button("ОК", FontAwesome.CHECK);
        okButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);


        Button cancelButton = new Button("Отменить", FontAwesome.CLOSE);
        cancelButton.setStyleName(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(e -> this.close());

        HorizontalLayout buttons = new HorizontalLayout(okButton, cancelButton);
        buttons.setSpacing(true);

        description.addValidator(new StringLengthValidator("Поле не может быть пустым!", 1, 10000, false));

        validity.setNullRepresentation("");
        validity.setNullSettingAllowed(true);
        validity.setConverter(Short.class);
        validity.addValidator(new NullValidator("Поле не может быть пустым!", false));
        validity.addValidator(new ShortRangeValidator("Поле не может содержать значение меньше 1 и больше 365!", (short) 1, (short)365));

        VerticalLayout main = new VerticalLayout(description, patient, doctor, creationDate, validity, priority, buttons);
        main.setSizeFull();
        main.setSpacing(true);
        main.setMargin(new MarginInfo(true,true,true,true));

        content.addComponents(main);
        setContent(content);

        setWidth(40, Unit.PERCENTAGE);
        setClosable(true);
        setDraggable(false);
        setResizable(false);
        setModal(true);
    }

    public RecipeEditWindow(Recipe recipe, RecipesView recipesView) {
        this();
        this.recipe = recipe.clone();
        this.recipesView = recipesView;

        okButton.addClickListener(e -> save());
        BeanFieldGroup.bindFieldsUnbuffered(this.recipe, this);
    }

    public RecipeEditWindow(RecipesView recipesView) {
        this();
        this.recipesView = recipesView;

        okButton.addClickListener(e -> add());
    }


    private boolean validate(){
        try {
            description.validate();
        }catch (Validator.InvalidValueException e){
            Notification.show("Поле \"Описание\"" + (e.getCauses().length>0 ? e.getCauses()[0]: e)
                    .getMessage().substring(4), Notification.Type.WARNING_MESSAGE);
            description.setValidationVisible(true);
            return false;
        }

        try {
            validity.validate();
        }catch (Validator.InvalidValueException e){
            if (e.getMessage().equals("Could not convert value to Short")){
                Notification.show("Поле \"Срок действия\" может содержать только цифры!", Notification.Type.WARNING_MESSAGE);
                return false;
            }
            Notification.show("Поле \"Срок действия\"" + (e.getCauses().length>0 ? e.getCauses()[0]: e)
                    .getMessage().substring(4), Notification.Type.WARNING_MESSAGE);
            validity.setValidationVisible(true);
            return false;
        }
        return true;
    }

    public void save(){
        if (validate()){
            recipeDAO.update(recipe);
            recipesView.update();
            this.close();
        }
    }
    public void add(){
        if (validate()){
            this.recipe = new Recipe((String) description.getValue(), (Patient) patient.getValue(),
                    (Doctor) doctor.getValue(), new java.sql.Date(creationDate.getValue().getTime()),
                    Short.parseShort(validity.getValue()), (Priority) priority.getValue());
            recipeDAO.create(this.recipe);
            recipesView.update();
            this.close();
        }
    }
}

