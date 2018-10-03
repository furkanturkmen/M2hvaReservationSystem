package com.hva.m2mobi.m2hva_reservationsystem.utils;

import com.google.api.services.calendar.model.Events;

public interface CalendarEventListener {
    void onCalendarEventsReturned(Events events);
}
