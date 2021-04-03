package com.hotmokafe.application.utils;

import com.hotmokafe.application.entities.Account;
import com.hotmokafe.application.views.main.MainView;
import com.vaadin.flow.component.Text;

public class Kernel {
    private static Kernel instance = null;

    private final Account accountLogged = new Account();
    private String url = "ec2-54-194-239-91.eu-west-1.compute.amazonaws.com:8080";

    //singleton

    private Kernel(){}

    public static Kernel getInstance(){
        return instance == null ? instance = new Kernel() : instance;
    }

    //extra

    public Account getAccountLogged() {
        return accountLogged;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
