package com.hotmokafe.application.views.install;

import com.hotmokafe.application.blockchain.CommandException;
import com.hotmokafe.application.blockchain.Install;
import com.hotmokafe.application.utils.Store;
import com.hotmokafe.application.utils.StringUtils;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Route(value = "install", layout = MainView.class)
@PageTitle("Install")
public class InstallView extends Div {
    private final VerticalLayout mainLayout;
    private List<String> libs;
    private String path2Jar;
    private Dialog mainDialog = new Dialog();

    private TextField urlField;
    private TextField payerField;
    private Upload jarUpload;
    private Upload libsUpload;
    private MemoryBuffer buffer;
    private MultiFileMemoryBuffer multiBuffer;
    private TextField classPathField;
    private Checkbox nonInteractive;
    private TextField gasLimitField;

    BigInteger gas;

    private void call(){
        libs.addAll(multiBuffer.getFiles());
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
    }

    private void buildDialog() {
        Button confirm = new Button("Confirm");
        Button cancel = new Button("Cancel");

        VerticalLayout layout = new VerticalLayout();


        layout.add(new Text("Do you really want to spend up to " + gas + " gas units to create a new account?"));

        confirm.addClickListener(e -> {
            mainDialog.close();
            call();
        });
        cancel.addClickListener(e -> mainDialog.close());

        layout.add(new HorizontalLayout(confirm, cancel));
        mainDialog.add(layout);
    }

    public InstallView() {
        mainLayout = new VerticalLayout();
        libs = new ArrayList<>();

        urlField = new TextField("URL");
        urlField.setValue(Store.getInstance().getUrl());
        urlField.setSizeFull();

        payerField = new TextField("Payer");
        payerField.setPlaceholder("Base 64 hash");

        if (Store.getInstance().getCurrentAccount() != null &&
                StringUtils.isValid(Store.getInstance().getCurrentAccount().getReference()))
            payerField.setValue(Store.getInstance().getCurrentAccount().getReference());

        payerField.setSizeFull();

        buffer = new MemoryBuffer();
        jarUpload = new Upload(buffer);
        jarUpload.setUploadButton(new Button("Upload jar file"));
        jarUpload.setAcceptedFileTypes(".jar");
        jarUpload.setSizeFull();

        multiBuffer = new MultiFileMemoryBuffer();
        libsUpload = new Upload(multiBuffer);
        libsUpload.setUploadButton(new Button("Add libraries"));
        libsUpload.setAcceptedFileTypes(".jar");
        libsUpload.setSizeFull();

        classPathField = new TextField("Classpath");
        classPathField.setSizeFull();

        nonInteractive = new Checkbox("Non interactive");
        nonInteractive.setValue(true);

        gasLimitField = new TextField("Gas limit");
        gasLimitField.setValue("heuristic");
        gasLimitField.setSizeFull();

        Button button = new Button("Install");
        button.addClickListener(e -> {
            if(!nonInteractive.getValue()){
                gas = BigInteger.valueOf(0);

                if("heuristic".equals(gasLimitField.getValue())){
                    if(buffer.getFileData() != null){
                        gas = BigInteger.valueOf(100_000L).add(BigInteger.valueOf(100).multiply(
                                BigInteger.valueOf(((ByteArrayOutputStream)buffer.getFileData().getOutputBuffer()).toByteArray().length)));
                    }
                }
                else{
                    gas = new BigInteger(gasLimitField.getValue());
                }

                buildDialog();
                mainDialog.open();
            }
            else
                call();
        });

        mainLayout.add(urlField, payerField, nonInteractive,
                classPathField, gasLimitField, jarUpload, libsUpload, button);
        add(mainLayout);
    }
}
