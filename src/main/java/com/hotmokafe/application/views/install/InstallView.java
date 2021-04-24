package com.hotmokafe.application.views.install;

import com.hotmokafe.application.blockchain.CommandException;
import com.hotmokafe.application.blockchain.Install;
import com.hotmokafe.application.utils.Store;
import com.hotmokafe.application.utils.StringUtils;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
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
    private final List<String> libs;
    private final Dialog mainDialog = new Dialog();

    private final TextField urlField;
    private final TextField payerField;
    private final Upload jarUpload;
    private final ComboBox<String> libsComboBOX;
    private final MemoryBuffer buffer;
    private final TextField classPathField;
    private final Checkbox nonInteractive;
    private final TextField gasLimitField;

    BigInteger gas;

    /**
     * Executes the blockchain "Install" command
     */
    private void call(){
        Dialog dialog = new Dialog();
        try{
            if (StringUtils.isValid(urlField.getValue()) &&
                    StringUtils.isValid(payerField.getValue()) &&
                    StringUtils.isValid(buffer.getFileName()))
            {
                Install install = new Install(
                        urlField.getValue(),
                        payerField.getValue(),
                        //the main difference between the Install CLI is this byte array parameter.
                        //Because we are using a server side rendering framework (Vaadin)
                        //once the jar is uploaded no path is avaiable but only is data (byte).
                        //Therfore, in order to use the jar, we must modify the blockchain
                        // Install class so that it can operate on a byte array.
                        ((ByteArrayOutputStream)buffer.getFileData().getOutputBuffer()).toByteArray(),
                        libs,
                        classPathField.getValue(),
                        nonInteractive.getValue(),
                        gasLimitField.getValue()
                );

                try {
                    install.run();
                } catch(CommandException e){
                    dialog.add(new Text("Exception thrown: " + e.getCause().getMessage()));
                    dialog.open();
                }

                dialog.add(new Text(install.getOutcome().replace("<%REPLACE%>", buffer.getFileName())));
                dialog.open();
            }
        } catch (CommandException exception){
            dialog.add(new Text("Exception thrown: " + exception.getCause().getMessage()));
            dialog.open();
        }
    }

    /**
     * Builds the confirm dialog
     */
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
                StringUtils.isValid(Store.getInstance().getCurrentAccount().getReference())) //initialize the field only if an account is in memory
            payerField.setValue(Store.getInstance().getCurrentAccount().getReference());

        payerField.setSizeFull();

        buffer = new MemoryBuffer();
        jarUpload = new Upload(buffer);
        jarUpload.setUploadButton(new Button("Upload jar file"));
        jarUpload.setAcceptedFileTypes(".jar");
        jarUpload.setSizeFull();
        jarUpload.setMaxWidth("98%");

        libsComboBOX = new ComboBox<>("Libraries");
        libsComboBOX.setAllowCustomValue(true);
        libsComboBOX.setSizeFull();
        libsComboBOX.addCustomValueSetListener(e -> {
            if (!libs.contains(e.getDetail())) {
                libs.add(e.getDetail());
                libsComboBOX.setItems(libs);
                new Notification(
                        "Value " + e.getDetail() + " added", 3000).open();
            }
            libsComboBOX.setValue("");  //reset input value
        });

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
                        //estimates the unit of gas that could be used for the transaction
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
                classPathField, gasLimitField, jarUpload, libsComboBOX, button);
        add(mainLayout);
    }
}
