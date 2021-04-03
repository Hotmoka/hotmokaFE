package com.hotmokafe.application.utils;

import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.Text;

public class Kernel {
    private static Kernel instance = null;

    private String accountLogged = "12f2f64d26a859adb45b44723b25a68ca853f30a8f9bc70f10e3092e985fa3bb#0";
    private String url = "ec2-54-194-239-91.eu-west-1.compute.amazonaws.com:8080";

    //singleton

    private Kernel(){}

    public static Kernel getInstance(){
        return instance == null ? instance = new Kernel() : instance;
    }

    //extra

    public String getAccountLogged() {
        return accountLogged;
    }

    public void setAccountLogged(String accountLogged) {
        this.accountLogged = accountLogged;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
