package com.hotmokafe.application.views.createaccount;

import com.hotmokafe.application.blockchain.CommandException;
import com.hotmokafe.application.blockchain.CreateAccount;
import com.hotmokafe.application.utils.Store;
import com.hotmokafe.application.utils.StringUtils;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
    private Dialog mainDialog = new Dialog();

    private TextField URLField;
    private TextField payerField;
    private Checkbox nonInteractive;
    private NumberField balanceField;
    private NumberField balanceFieldRed;

    private final Button button;

    private void call(){
        Dialog dialog = new Dialog();
        DecimalFormat format = new DecimalFormat("#");

        try {
            if (StringUtils.stringIsNUllOrEmpty(URLField.getValue()))
                new CreateAccount(payerField.getValue(), format.format(balanceField.getValue()), format.format(balanceFieldRed.getValue())).run();
            else
                new CreateAccount(URLField.getValue(), payerField.getValue(), format.format(balanceField.getValue()), format.format(balanceFieldRed.getValue())).run();

            dialog.add(new Text("A new account " + Store.getInstance().getCurrentAccount().getReference() + " has been created"));
            dialog.open();
        } catch (CommandException exception) {
            dialog.add(new Text("Exception thrown: " + exception.getCause().getMessage()));
            dialog.open();
        }
    }

    private void buildDialog() {
        Button confirm = new Button("Confirm");
        Button cancel = new Button("Cancel");

        VerticalLayout layout = new VerticalLayout();

        int gas = balanceFieldRed.getValue() > 0 ? 200_000 : 100_000;
        layout.add(new Text("Do you really want to spend up to " + gas + " gas units to create a new account?"));

        confirm.addClickListener(e -> {
            mainDialog.close();
            call();
        });
        cancel.addClickListener(e -> mainDialog.close());

        layout.add(new HorizontalLayout(confirm, cancel));
        mainDialog.add(layout);
    }

    public CreateAccountView() {
        mainLayout = new VerticalLayout();

        URLField = new TextField("URL");
        URLField.setPlaceholder("The URL of the node");
        URLField.setValue(Store.getInstance().getUrl());
        URLField.setSizeFull();

        payerField = new TextField("Payer");
        payerField.setSizeFull();
        payerField.setValue("faucet");

        nonInteractive = new Checkbox("Non interactive");
        nonInteractive.setValue(true);

        balanceField = new NumberField("Balance");
        balanceField.setSizeFull();
        balanceField.setValue(100.0);

        balanceFieldRed = new NumberField("Balance Red");
        balanceFieldRed.setSizeFull();
        balanceFieldRed.setValue(0.0);

        button = new Button("Crea");
        button.addClickListener(e ->{
            if(!nonInteractive.getValue())
                mainDialog.open();
            else
                call();
        } );

        buildDialog();
        mainLayout.add(URLField, payerField, nonInteractive, balanceField, balanceFieldRed, button);

        add(mainLayout);
    }
}
