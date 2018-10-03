package com.hva.m2mobi.m2hva_reservationsystem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class TabActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_room_booking:
                    mTextMessage.setText(R.string.title_room_booking);
                    RoomBookingFragment fFragment = new RoomBookingFragment();
                    fragmentTransaction.replace(R.id.container_frame,fFragment);
                    break;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.title_profile);
                    ProfileFragment sFragment = new ProfileFragment();
                    fragmentTransaction.replace(R.id.container_frame,sFragment);
                    break;
                default:
                    return false;
            }
            fragmentTransaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        RoomBookingFragment rbFragment = new RoomBookingFragment();
        fragmentTransaction.add(R.id.container_frame, rbFragment);
        fragmentTransaction.commit();
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
