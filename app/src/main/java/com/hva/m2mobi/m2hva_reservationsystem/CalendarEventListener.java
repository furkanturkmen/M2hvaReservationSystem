package com.hva.m2mobi.m2hva_reservationsystem;

import com.google.api.services.calendar.model.Events;

interface CalendarEventListener {
    void onCalendarEventsReturned(Events events);
}
