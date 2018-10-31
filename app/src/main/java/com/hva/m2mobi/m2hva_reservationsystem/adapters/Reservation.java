package com.hva.m2mobi.m2hva_reservationsystem.adapters;

public class Reservation {
    private String description;
    private String date;
    private String time;

    public Reservation(String description, String date, String time) {
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
