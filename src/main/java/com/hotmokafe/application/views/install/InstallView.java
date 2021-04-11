package com.hotmokafe.application.views.install;

import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "install", layout = MainView.class)
@PageTitle("Install")
public class InstallView extends Div {
    private final VerticalLayout mainLayout;

    public InstallView(){
        mainLayout = new VerticalLayout();

        TextField urlField = new TextField("URL");
        urlField.setSizeFull();
        TextField payerField = new TextField("Payer");
        payerField.setSizeFull();

        Upload jarUpload = new Upload(new MemoryBuffer());
        jarUpload.setUploadButton(new Button("Upload jar file"));
        jarUpload.setAcceptedFileTypes(".jar");
        jarUpload.setSizeFull();

        Upload libsUpload = new Upload(new MultiFileMemoryBuffer());
        libsUpload.setUploadButton(new Button("Add libraries"));
        libsUpload.setAcceptedFileTypes(".jar");
        libsUpload.setSizeFull();

        TextField classPathField = new TextField("Classpath");
        classPathField.setSizeFull();
        Checkbox nonInteractive = new Checkbox("Non interactive");
        TextField gasLimitField = new TextField("Gas limit");
        gasLimitField.setSizeFull();
        mainLayout.add(urlField, payerField, classPathField,
                nonInteractive, gasLimitField, jarUpload, libsUpload);
        add(mainLayout);
    }
}
