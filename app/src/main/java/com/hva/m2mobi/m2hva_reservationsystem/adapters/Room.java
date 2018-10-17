package com.hva.m2mobi.m2hva_reservationsystem.adapters;

public class Room {
    private String name;
    private String description;
    private String availability;


    public Room(String name, String description, String availability) {
        this.name = name;
        this.description = description;
        this.availability = availability;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
