package com.hotmokafe.application.views.state;

import com.hotmokafe.application.blockchain.State;
import com.hotmokafe.application.entities.Account;
import com.hotmokafe.application.utils.Kernel;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;

import java.lang.reflect.Method;
import java.util.List;

@Route(value = "state", layout = MainView.class)
public class StateView extends Div {
    FormLayout layout;

    private void onCLickListener() {
        new State().run();
        Account a = Kernel.getInstance().getAccountLogged();
        long counter = 1;
        //fields
        VerticalLayout l = new VerticalLayout();
        for (String field : a.getFileds()){
            Label label = new Label();
            label.add(new Text("" + counter++ + ") " + field));
            l.add(label);
        }

        counter = 1;

        Accordion acc = new Accordion();
        acc.add("Fields", l);
        layout.add(acc);

        //fields inherited
        VerticalLayout l1 = new VerticalLayout();
        for (String field : a.getInheritedFileds()){
            Label label = new Label();
            label.add(new Text("" + counter++ + ") " + field));
            l1.add(label);
        }

        counter = 1;

        Accordion acc1 = new Accordion();
        acc1.add("Fields Inherited", l1);

        //constructors
        VerticalLayout l2 = new VerticalLayout();
        for (String field : a.getConstructors()){
            Label label = new Label();
            label.add(new Text("" + counter++ + ") " + field));
            l2.add(label);
        }

        counter = 1;

        Accordion acc2 = new Accordion();
        acc2.add("Constructors", l2);

        //methods
        VerticalLayout l3 = new VerticalLayout();
        for (String field : a.getMethods()){
            Label label = new Label();
            label.add(new Text("" + counter++ + ") " + field));
            l3.add(label);
        }

        counter = 1;

        Accordion acc3 = new Accordion();
        acc3.add("Methods", l3);

        //inherited methods
        VerticalLayout l4 = new VerticalLayout();
        for (String field : a.getInheritedMethods()){
            Label label = new Label();
            label.add(new Text("" + counter++ + ") " + field));
            l4.add(label);
        }

        Accordion acc4 = new Accordion();
        acc4.add("Inherited Methods", l4);

        VerticalLayout accordionLayout = new VerticalLayout();
        accordionLayout.add(acc, acc1, acc2, acc3, acc4);

        Accordion main = new Accordion();
        main.add(a.getReference(), accordionLayout);
        main.setSizeFull();
        add(main);
    }

    public StateView() {
        layout = new FormLayout();

        Button b = new Button("Get state");
        b.addClickListener(e -> onCLickListener());
        layout.add(b);
        layout.add(new Span());
        layout.setSizeFull();
        add(layout);
    }
}
