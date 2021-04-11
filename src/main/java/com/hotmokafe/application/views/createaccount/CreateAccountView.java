package com.hotmokafe.application.views.createaccount;

import com.hotmokafe.application.blockchain.CommandException;
import com.hotmokafe.application.blockchain.CreateAccount;
import com.hotmokafe.application.utils.Store;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.dependency.CssImport;

import java.text.DecimalFormat;

@Route(value = "create-account", layout = MainView.class)
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

        useDefaultURL.addValueChangeListener(e -> URLField.setEnabled(!useDefaultURL.getValue()));

        URLField.setEnabled(false);

        TextField payerField = new TextField();
        payerField.setSizeFull();
        payerField.setValue("faucet");

        NumberField balanceField = new NumberField();
        balanceField.setSizeFull();
        balanceField.setValue(100.0);

        NumberField balanceFieldRed = new NumberField();
        balanceFieldRed.setSizeFull();
        balanceFieldRed.setValue(0.0);

        layoutWithFormItems.addFormItem(URLField, "URL");
        layoutWithFormItems.addFormItem(useDefaultURL, "Use default URL");
        layoutWithFormItems.addFormItem(payerField, "Payer");
        layoutWithFormItems.addFormItem(new Span(), "");
        layoutWithFormItems.addFormItem(balanceField, "Balance");
        layoutWithFormItems.addFormItem(new Span(), "");
        layoutWithFormItems.addFormItem(balanceFieldRed, "Balance Red");
        layoutWithFormItems.addFormItem(new Span(), "");
        mainLayout.add(layoutWithFormItems);

        button = new Button("Crea");
        button.addClickListener(e -> {
            Dialog dialog = new Dialog();
            DecimalFormat format = new DecimalFormat("#");
            try {
                if (useDefaultURL.getValue())
                    new CreateAccount(payerField.getValue(), format.format(balanceField.getValue()), format.format(balanceFieldRed.getValue())).run();
                else
                    new CreateAccount(URLField.getValue(), payerField.getValue(), balanceField.getValue().toString()).run();

                dialog.add(new Text("A new account " + Store.getInstance().getCurrentAccount().getReference() + " has been created"));
                dialog.open();
            } catch (CommandException exception) {
                dialog.add(new Text("Exception thrown: " + exception.getCause().getMessage()));
                dialog.open();
            }
        });

        mainLayout.add(button);

        add(mainLayout);
    }
}
