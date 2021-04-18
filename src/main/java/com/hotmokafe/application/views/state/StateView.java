package com.hotmokafe.application.views.state;

import com.hotmokafe.application.blockchain.State;
import com.hotmokafe.application.entities.Account;
import com.hotmokafe.application.utils.Store;
import com.hotmokafe.application.utils.StringUtils;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

@Route(value = "state", layout = MainView.class)
@PageTitle("State")
public class StateView extends Div {
    private FormLayout layout = new FormLayout();
    private TextField inputField = new TextField();
    private Button button = new Button("Cerca");

    private void mainLayoutBuilder(Component... component){
        layout.removeAll();
        layout.add(inputField);
        layout.add(button);

        if(component != null)
            layout.add(component);

        add(layout);
    }
    
    private Accordion fieldsLayoutBuilder(String summary, List<String> list) {
        ListItem[] items = new ListItem[list.size()];

        for (String label : list) {
            if (label.contains("%STORAGE%")) {
                String s = label.split("%STORAGE%")[1];
                String[] tokens = s.split("=");

                Button b = new Button(tokens[1].trim());
                b.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                b.addClickListener(e -> {
                    viewState(b.getText());
                });

                items[list.indexOf(label)] = new ListItem(new Label(tokens[0] + "="), b);
            } else {
                items[list.indexOf(label)] = new ListItem(new Label(label));
            }
        }

        Accordion acc = new Accordion();
        acc.add(summary, new OrderedList(items));
        acc.setSizeFull();
        acc.close();

        return acc;
    }
    
    private Accordion layoutBuilder(String summary, List<String> list) {
        ListItem[] items = new ListItem[list.size()];

        for (int i = 0; i < list.size(); i++)
            items[i] = new ListItem(new Label(list.get(i)));

        Accordion acc = new Accordion();
        acc.add(summary, new OrderedList(items));
        acc.close();

        return acc;
    }

    private void viewState(String id) {
        Account a = new Account();
        a.setReference(id);
        Store.getInstance().setCurrentAccount(a);

        new State().run();

        a = Store.getInstance().getCurrentAccount();

        Accordion main = new Accordion();
        main.add(a.getReference(), new VerticalLayout(
                fieldsLayoutBuilder("Fields", a.getFields()),
                fieldsLayoutBuilder("Inherited fields", a.getInheritedFileds()),
                layoutBuilder("Constructors", a.getConstructors()),
                layoutBuilder("Methods", a.getMethods()),
                layoutBuilder("Inherited methods", a.getInheritedMethods())
        ));
        main.setSizeFull();

        mainLayoutBuilder(main);
    }

    public StateView() {
        button.addClickListener(e -> {
            viewState(inputField.getValue());
        });

        button.setMaxHeight("10%");
        button.setMaxWidth("10%");

        inputField.setPlaceholder("Cerca per puntatore");

        mainLayoutBuilder();

        String reference = Store.getInstance().getCurrentAccount().getReference();

        if (StringUtils.isValid(reference))
            viewState(reference);
    }
}
