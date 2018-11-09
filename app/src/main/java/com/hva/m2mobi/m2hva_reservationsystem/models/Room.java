package com.hva.m2mobi.m2hva_reservationsystem.models;

public class Room {
    private int imgResource;
    private String name;
    private String description;
    private String availability;
    private String time;
    private int capacity;

    public Room(int imgResource, String name, String description, String availability, String time, int capacity) {
        this.imgResource = imgResource;
        this.name = name;
        this.description = description;
        this.availability = availability;
        this.time = time;
        this.capacity = capacity;
    }

    public int getImgResource() {
        return imgResource;
    }

    public void setImgResource(int imgResource) {
        this.imgResource = imgResource;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
