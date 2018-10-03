package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.model.Events;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarEventListener;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarTask;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarTaskParams;

//Activity to get the account the user wants to use and authorise it for using the calendar
public class AccountAuthorisationActivity extends AppCompatActivity {

    private static final int REQUEST_ACCOUNT_CHOOSER = 123;
    Intent returnIntent = new Intent();
    CalendarTaskParams params = new CalendarTaskParams(new CalendarEventListener() {
        @Override
        public void onCalendarEventsReturned(Events events) {

            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }, this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Build a new authorized API client service.
        GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(this, CalendarTask.SCOPES)
                .setBackOff(new ExponentialBackOff());
        int noOfAccounts = mCredential.getAllAccounts().length;
        if(noOfAccounts > 1) {
            Intent chooseAccount = mCredential.newChooseAccountIntent();
            startActivityForResult(chooseAccount, REQUEST_ACCOUNT_CHOOSER);

        }else{
            setAccountName(mCredential.getAllAccounts()[0].name);
        }

    }

    private void setAccountName(final String name){

        returnIntent.putExtra("name", name);
        params.accountName = name;
        params.calendarAction = CalendarTaskParams.GET_ALL_EVENTS;

        new CalendarTask().execute(params);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {

        if (requestCode == REQUEST_ACCOUNT_CHOOSER && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            setAccountName(accountName);
        }if(requestCode == CalendarTask.ACCOUNT_AUTH_REQUEST && resultCode == RESULT_OK){
            new CalendarTask().execute(params);
        }else if(requestCode == CalendarTask.ACCOUNT_AUTH_REQUEST && resultCode == RESULT_CANCELED){
            //show error asking for authorization
            setResult(Activity.RESULT_CANCELED,returnIntent);
            finish();
        }
    }
}
