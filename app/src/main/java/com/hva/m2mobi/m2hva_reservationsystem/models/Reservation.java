package com.hva.m2mobi.m2hva_reservationsystem.models;

public class Reservation {
    private String description;


    public Reservation(String description) {
        this.description = description;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
