package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hva.m2mobi.m2hva_reservationsystem.R;

public class RoomDetailActivity extends AppCompatActivity {

    private Button reserveBtn;
    private View.OnClickListener onClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_detail);

        reserveBtn = findViewById(R.id.reserveBtn);
        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity();
            }
        });
    }

    private void launchActivity(){
        startActivity(new Intent(this,CalendarActivity.class));
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
