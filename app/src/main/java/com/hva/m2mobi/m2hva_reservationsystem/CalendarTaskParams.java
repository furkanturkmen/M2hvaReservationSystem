package com.hva.m2mobi.m2hva_reservationsystem;

import android.app.Activity;

import com.google.api.services.calendar.model.Event;

//Passes required Objects to calendar tasks
class CalendarTaskParams {
    CalendarEventListener result;
    Activity activity;
    String accountName;
    int calendarAction;
    int room;
    Event event;
    static final int GET_ROOM_EVENTS = 0;
    static final int GET_ALL_EVENTS = -1;
    static final int GET_CURRENT_EVENT = 1;
    static final int GET_MY_EVENTS = 2;
    static final int ADD_EVENT = 3;

    static final int ROOM_OREO = 0;
    static final int ROOM_MARS = 1;
    static final int ROOM_KITK = 2;
    static final int ROOM_JELB = 3;
    static final int ROOM_ICSW = 4;

    CalendarTaskParams(CalendarEventListener result, Activity activity) {
        this.result = result;
        this.activity = activity;
    }
}
