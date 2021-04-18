package com.hotmokafe.application.entities;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String reference;
    private List<String> fields = new ArrayList<>();
    private List<String> inheritedFileds = new ArrayList<>();
    private List<String> methods = new ArrayList<>();
    private List<String> inheritedMethods = new ArrayList<>();
    private List<String> constructors = new ArrayList<>();
    private List<String> storages = new ArrayList<>();

    private void replace(List<String> list, int index){
        String tmp = fields.get(index);
        tmp = "%STORAGE%" + tmp;
        list.set(index, tmp);
    }

    public void addStorage(String s ){
        if (fields.contains(s)) {
            replace(fields, fields.indexOf(s));
        } else {
            replace(inheritedFileds, inheritedFileds.indexOf(s));
        }
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<String> getInheritedFileds() {
        return inheritedFileds;
    }

    public void setInheritedFileds(List<String> inheritedFileds) {
        this.inheritedFileds = inheritedFileds;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public List<String> getInheritedMethods() {
        return inheritedMethods;
    }

    public void setInheritedMethods(List<String> inheritedMethods) {
        this.inheritedMethods = inheritedMethods;
    }

    public List<String> getConstructors() {
        return constructors;
    }

    public void setConstructors(List<String> constructors) {
        this.constructors = constructors;
    }

    public List<String> getStorages() {
        return storages;
    }
}
