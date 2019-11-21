package com.uimainon.mareus.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.uimainon.mareus.R;
import com.uimainon.mareus.controlleur.OnHeadlineSelectedListener;
import com.uimainon.mareus.model.Meeting;

import java.util.Calendar;

public class NewMeetingDateHourFragment extends Fragment {

    private OnHeadlineSelectedListener callback;


    /**
     * Create and return a new instance
     * @return @{@link NewMeetingDateHourFragment}
     */
    public static NewMeetingDateHourFragment newInstance() {
        NewMeetingDateHourFragment fragment = new NewMeetingDateHourFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View drawer = inflater.inflate(R.layout.fragment_add_meeting_date_hour, container, false);
        Meeting mMeeting = this.getArguments().getParcelable("meeting");
        String date = mMeeting.getDate();
        String[] splitArray = date.split("-");

        int mYear = Integer.parseInt(splitArray[0]);
        int mMonth = Integer.parseInt(splitArray[1]);
        int mDay = Integer.parseInt(splitArray[2]);

        initHour(drawer, mMeeting.getHour(), mMeeting.getMinute());
        initDate(drawer, mYear, mMonth, mDay);

        return drawer;
    }

    //-------------------------------------------------------------------------
    public interface OnHeadlineSelectedListener {
        public void callDateChanged(CalendarView calendarView, int position, int i1, int i2);
        public void callTimeChanged(TimePicker view, int hour, int minute);
    }

    public void setOnHeadlineSelectedListener(OnHeadlineSelectedListener callback) {
        this.callback = callback;
    }
    //-------------------------------------------------------------------------
    /**
     * affiche la date actuelle en  premier temps et affichera la nouvelle sélectionnée plus tard
     * @param drawer
     * @param mYear
     * @param mMonth
     * @param mDay
     */
    private void initDate(View drawer, int mYear, int mMonth, int mDay){

        CalendarView mCalendar = drawer.findViewById(R.id.calendar_meeting);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mYear);
        calendar.set(Calendar.MONTH, mMonth);
        calendar.set(Calendar.DAY_OF_MONTH, mDay);

        long showDate = calendar.getTimeInMillis();

        mCalendar.setDate(showDate, true, true);
        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                callback.callDateChanged(calendarView, i, i1, i2);
            }
        });
    }

    /**
     * affiche l'heure et les minutes dans le fragment. Ecouteur de placé en cas de modification
     * @param drawer
     * @param hour
     * @param minute
     */
    private void initHour(View drawer, int hour, int minute) {
        TimePicker mTimepicker = drawer.findViewById(R.id.timePicker);
        mTimepicker.setIs24HourView(true);
        mTimepicker.setCurrentHour(hour);
        mTimepicker.setCurrentMinute(minute);
        mTimepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hour, int minute) {
                callback.callTimeChanged(view, hour, minute);
            }
        });
    }




}
