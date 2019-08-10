package com.example.finalproject.Models;

public class ContactListModel {
    String name;
    String number;
    public ContactListModel(){}

    public ContactListModel(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
