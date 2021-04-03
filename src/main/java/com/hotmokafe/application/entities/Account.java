package com.hotmokafe.application.entities;

import java.util.List;

public class Account {
    private String reference = "";
    private List<String> innerClass;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<String> getInnerClass() {
        return innerClass;
    }

    public void setInnerClass(List<String> innerClass) {
        this.innerClass = innerClass;
    }
}
