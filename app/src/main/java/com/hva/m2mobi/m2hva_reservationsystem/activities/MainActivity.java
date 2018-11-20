package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.fragments.ReservationOverviewFragment;

import com.hva.m2mobi.m2hva_reservationsystem.fragments.RoomsOverviewFragment;
import com.hva.m2mobi.m2hva_reservationsystem.utils.DatabaseConnection;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Toolbar mToolbar;
    private BottomNavigationView mBottomNav;
    public static final int REQUEST_RESERVE_ROOM = 1337;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new booking
                Intent intent = new Intent(MainActivity.this,ReserveRoomActivity.class);
                startActivityForResult(intent,REQUEST_RESERVE_ROOM);
            }
        });

        mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBottomNav.setSelectedItemId(R.id.nav_rooms);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = new RoomsOverviewFragment();
        switch (menuItem.getItemId()) {
            case R.id.nav_rooms:
                selectedFragment = new RoomsOverviewFragment();
                mToolbar.setTitle(R.string.rooms);
                break;
            case R.id.nav_reservations:
                selectedFragment = new ReservationOverviewFragment();
                mToolbar.setTitle(R.string.my_reservations);
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // No user is signed in
            sentToLogin();
        }
    }

    private void sentToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}


