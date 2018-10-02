package com.hva.m2mobi.m2hva_reservationsystem;

import android.app.Activity;

//Passes required Objects to calendar tasks
class CalendarTaskParams {
    CalendarEventListener result;
    Activity activity;
    String accountName;

    CalendarTaskParams(CalendarEventListener result, Activity activity) {
        this.result = result;
        this.activity = activity;
    }
}
