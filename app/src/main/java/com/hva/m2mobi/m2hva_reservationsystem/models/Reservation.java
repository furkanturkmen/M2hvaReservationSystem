package com.hva.m2mobi.m2hva_reservationsystem.models;

import android.annotation.TargetApi;
import android.os.Build;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.O)
public class Reservation {
    private String title;
    private ArrayList<String> attendees = new ArrayList<String>();

    private String reservationDate;
    private String startTime;
    private String endTime;

    //reservation object
    public Reservation(String title, ArrayList attendees, String reservationDate, String startTime, String endTime) {
        this.title = title;
        this.attendees = attendees;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //getters for all variables
    public String getTitle() {
        return title;
    }
    public String getReservationDate(){
        return reservationDate;
    }
    public String getStartTime(){
        return startTime;
    }
    public String getEndTime(){
        return endTime;
    }
    public ArrayList getAttendees(){
        return attendees;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setReservationDate(String reservationDate){
        this.reservationDate = reservationDate;
    }
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }
    public void setEndTime(String endTime){
        this.endTime = endTime;
    }
    public void setAttendees(ArrayList attendees){
        this.attendees = attendees;
    }
}
