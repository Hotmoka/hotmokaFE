package com.hotmokafe.application.views.state;

import com.hotmokafe.application.blockchain.State;
import com.hotmokafe.application.entities.Account;
import com.hotmokafe.application.utils.Kernel;
import com.hotmokafe.application.utils.StringUtils;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "state", layout = MainView.class)
public class StateView extends Div {

    private Accordion layoutBuilder(String summary, List<String> list){
        long counter = 1;

        //fields
        VerticalLayout l = new VerticalLayout();
        for (String field : list){
            Label label = new Label();
            label.add(new Text("" + counter++ + ") " + field));
            l.add(label);
        }

        Accordion acc = new Accordion();
        acc.add(summary, l);

        return acc;
    }

    private void viewState() {
        new State().run();

        Account a = Kernel.getInstance().getCurrentAccount();

        Accordion main = new Accordion();
        main.add(a.getReference(), new VerticalLayout(
                layoutBuilder("Fields", a.getFields()),
                layoutBuilder("Inherited fields", a.getInheritedFileds()),
                layoutBuilder("Constructors", a.getConstructors()),
                layoutBuilder("Methods", a.getMethods()),
                layoutBuilder("Inherited methods", a.getInheritedMethods())
        ));
        main.setSizeFull();
        add(main);
    }

    public StateView() {
        if (StringUtils.isValid(Kernel.getInstance().getCurrentAccount().getReference()))
            viewState();
    }
}
