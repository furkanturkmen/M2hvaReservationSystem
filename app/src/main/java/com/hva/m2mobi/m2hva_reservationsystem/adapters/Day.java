package com.hva.m2mobi.m2hva_reservationsystem.adapters;

public class Day {
    private String dayInWeek;
    private int numberInMonth;
    private int numberOfBookings;

    public Day(String dayInWeek, int numberInMonth, int numberOfBookings) {
        this.dayInWeek = dayInWeek;
        this.numberInMonth = numberInMonth;
        this.numberOfBookings = numberOfBookings;
    }

    public String getDayInWeek() {
        return dayInWeek;
    }

    public void setDayInWeek(String dayInWeek) {
        this.dayInWeek = dayInWeek;
    }

    public int getNumberInMonth() {
        return numberInMonth;
    }

    public void setNumberInMonth(int numberInMonth) {
        this.numberInMonth = numberInMonth;
    }

    public int getNumberOfBookings() {
        return numberOfBookings;
    }

    public void setNumberOfBookings(int numberOfBookings) {
        this.numberOfBookings = numberOfBookings;
    }
}
