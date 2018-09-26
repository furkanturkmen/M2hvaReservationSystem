package com.hva.m2mobi.m2hva_reservationsystem;

public class RoomsOverviewPlaceholder {
    private String mRoomName;
    private String mRoomAvailability;

    public RoomsOverviewPlaceholder(String mRoomName, String mRoomAvailability){
        this.mRoomName = mRoomName;
        this.mRoomAvailability = mRoomAvailability;
    }

    public String getmRoomName(){
        return mRoomName;
    }

    public String getmRoomAvailability(){
        return mRoomAvailability;
    }

    public static final String[] PLACEHOLDER_ROOMNAME = {
            "Pie",
            "Oreo",
            "KitKat"
    };

    public static final String[] PLACEHOLDER_ROOMAVAILABILITY = {
            "Taken",
            "Available",
            "Available"
    };

}
