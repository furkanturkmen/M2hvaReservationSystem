package com.hva.m2mobi.m2hva_reservationsystem.models;

import android.annotation.TargetApi;
import android.os.Build;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.O)
public class Reservation {

    private ArrayList<String> attendees = new ArrayList<String>();
    private String reservationDate;// = formattedDate;
    private String startTime;// = formattedTime;
    private String endTime;// = formattedTime;

    public Room reservationRoom;

    //reservation object
    public Reservation(ArrayList attendees, String reservationDate, String startTime, String endTime, Room room) {
        this.attendees = attendees;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationRoom = room;
    }

    //getters for all variables
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

    public Room getReservationRoom() {
        return reservationRoom;
    }

    public void setReservationRoom(Room reservationRoom) {
        this.reservationRoom = reservationRoom;
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

    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }
}
