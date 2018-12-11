package com.hva.m2mobi.m2hva_reservationsystem.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;

public class CustomTimePickerDialog extends TimePickerDialog {
    private int minMinute;
    private int minHour;
    private int maxMinute;
    private int maxHour;
    private NumberPicker minuteSpinner;

    public CustomTimePickerDialog(Context context, OnTimeSetListener listener, int hourOfDay, int minute, int maxHour, int maxMinute) {
        super(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, listener, hourOfDay, minute, true);
        this.minMinute = minute;
        this.minHour = hourOfDay;
        this.maxMinute = maxMinute;
        this.maxHour = maxHour;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            TimePicker timePicker = findViewById(timePickerField.getInt(null));
            Field field = classForid.getField("minute");

            minuteSpinner = timePicker
                    .findViewById(field.getInt(null));
            minuteSpinner.setMinValue(minMinute);
            if (minHour == maxHour){
                minuteSpinner.setMaxValue(maxMinute);
            }

            Field hourField = classForid.getField("hour");

            NumberPicker hourSpinner = timePicker.findViewById(hourField.getInt(null));
            hourSpinner.setMinValue(minHour);
            hourSpinner.setMaxValue(maxHour);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minuteOfHour){
        Log.d("CustomTimePicker", "updateTime: " + hourOfDay + " " + minuteOfHour);
        super.updateTime(hourOfDay, minuteOfHour);
        if (hourOfDay > minHour){
            minuteSpinner.setMinValue(0);
        } else {
            minuteSpinner.setMinValue(minMinute);
        }
        if (hourOfDay < maxHour){
            minuteSpinner.setMaxValue(59);
        } else {
            minuteSpinner.setMaxValue(maxMinute);
        }
    }
}
