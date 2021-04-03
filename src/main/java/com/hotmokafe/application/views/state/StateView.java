package com.hotmokafe.application.views.state;

import com.hotmokafe.application.blockchain.State;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "state", layout = MainView.class)
public class StateView extends Div {
    FormLayout layout;

    public StateView(){
        layout = new FormLayout();

        Button b = new Button("Get state");
        b.addClickListener(e -> new State().run());
        layout.add(b);
        add(layout);
    }
}
