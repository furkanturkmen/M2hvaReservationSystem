package com.hva.m2mobi.m2hva_reservationsystem.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DatabaseService {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference roomRef = database.getReference("rooms");
    DatabaseReference reservationRef = database.getReference("reservations");
    String BASE_URL = database.toString();

    Retrofit retrofitBuild = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        @GET("/reservations/")
        Call<List<Reservation>> getAllReservations();

        @GET("/rooms")
        Call<List<Room>> getAllRooms();

        @GET("/reservations/{dbRoom}")
        Call<Room> getRoom(@Path("dbRoom") String dbRoomId);

        @GET("/reservations/{dbRes}")
        Call<Reservation> getReservedRoom(@Path("dbRes") String dbResId);

        @POST("/reservations/new")
        Call<Reservation> insertReservation(@Body Reservation reservation);

        @POST("/rooms/new")
        Call<Room> insertRoom(@Body Room room);

        @DELETE("/reservations")
        Call<Reservation> deleteRoom();



}

