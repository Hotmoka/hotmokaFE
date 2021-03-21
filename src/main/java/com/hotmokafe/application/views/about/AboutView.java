package com.hotmokafe.application.views.about;

import com.hotmokafe.application.blockchain.CreateAccount;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.dependency.CssImport;

import java.security.NoSuchAlgorithmException;

@Route(value = "about", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("About")
@CssImport("./views/about/about-view.css")
public class AboutView extends Div {
    private Button button;
    public AboutView() {
        addClassName("about-view");
        add(new Text("Content placeholder"));
        add(button = new Button("Crea account"));

        button.addClickListener(e ->{
            try {
                add(new Text(CreateAccount.Run()));
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) { }
        });
    }

}
