package com.hotmokafe.application.utils;

import com.hotmokafe.application.entities.Account;

public class Kernel {
    private static Kernel instance = null;

    private Account currentAccount = new Account();
    private String url = "ec2-54-194-239-91.eu-west-1.compute.amazonaws.com:8080";

    //singleton

    private Kernel(){}

    public static Kernel getInstance(){
        return instance == null ? instance = new Kernel() : instance;
    }

    //extra


    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
