package com.hva.m2mobi.m2hva_reservationsystem;

import android.app.Activity;

public class CalendarTaskParams {
    public TaskIF result;
    public Activity activity;

    public CalendarTaskParams(TaskIF result, Activity activity) {
        this.result = result;
        this.activity = activity;
    }
}
