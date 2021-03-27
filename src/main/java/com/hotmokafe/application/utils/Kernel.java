package com.hotmokafe.application.utils;

import com.hotmokafe.application.views.main.MainView;

public class Kernel {
    private static Kernel instance = null;

    //Views
    private MainView mainView;

    private Kernel(){}

    public static Kernel getInstance(){
        return instance == null ? instance = new Kernel() : instance;
    }

    public MainView getMainView() {
        return mainView;
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }
}
