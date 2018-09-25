package com.hva.m2mobi.m2hva_reservationsystem;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RoomDetailActivity extends AppCompatActivity {

    private Button reserveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_detail);


        reserveBtn = findViewById(R.id.reserveBtn);


        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackbar();
            }
        });
    }

    public void showSnackbar() {
        Snackbar snackbar = Snackbar.make(reserveBtn, "Your reservation has been completed", Snackbar.LENGTH_INDEFINITE)
                .setAction("cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(reserveBtn, "Your reservation has been cancelled.", Snackbar.LENGTH_INDEFINITE);
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }
}
