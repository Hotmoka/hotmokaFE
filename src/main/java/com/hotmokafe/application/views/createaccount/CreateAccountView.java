package com.hotmokafe.application.views.createaccount;

import com.hotmokafe.application.blockchain.CommandException;
import com.hotmokafe.application.blockchain.CreateAccount;
import com.hotmokafe.application.entities.Person;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
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

        TextField URLField = new TextField();
        URLField.setSizeFull();

        Checkbox useDefaultURL = new Checkbox();
        useDefaultURL.setValue(true);

        useDefaultURL.addValueChangeListener(e -> {
           URLField.setEnabled(!useDefaultURL.getValue());
        });

        URLField.setEnabled(false);

        TextField payerField = new TextField();
        //lastName.setPlaceholder("Cognome");
        payerField.setSizeFull();

        TextField balanceField = new TextField();
        balanceField.setSizeFull();

        TextField balanceRedField = new TextField();
        balanceRedField.setSizeFull();

        Checkbox nonInteractive = new Checkbox();

        layoutWithFormItems.addFormItem(URLField, "URL");
        layoutWithFormItems.addFormItem(useDefaultURL, "Use default URL");
        layoutWithFormItems.addFormItem(payerField, "Payer");
        layoutWithFormItems.addFormItem(balanceField, "Balance");
        layoutWithFormItems.addFormItem(balanceRedField, "Balance Red");
        layoutWithFormItems.addFormItem(nonInteractive, "Non interactive");
        mainLayout.add(layoutWithFormItems);

        button = new Button("Crea");
        button.addClickListener(e -> {
            try{
                if (useDefaultURL.getValue())
                    new CreateAccount(payerField.getValue(), balanceField.getValue(), balanceRedField.getValue(), nonInteractive.getValue()).run();
                else
                    new CreateAccount(URLField.getValue(),payerField.getValue(), balanceField.getValue(), balanceRedField.getValue(), nonInteractive.getValue()).run();
            } catch (CommandException exception){
                Dialog dialog = new Dialog();
                dialog.add(new Text("Errore eccezione generata: " +  exception.getCause().getMessage()));
                dialog.open();
            }
        });

        mainLayout.add(button);

        add(mainLayout);
    }

}
