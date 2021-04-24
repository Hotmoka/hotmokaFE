package com.hotmokafe.application.views.state;

import com.hotmokafe.application.blockchain.CommandException;
import com.hotmokafe.application.blockchain.State;
import com.hotmokafe.application.entities.Account;
import com.hotmokafe.application.utils.Store;
import com.hotmokafe.application.utils.StringUtils;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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

    /**
     * method used to build an accordion for displaying the account's fileds
     * @param summary a text label used to identify the accordion
     * @param list the list of fields to add in the accordion's body
     * @return the accordion
     */
    private Accordion fieldsLayoutBuilder(String summary, List<String> list) {
        ListItem[] items = new ListItem[list.size()];

        for (String label : list) {
            if (label.contains("%STORAGE%")) {  //the entry contains "%STORAGE%" ?
                String s = label.split("%STORAGE%")[1]; //then subdivide the entry in two substring and take only the part that follows %STORAGE%
                String[] tokens = s.split("="); //split again the string using the "=" as a point of reference

                Button b = new Button(tokens[1].trim()); //add the r-value as the label of the button
                b.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                b.addClickListener(e -> {
                    inputField.setValue(b.getText());   //when the button is clicked the main textfield will be set with the reference
                    viewState();  // and a call to State will be triggered
                });

                //add the l-value (namely the field name and the type) and the button
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

    /**
     * method used to build an accordion for displaying the account's methods
     * @param summary a text label used to identify the accordion
     * @param list the list of fields to add in the accordion's body
     * @return the accordion
     */
    private Accordion layoutBuilder(String summary, List<String> list) {
        ListItem[] items = new ListItem[list.size()];

        for (int i = 0; i < list.size(); i++)
            items[i] = new ListItem(new Label(list.get(i)));

        Accordion acc = new Accordion();
        acc.add(summary, new OrderedList(items));
        acc.close();

        return acc;
    }

    /**
     * method used to trigger the blockchain "State" command
     */
    private void viewState() {
        Account a = new Account();
        a.setReference(inputField.getValue());
        Store.getInstance().setCurrentAccount(a);

        try{
            new State().run();
        } catch (CommandException e){
            Dialog dialog = new Dialog();
            dialog.add(new Text("Exception thrown: " + e.getCause().getMessage()));
            dialog.open();
        }

        a = Store.getInstance().getCurrentAccount();

        Accordion main = new Accordion();
        main.add(a.getReference() + ":" + Store.getInstance().getCurrentAccount().getTag().clazz,
                new VerticalLayout(
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
        button.addClickListener(e -> viewState());

        button.setMaxHeight("10%");
        button.setMaxWidth("10%");

        inputField.setPlaceholder("Cerca per puntatore");

        mainLayoutBuilder();

        if (StringUtils.isValid(Store.getInstance().getCurrentAccount().getReference())){
            inputField.setValue(Store.getInstance().getCurrentAccount().getReference());
            viewState();
        }
    }
}
