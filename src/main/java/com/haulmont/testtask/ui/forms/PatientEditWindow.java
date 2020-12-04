package com.haulmont.testtask.ui.forms;

import com.haulmont.testtask.dao.PatientDAO;
import com.haulmont.testtask.model.Patient;
import com.haulmont.testtask.ui.view.PatientsView;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class PatientEditWindow extends Window {
    private final TextField lastName = new TextField("Фамилия");
    private final TextField firstName = new TextField("Имя");
    private final TextField patronymic = new TextField("Отчество");
    private final TextField phoneNumber = new TextField("Телефон");
    private final Button okButton;


    private final PatientDAO patientDAO;
    private Patient patient;
    private PatientsView patientsView;

    public PatientEditWindow(){
        this.patientDAO = PatientDAO.getInstance();

        VerticalLayout content = new VerticalLayout();

        okButton = new Button("ОК", FontAwesome.CHECK);
        okButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);


        Button cancelButton = new Button("Отменить", FontAwesome.CLOSE);
        cancelButton.setStyleName(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(e -> this.close());

        HorizontalLayout buttons = new HorizontalLayout(okButton, cancelButton);
        buttons.setSpacing(true);

        lastName.addValidator(new StringLengthValidator("Поле не может быть пустым!", 1, 10000, false));
        lastName.addValidator(new RegexpValidator("([а-яА-ЯёЁ]+(-[а-яА-ЯёЁ]+)?)?","Поле должно содержать только символы русского языка!"));

        firstName.addValidator(new StringLengthValidator("Поле не может быть пустым!", 1, 10000, false));
        firstName.addValidator(new RegexpValidator("([а-яА-ЯёЁ]+(-[а-яА-ЯёЁ]+)?)?","Поле должно содержать только символы русского языка!"));

        patronymic.addValidator(new RegexpValidator("([а-яА-ЯёЁ]+(-[а-яА-ЯёЁ]+)?)?","Поле должно содержать только символы русского языка!"));

        phoneNumber.addValidator(new StringLengthValidator("Поле не может быть пустым!", 1, 10000, false));
        phoneNumber.addValidator(new RegexpValidator("((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$",
                "Неверный формат номера телефона!"));

        VerticalLayout main = new VerticalLayout(lastName, firstName, patronymic, phoneNumber, buttons);
        main.setSpacing(true);
        main.setMargin(new MarginInfo(true,true,true,true));

        content.addComponents(main);
        setContent(content);
        setClosable(true);
        setDraggable(false);
        setResizable(false);
        setModal(true);
    }

    public PatientEditWindow(Patient patient, PatientsView patientsView) {
        this();
        this.patient = patient.clone();
        this.patientsView = patientsView;

        okButton.addClickListener(e -> save());
        BeanFieldGroup.bindFieldsUnbuffered(this.patient, this);
    }

    public PatientEditWindow(PatientsView patientsView) {
        this();
        this.patientsView = patientsView;

        okButton.addClickListener(e -> add());
    }

    private boolean validate(){
        try {
            lastName.validate();
        }catch (Validator.InvalidValueException e){
            Notification.show("Поле \"Фамилия\"" + (e.getCauses().length>0 ? e.getCauses()[0]: e)
                    .getMessage().substring(4), Notification.Type.WARNING_MESSAGE);
            lastName.setValidationVisible(true);
            return false;
        }
        try {
            firstName.validate();
        }catch (Validator.InvalidValueException e){
            Notification.show("Поле \"Имя\"" + (e.getCauses().length > 0 ? e.getCauses()[0]: e)
                    .getMessage().substring(4), Notification.Type.WARNING_MESSAGE);
            firstName.setValidationVisible(true);
            return false;
        }
        try {
            patronymic.validate();
        }catch (Validator.InvalidValueException e){
            Notification.show("Поле \"Отчество\"" + (e.getCauses().length > 0 ? e.getCauses()[0]: e)
                    .getMessage().substring(4), Notification.Type.WARNING_MESSAGE);
            patronymic.setValidationVisible(true);
            return false;
        }
        try {
            phoneNumber.validate();
        }catch (Validator.InvalidValueException e){
            Notification.show("Поле \"Телефон\"" + (e.getCauses().length > 0 ? e.getCauses()[0]: e)
                    .getMessage().substring(4), Notification.Type.WARNING_MESSAGE);
            phoneNumber.setValidationVisible(true);
            return false;
        }
        return true;
    }

    public void save(){
        if (validate()){
            patientDAO.update(patient);
            patientsView.update();
            this.close();
        }
    }

    public void add(){
        if (validate()){
            this.patient = new Patient(lastName.getValue(), firstName.getValue(), patronymic.getValue(), phoneNumber.getValue());
            patientDAO.create(this.patient);
            patientsView.update();
            this.close();
        }
    }
}