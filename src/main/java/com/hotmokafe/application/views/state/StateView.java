package com.hotmokafe.application.views.state;

import com.hotmokafe.application.blockchain.State;
import com.hotmokafe.application.entities.Account;
import com.hotmokafe.application.utils.Store;
import com.hotmokafe.application.utils.StringUtils;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
                String s = label.replace("%STORAGE%", "");
                String[] tokens = s.split("=");
                Anchor anchor = new Anchor("localhost:8080/state/" + tokens[1], tokens[1]);

                items[list.indexOf(label)] = new ListItem(new Label(tokens[0] + "="), anchor);
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

    private void viewState() {
        new State().run();

        Account a = Store.getInstance().getCurrentAccount();

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
            Account a = new Account();
            a.setReference(inputField.getValue());
            Store.getInstance().setCurrentAccount(a);
            viewState();
        });

        button.setMaxHeight("10%");
        button.setMaxWidth("10%");

        inputField.setPlaceholder("Cerca per puntatore");

        mainLayoutBuilder();

        if (StringUtils.isValid(Store.getInstance().getCurrentAccount().getReference()))
            viewState();
    }
}
