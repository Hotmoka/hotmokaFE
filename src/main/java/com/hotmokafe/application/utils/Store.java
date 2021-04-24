package com.hotmokafe.application.utils;

import com.hotmokafe.application.entities.Account;

/**
 * The Most important class of the app.
 * It's a singleton pattern that is used to store all the information needed to be shared among the UI views
 */
public class Store {
    private static Store instance = null;

    private Account currentAccount = new Account(); //the last used account to trigger a blockchain call (namely CreateAccount, State and so on)
    private String url = "ec2-54-194-239-91.eu-west-1.compute.amazonaws.com:8080";  //the default url to the remote node

    //singleton

    private Store(){}

    public static Store getInstance(){
        return instance == null ? instance = new Store() : instance;
    }

    //methods

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
