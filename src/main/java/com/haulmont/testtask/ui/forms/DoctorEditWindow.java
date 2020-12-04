package com.haulmont.testtask.ui.forms;

import com.haulmont.testtask.dao.DoctorDAO;
import com.haulmont.testtask.model.Doctor;
import com.haulmont.testtask.ui.view.DoctorsView;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class DoctorEditWindow extends Window {
    private final TextField lastName = new TextField("Фамилия");
    private final TextField firstName = new TextField("Имя");
    private final TextField patronymic = new TextField("Отчество");
    private final TextField specialization = new TextField("Специализация");
    private final Button okButton;


    private final DoctorDAO doctorDAO;
    private Doctor doctor;
    private DoctorsView doctorsView;

    public DoctorEditWindow() {
        this.doctorDAO = DoctorDAO.getInstance();

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
        lastName.addValidator(new RegexpValidator("([а-яА-ЯёЁ]+(-[а-яА-ЯёЁ]+)?)?", "Поле должно содержать только символы русского языка!"));

        firstName.addValidator(new StringLengthValidator("Поле не может быть пустым!", 1, 10000, false));
        firstName.addValidator(new RegexpValidator("([а-яА-ЯёЁ]+(-[а-яА-ЯёЁ]+)?)?", "Поле должно содержать только символы русского языка!"));

        patronymic.addValidator(new RegexpValidator("([а-яА-ЯёЁ]+(-[а-яА-ЯёЁ]+)?)?", "Поле должно содержать только символы русского языка!"));

        specialization.addValidator(new StringLengthValidator("Поле не может быть пустым!", 1, 10000, false));
        specialization.addValidator(new RegexpValidator("([а-яА-ЯёЁ]+(-[а-яА-ЯёЁ]+)?)?", "Поле должно содержать только символы русского языка!"));

        VerticalLayout main = new VerticalLayout(lastName, firstName, patronymic, specialization, buttons);
        main.setSpacing(true);
        main.setMargin(new MarginInfo(true, true, true, true));

        content.addComponents(main);
        setContent(content);
        setClosable(true);
        setDraggable(false);
        setResizable(false);
        setModal(true);
    }

    public DoctorEditWindow(Doctor doctor, DoctorsView doctorsView) {
        this();

        this.doctor = doctor.clone();
        this.doctorsView = doctorsView;

        okButton.addClickListener(e -> save());
        BeanFieldGroup.bindFieldsUnbuffered(this.doctor, this);
    }

    public DoctorEditWindow(DoctorsView doctorsView) {
        this();
        this.doctorsView = doctorsView;

        okButton.addClickListener(e -> add());
    }

    private boolean validate() {
        try {
            lastName.validate();
        } catch (Validator.InvalidValueException e) {
            Notification.show("Поле \"Фамилия\"" + (e.getCauses().length > 0 ? e.getCauses()[0] : e)
                    .getMessage().substring(4), Notification.Type.WARNING_MESSAGE);
            lastName.setValidationVisible(true);
            return false;
        }
        try {
            firstName.validate();
        } catch (Validator.InvalidValueException e) {
            Notification.show("Поле \"Имя\"" + (e.getCauses().length > 0 ? e.getCauses()[0] : e)
                    .getMessage().substring(4), Notification.Type.WARNING_MESSAGE);
            firstName.setValidationVisible(true);
            return false;
        }
        try {
            patronymic.validate();
        } catch (Validator.InvalidValueException e) {
            Notification.show("Поле \"Отчество\"" + (e.getCauses().length > 0 ? e.getCauses()[0] : e)
                    .getMessage().substring(4), Notification.Type.WARNING_MESSAGE);
            patronymic.setValidationVisible(true);
            return false;
        }
        try {
            specialization.validate();
        } catch (Validator.InvalidValueException e) {
            Notification.show("Поле \"Специализация\"" + (e.getCauses().length > 0 ? e.getCauses()[0] : e)
                    .getMessage().substring(4), Notification.Type.WARNING_MESSAGE);
            specialization.setValidationVisible(true);
            return false;
        }
        return true;
    }

    public void save() {
        if (validate()) {
            doctorDAO.update(doctor);
            doctorsView.update();
            this.close();
        }
    }

    public void add() {
        if (validate()) {
            this.doctor = new Doctor(lastName.getValue(), firstName.getValue(), patronymic.getValue(), specialization.getValue());
            doctorDAO.create(this.doctor);
            doctorsView.update();
            this.close();
        }
    }
}
