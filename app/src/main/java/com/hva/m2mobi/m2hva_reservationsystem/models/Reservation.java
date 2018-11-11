package com.hva.m2mobi.m2hva_reservationsystem.models;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.O)
public class Reservation {

    private int attendees;
    private String date;
    private String startTime;
    private String endTime;
    private Room reservationRoom;
    private String creator;
    private String ID;

    //reservation object
    public Reservation(int attendees, String startTime, String endTime, Room room, String creator, String date, String ID) {
        this.attendees = attendees;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationRoom = room;
        this.creator = creator;
        this.date = date;
        this.ID = ID;
    }

    @NonNull
    @Override
    public String toString() {
        String string = "Start Time: " + startTime + "\n";
        string += "End Time: " + endTime + "\n";
        string += "Room: " + reservationRoom.getName() + "\n";
        string += "Creator: " + creator + "\n";
        return string;
    }

    //getters for all variables
    public String getStartTime(){
        return startTime;
    }
    public String getEndTime(){
        return endTime;
    }
    public int getAttendees(){
        return attendees;
    }

    public Room getReservationRoom() {
        return reservationRoom;
    }

    public void setReservationRoom(Room reservationRoom) {
        this.reservationRoom = reservationRoom;
    }
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }
    public void setEndTime(String endTime){
        this.endTime = endTime;
    }

    public void setAttendees(int attendees) {
        this.attendees = attendees;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
