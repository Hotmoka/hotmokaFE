package com.hotmokafe.application.views.createaccount;

import com.hotmokafe.application.blockchain.CreateAccount;
import com.hotmokafe.application.entities.Person;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.dependency.CssImport;

import java.security.NoSuchAlgorithmException;

@Route(value = "about", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Create Account")
@CssImport("./views/about/about-view.css")
public class CreateAccountView extends Div {
    private final VerticalLayout mainLayout;
    private final FormLayout layoutWithFormItems;
    private final Button button;

    public CreateAccountView() {
        mainLayout = new VerticalLayout();

        layoutWithFormItems = new FormLayout();

        TextField firstName = new TextField();
        firstName.setPlaceholder("Nome");
        firstName.setSizeFull();

        TextField lastName = new TextField();
        lastName.setPlaceholder("Cognome");
        lastName.setSizeFull();

        DatePicker birthDate = new DatePicker();
        birthDate.setSizeFull();

        layoutWithFormItems.addFormItem(firstName, "Nome");
        layoutWithFormItems.addFormItem(lastName, "Cognome");

        layoutWithFormItems.addFormItem(birthDate, "Data di nascita");

        mainLayout.add(layoutWithFormItems);

        button = new Button("Crea");
        button.addClickListener(e -> {

            if (firstName.getValue().length() != 0 && lastName.getValue().length() != 0 && birthDate.getValue() != null){
                new CreateAccount().run();
                //Dialog dialog = new Dialog();
                //dialog.open();
            }
        });

        mainLayout.add(button);

        add(mainLayout);
    }

}
