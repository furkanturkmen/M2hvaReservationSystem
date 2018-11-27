package com.hva.m2mobi.m2hva_reservationsystem.models;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hva.m2mobi.m2hva_reservationsystem.R;

import java.util.HashMap;

import static java.lang.StrictMath.toIntExact;

public class Room {
    private int imgResource;
    private String name;
    private String description;
    private String calendarID;
    private int capacity;
    private boolean availability;
    private String time;

    @SerializedName("calendarID")
    @Expose
    private String calendarID;

    @SerializedName("capacity")
    @Expose
    private int capacity;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("name")
    @Expose
    private String name;

    public Room(){
        imgResource = R.drawable.hunting_room;
    }

    public Room(int imgResource, String name, String description, String calendarID, int capacity) {
        this.imgResource = imgResource;
        this.name = name;
        this.description = description;
        this.calendarID = calendarID;
        this.time = "10:00";
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

    public String getCalendarID() {
        return calendarID;
    }

    public void setCalendarID(String calendarID) {
        this.calendarID = calendarID;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isAvailable() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
