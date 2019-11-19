package com.uimainon.mareus.rooteur.utils;

import android.view.View;
import android.widget.CalendarView;
import android.widget.TimePicker;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.uimainon.mareus.R;
import com.uimainon.mareus.view.NewMeetingDateHourFragment;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

public class TimePickerAction {

    /**
     * Returns a {@link ViewAction} that sets time on timePicker.
     */
    public static ViewAction setTime(final int hours, final int minutes) {

        return new ViewAction() {


            @Override
            public void perform(UiController uiController, View view) {
                final TimePicker timePicker = (TimePicker) view;
                timePicker.setIs24HourView(true);
                timePicker.setCurrentHour(hours);
                timePicker.setCurrentMinute(minutes);
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hour, int minute) {
                        //callback.callTimeChanged(view, hour, minute);
                    }
                });
            }

            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "set time";
            }

        };


    }
}
