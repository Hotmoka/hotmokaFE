package com.hotmokafe.application.entities;

public class Person {
    private final String name;
    private final int day;
    private final int month;
    private final int year;
    private final Person parent1;
    private final Person parent2;

    public Person(String name, int day, int month, int year,
                  Person parent1, Person parent2) {

        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public Person(String name, int day, int month, int year) {
        this(name, day, month, year, null, null);
    }

    public String getName() {
        return name;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Person getParent1() {
        return parent1;
    }

    public Person getParent2() {
        return parent2;
    }

    @Override
    public String toString() {
        return name + " (" + day + "/" + month + "/" + year + ")";
    }
}
