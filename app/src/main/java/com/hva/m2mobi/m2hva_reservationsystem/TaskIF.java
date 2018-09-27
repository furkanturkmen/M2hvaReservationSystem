package com.hva.m2mobi.m2hva_reservationsystem;

import com.google.api.services.calendar.model.Events;

public interface TaskIF {
    public void onCalendarEventsReturned(Events events);
}
