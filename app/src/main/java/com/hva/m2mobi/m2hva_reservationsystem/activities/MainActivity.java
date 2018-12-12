package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.FirebaseDatabase;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.fragments.ReservationOverviewFragment;

import com.hva.m2mobi.m2hva_reservationsystem.fragments.RoomsOverviewFragment;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;
import com.hva.m2mobi.m2hva_reservationsystem.utils.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Toolbar mToolbar;
    private BottomNavigationView mBottomNav;
    public static final int REQUEST_RESERVE_ROOM = 1337;

    private static final int REQUEST_PERMISSIONS_CALENDAR = 111;
    private static final int REQUEST_ACCOUNT_CALENDAR = 222;
    public static List<Room> roomsOutDB = new ArrayList<>();

    private View permissionView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // Add new booking
                if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR)
                        == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.GET_ACCOUNTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, ReserveRoomActivity.class);
                    startActivityForResult(intent, REQUEST_RESERVE_ROOM);
                }
            }
        });

        try {
            roomsOutDB = DatabaseConnection.getRooms();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(this);
        requestPermissions();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBottomNav.setSelectedItemId(R.id.nav_rooms);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            FrameLayout view = findViewById(R.id.fragment_container);
//            RelativeLayout layout = (RelativeLayout) view.getParent();
//            layout.removeView(view);
            view.removeAllViewsInLayout();
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
        return false;
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

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions(){
        if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(this),new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.GET_ACCOUNTS},REQUEST_PERMISSIONS_CALENDAR);
        } else {
            mBottomNav.setSelectedItemId(R.id.nav_rooms);
        }
    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == REQUEST_PERMISSIONS_CALENDAR && grantResults[0] == 0 && grantResults[1] == 0){
            mBottomNav.setSelectedItemId(R.id.nav_rooms);
        } else {
            LayoutInflater layoutInflater = getLayoutInflater();
            ViewGroup frameLayout = (ViewGroup) findViewById(R.id.fragment_container);
            frameLayout.removeAllViewsInLayout();
            permissionView = layoutInflater.inflate(R.layout.no_permission, frameLayout);
            Button button = permissionView.findViewById(R.id.givePermission);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissions();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Intent newIntent = new Intent(this, LoginActivity.class);
                newIntent.putExtra(LoginActivity.LOGOUT_EXTRA, true);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newIntent);
                finish();
                return false;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}


