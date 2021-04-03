package com.hotmokafe.application.entities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private String reference;
    private final List<String> fileds = new ArrayList<>();
    private final List<String> inheritedFileds = new ArrayList<>();
    private final List<String> methods = new ArrayList<>();
    private final List<String> inheritedMethods = new ArrayList<>();
    private final List<String> constructors = new ArrayList<>();

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<String> getFileds() {
        return fileds;
    }

    public List<String> getInheritedFileds() {
        return inheritedFileds;
    }

    public List<String> getMethods() {
        return methods;
    }

    public List<String> getInheritedMethods() {
        return inheritedMethods;
    }

    public List<String> getConstructors() {
        return constructors;
    }
}
