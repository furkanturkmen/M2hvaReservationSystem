package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.fragments.ReservationOverviewFragment;

import com.hva.m2mobi.m2hva_reservationsystem.fragments.RoomsOverviewFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    FloatingActionButton fab;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new booking
                Intent intent = new Intent(MainActivity.this,ReserveRoomActivity.class);
                startActivity(intent);

            }
        });

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomsOverviewFragment()).commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1_id:
                // Go to item 1
                Toast.makeText(getApplicationContext(), "Item 1 selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item2_id:
                //Go to item 2
                Toast.makeText(getApplicationContext(), "Item 2 selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item3_id:
                //Go to item 3
                Toast.makeText(getApplicationContext(), "Item 3 selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.search_id:
                //Search item
                Toast.makeText(getApplicationContext(), "Search selected", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_rooms:
                selectedFragment = new RoomsOverviewFragment();
                toolbar.setTitle("Rooms");
                break;
            case R.id.nav_reservations:
                selectedFragment = new ReservationOverviewFragment();
                toolbar.setTitle("My reservations");
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


