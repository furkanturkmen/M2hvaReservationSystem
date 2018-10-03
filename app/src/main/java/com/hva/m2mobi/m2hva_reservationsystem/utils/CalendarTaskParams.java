package com.hva.m2mobi.m2hva_reservationsystem.utils;

import android.app.Activity;

import com.google.api.services.calendar.model.Event;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarEventListener;

//Passes required Objects to calendar tasks
public class CalendarTaskParams {
    public CalendarEventListener result;
    public Activity activity;
    public String accountName;
    public int calendarAction;
    public int room;
    public Event event;
    public static final int GET_ROOM_EVENTS = 0;
    public static final int GET_ALL_EVENTS = -1;
    public static final int GET_CURRENT_EVENT = 1;
    public static final int GET_MY_EVENTS = 2;
    public static final int ADD_EVENT = 3;
    public static final int ROOM_OREO = 0;
    public static final int ROOM_MARS = 1;
    public static final int ROOM_KITK = 2;
    public static final int ROOM_JELB = 3;
    public static final int ROOM_ICSW = 4;

    public CalendarTaskParams(CalendarEventListener result, Activity activity) {
        this.result = result;
        this.activity = activity;
    }
}
