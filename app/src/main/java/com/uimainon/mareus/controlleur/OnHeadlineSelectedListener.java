package com.uimainon.mareus.controlleur;

import android.widget.CalendarView;
import android.widget.TimePicker;

import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.Room;

public interface OnHeadlineSelectedListener {

    void callTimeChanged(TimePicker view, int hour, int minute);
    void callDateChanged(CalendarView view, int year, int month, int day);
    void callParticipantChanged(Participant participants, int dispoOrNotDispo);
    void callRoomChanged(Room mRoom);
    void callValidNewActivity();
    void callDialogRoom();
}
