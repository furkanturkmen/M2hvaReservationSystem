package com.hva.m2mobi.m2hva_reservationsystem.models;

import java.util.Date;

public class TimeSlot {
    Date startTime;
    Date endTime;
    TimeSlot next;
    TimeSlot previous;

    public TimeSlot(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
