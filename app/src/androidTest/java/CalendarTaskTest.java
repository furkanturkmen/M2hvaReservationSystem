

import android.app.Activity;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.util.Pair;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.hva.m2mobi.m2hva_reservationsystem.activities.CalendarActivity;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarEventListener;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarTask;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarTaskParams;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


// @RunWith is required only if you use a mix of JUnit3 and JUnit4.
@RunWith(AndroidJUnit4.class)
@SmallTest
public class CalendarTaskTest{

    @Rule
    public ActivityTestRule<CalendarActivity> mActivityRule = new ActivityTestRule<>(
            CalendarActivity.class);

    private CalendarTaskParams params;
    private CalendarEventListener listener;

    @Before
    public void createParams(){
        params = new CalendarTaskParams(listener,mActivityRule.getActivity());
        params.accountName = "kylewatson98@gmail.com";
    }

    @Test
    public void getEvents(){
        params.calendarAction = CalendarTaskParams.GET_ALL_EVENTS;
        final CountDownLatch signal = new CountDownLatch(1);
        listener = new CalendarEventListener() {
            @Override
            public void onCalendarEventsReturned(Events events) {
                assertThat(events != null, is(true));
                signal.countDown();
            }
        };
        params.result = listener;
        CalendarTask task = new CalendarTask();
        task.execute(params);
        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addEvent(){

        params.calendarAction = CalendarTaskParams.ADD_EVENT;
        params.event = new Event();
        params.room = CalendarTaskParams.ROOM_ICSW;
        params.event.setSummary("Add event no_permission");
        params.event.setDescription("Add event no_permission desc");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        params.event.setStart(new EventDateTime().setDate(new DateTime(calendar.getTime())));
        calendar.add(Calendar.HOUR, 1);
        params.event.setStart(new EventDateTime().setDate(new DateTime(calendar.getTime())));
        CalendarTask task = new CalendarTask();
        task.execute(params);


        final CountDownLatch signal = new CountDownLatch(1);
        params.calendarAction = CalendarTaskParams.GET_ROOM_EVENTS;
        listener = new CalendarEventListener() {
            @Override
            public void onCalendarEventsReturned(Events events) {
                assertThat(events != null, is(true));
                List<Event> eventList = events.getItems();
                assertThat(eventList.isEmpty(), is(false));
                Event myEvent = new Event();
                for(Event event : eventList){
                    if(event.getCreator().getEmail().equals(params.accountName)){
                        myEvent = event;
                        break;
                    }
                }
                assertThat(myEvent.getSummary(), is(params.event.getSummary()));
                assertThat(myEvent.getStart(), is(params.event.getStart()));
                assertThat(myEvent.getDescription(), is(params.event.getDescription()));
                assertThat(myEvent.getEnd(), is(params.event.getEnd()));
                signal.countDown();
            }
        };
        params.result = listener;
        task.execute(params);
        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

