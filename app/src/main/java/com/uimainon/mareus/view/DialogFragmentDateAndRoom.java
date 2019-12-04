package com.uimainon.mareus.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.uimainon.mareus.R;
import com.uimainon.mareus.controlleur.MeetingService;
import com.uimainon.mareus.di.DI;
import com.uimainon.mareus.events.ChoiceRoomEvent;
import com.uimainon.mareus.model.Room;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.List;

public class DialogFragmentDateAndRoom extends DialogFragment  {

    private MeetingService mMeetingService = DI.getMeetingService();
    private List<Room> mRoom = mMeetingService.AllRooms();
    private GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;
        assert this.getArguments() != null;
        boolean showChoice = this.getArguments().getBoolean("showDateRoom");
        boolean showChoiceFilter = this.getArguments().getBoolean("showRoom");
        if(!showChoice){
            v = inflater.inflate(R.layout.dialogue_no_filter, container, false);
        }else{
            if(showChoiceFilter){
                v = inflater.inflate(R.layout.dialogue_filter_room, container, false);
                initListRoom(v);
            }else{
                v = inflater.inflate(R.layout.calendar_dialogue, container, false);
                initListDate(v);
            }
        }
        ImageButton button = (ImageButton) v.findViewById(R.id.btn_close_dialog_room);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }


    //-------------------------------------------------------------------------
    public interface DialogListener {
        public void callDialogDate(int year, int month, int day);
        public void callDialogRoom(Room mRoom);
    }

    //-------------------------------------------------------------------------
    private void initListRoom(View view) {
        mGridView = view.findViewById(R.id.gridviewRoom);
        mGridView.setAdapter(new ListRoomGridAdapter(mRoom, getContext()));
    }
    private void initListDate(View view) {
        CalendarView mCalendar = view.findViewById(R.id.calendar_dialog_meeting);
        Calendar calendar = Calendar.getInstance();
        long showDate = calendar.getTimeInMillis();
        mCalendar.setDate(showDate, true, true);
        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                DialogListener listener  = (DialogListener) getTargetFragment();
                assert listener != null;
                listener.callDialogDate(year, month, day);
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * eventBus lors de la sélection de la room dans la boite de dialog
     * @param event
     */
    @Subscribe
    public void onChoiceRoom(ChoiceRoomEvent event) {
        DialogListener listener = (DialogListener) getTargetFragment();
        assert listener != null;
        listener.callDialogRoom(event.room);
        dismiss();
    }

}
