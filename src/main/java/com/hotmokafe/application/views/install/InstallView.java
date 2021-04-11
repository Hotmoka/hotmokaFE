package com.hotmokafe.application.views.install;

import com.hotmokafe.application.blockchain.CommandException;
import com.hotmokafe.application.blockchain.Install;
import com.hotmokafe.application.blockchain.State;
import com.hotmokafe.application.utils.Kernel;
import com.hotmokafe.application.utils.StringUtils;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
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

import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Route(value = "install", layout = MainView.class)
@PageTitle("Install")
public class InstallView extends Div {
    private final VerticalLayout mainLayout;
    private List<String> libs;
    private String path2Jar;

    public InstallView() {
        mainLayout = new VerticalLayout();
        libs = new ArrayList<>();

        TextField urlField = new TextField("URL");
        urlField.setValue(Kernel.getInstance().getUrl());
        urlField.setSizeFull();

        TextField payerField = new TextField("Payer");
        payerField.setPlaceholder("Base 64 hash");

        if (Kernel.getInstance().getCurrentAccount() != null &&
                StringUtils.isValid(Kernel.getInstance().getCurrentAccount().getReference()))
            payerField.setValue(Kernel.getInstance().getCurrentAccount().getReference());

        payerField.setSizeFull();

        MemoryBuffer buffer = new MemoryBuffer();
        Upload jarUpload = new Upload(buffer);
        jarUpload.setUploadButton(new Button("Upload jar file"));
        jarUpload.setAcceptedFileTypes(".jar");
        jarUpload.setSizeFull();

        MultiFileMemoryBuffer multiBuffer = new MultiFileMemoryBuffer();
        Upload libsUpload = new Upload(multiBuffer);
        libsUpload.setUploadButton(new Button("Add libraries"));
        libsUpload.setAcceptedFileTypes(".jar");
        libsUpload.setSizeFull();

        TextField classPathField = new TextField("Classpath");
        classPathField.setSizeFull();

        Checkbox nonInteractive = new Checkbox("Non interactive");
        nonInteractive.setValue(true);

        TextField gasLimitField = new TextField("Gas limit");
        gasLimitField.setValue("heuristic");
        gasLimitField.setSizeFull();

        Button button = new Button("Install");
        button.addClickListener(e -> {
            multiBuffer.getFiles().forEach(fileName -> libs.add(fileName));
            Dialog dialog = new Dialog();
            try{
                if (StringUtils.isValid(urlField.getValue()) &&
                        StringUtils.isValid(payerField.getValue()) &&
                        StringUtils.isValid(buffer.getFileName()))
                {
                    Install install = new Install(
                            urlField.getValue(),
                            payerField.getValue(),
                            ((ByteArrayOutputStream)buffer.getFileData().getOutputBuffer()).toByteArray(),
                            libs,
                            classPathField.getValue(),
                            nonInteractive.getValue(),
                            gasLimitField.getValue()
                    );

                    install.run();

                    dialog.add(new Text(install.getOutcome().replace("<%REPLACE%>", buffer.getFileName())));
                    dialog.open();
                }
            } catch (CommandException exception){
                dialog.add(new Text("Exception thrown: " + exception.getCause().getMessage()));
                dialog.open();
            }
        });

        mainLayout.add(urlField, payerField, classPathField,
                nonInteractive, gasLimitField, jarUpload, libsUpload, button);
        add(mainLayout);
    }
}
