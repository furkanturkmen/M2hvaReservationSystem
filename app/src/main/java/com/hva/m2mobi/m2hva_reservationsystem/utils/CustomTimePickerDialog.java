package com.hva.m2mobi.m2hva_reservationsystem.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Constructor;
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
        TimePicker timePicker = fixSpinner(context,hourOfDay, minute, true);
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
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

    private TimePicker fixSpinner(Context context, int hourOfDay, int minute, boolean is24HourView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // android:timePickerMode spinner and clock began in Lollipop
            try {
                // Get the theme's android:timePickerMode
                //two modes are available clock mode and spinner mode ... selecting spinner mode for latest versions
                final int MODE_SPINNER = 2;
                Class<?> styleableClass = Class.forName("com.android.internal.R$styleable");
                Field timePickerStyleableField = styleableClass.getField("TimePicker");
                int[] timePickerStyleable = (int[]) timePickerStyleableField.get(null);
                final TypedArray a = context.obtainStyledAttributes(null, timePickerStyleable, android.R.attr.timePickerStyle, 0);
                Field timePickerModeStyleableField = styleableClass.getField("TimePicker_timePickerMode");
                int timePickerModeStyleable = timePickerModeStyleableField.getInt(null);
                final int mode = a.getInt(timePickerModeStyleable, MODE_SPINNER);
                a.recycle();
                if (mode == MODE_SPINNER) {
                    TimePicker timePicker = (TimePicker) findField(TimePickerDialog.class, TimePicker.class, "mTimePicker").get(this);
                    Class<?> delegateClass = Class.forName("android.widget.TimePicker$TimePickerDelegate");
                    Field delegateField = findField(TimePicker.class, delegateClass, "mDelegate");
                    Object delegate = delegateField.get(timePicker);
                    Class<?> spinnerDelegateClass;
                    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP) {
                        spinnerDelegateClass = Class.forName("android.widget.TimePickerSpinnerDelegate");
                    } else {

                        spinnerDelegateClass = Class.forName("android.widget.TimePickerClockDelegate");
                    }
                    if (delegate.getClass() != spinnerDelegateClass) {
                        delegateField.set(timePicker, null); // throw out the TimePickerClockDelegate!
                        timePicker.removeAllViews(); // remove the TimePickerClockDelegate views
                        Constructor spinnerDelegateConstructor = spinnerDelegateClass.getConstructor(TimePicker.class, Context.class, AttributeSet.class, int.class, int.class);
                        spinnerDelegateConstructor.setAccessible(true);
                        // Instantiate a TimePickerSpinnerDelegate
                        delegate = spinnerDelegateConstructor.newInstance(timePicker, context, null, android.R.attr.timePickerStyle, 0);
                        delegateField.set(timePicker, delegate); // set the TimePicker.mDelegate to the spinner delegate
                        // Set up the TimePicker again, with the TimePickerSpinnerDelegate
                        timePicker.setIs24HourView(is24HourView);
                        timePicker.setCurrentHour(hourOfDay);
                        timePicker.setCurrentMinute(minute);
                        timePicker.setOnTimeChangedListener(this);
                    }
                    return timePicker;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private static Field findField(Class objectClass, Class fieldClass, String expectedName) {
        try {
            Field field = objectClass.getDeclaredField(expectedName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {} // ignore
        // search for it if it wasn't found under the expected ivar name
        for (Field searchField : objectClass.getDeclaredFields()) {
            if (searchField.getType() == fieldClass) {
                searchField.setAccessible(true);
                return searchField;
            }
        }
        return null;
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minuteOfHour){
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
