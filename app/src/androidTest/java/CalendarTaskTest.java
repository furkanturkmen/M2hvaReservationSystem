

import android.app.Activity;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.google.api.services.calendar.model.Events;
import com.hva.m2mobi.m2hva_reservationsystem.activities.CalendarActivity;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarEventListener;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarTask;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarTaskParams;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
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
        params.calendarAction = CalendarTaskParams.GET_ALL_EVENTS;
    }

    @Test
    public void getEvents(){
        listener = new CalendarEventListener() {
            @Override
            public void onCalendarEventsReturned(Events events) {
                assertThat(events != null, is(true));
                assertThat(events.isEmpty(), is(false));
            }
        };
        params.result = listener;
        CalendarTask task = new CalendarTask();
        task.execute(params);
       // getActivity();

        //assertThat(InstrumentationRegistry.getContext() ==null, is(true));
        // Verify that the received data is correct.
        //assertThat(createdFromParcelData.size(), is(1));
    }
}

