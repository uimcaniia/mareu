package com.uimainon.mareus.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uimainon.mareus.R;
import com.uimainon.mareus.controlleur.MeetingService;
import com.uimainon.mareus.di.DI;
import com.uimainon.mareus.events.DispoParticipantEvent;
import com.uimainon.mareus.events.NotDispoParticipantEvent;
import com.uimainon.mareus.controlleur.OnHeadlineSelectedListener;
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class NewMeetingParticipantFragment extends Fragment {
    private MeetingService mMeetingService;
    private RecyclerView mRecyclerView;
    private OnHeadlineSelectedListener callback;

    /**
     * Create and return a new instance
     * @return @{@link NewMeetingParticipantFragment}
     */
    public static NewMeetingParticipantFragment newInstance() {
        NewMeetingParticipantFragment fragment = new NewMeetingParticipantFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMeetingService = DI.getMeetingService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Meeting mMeeting = this.getArguments().getParcelable("meeting");
        List<Participant> mParticipants = mMeetingService.getListParticipantForThisDate(mMeeting.getDate(), mMeeting.getHour(), mMeeting.getMinute(), mMeeting);
        List<Participant>mParticipantsOfMeeting = mMeeting.getParticipants();
        List<Meeting> meetingList = mMeetingService.AllMeetings();

        if(mParticipants.size() != 0){
            View drawer = inflater.inflate(R.layout.fragment_add_metting_participant_list, container, false);
            Context context = drawer.getContext();
            mRecyclerView = (RecyclerView) drawer.findViewById(R.id.list_participant);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context)); // va positionner les élément en ligne, par défault de haut en bas
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            initListParticipant(mParticipants, mParticipantsOfMeeting, meetingList);
            return drawer;
        }else{
            View drawer = inflater.inflate(R.layout.fragment_add_meeting_no_participant, container, false);
            return drawer;
        }
    }

    //-------------------------------------------------------------------------
    public interface OnHeadlineSelectedListener {
        public void callParticipantChanged(Participant participant, int position);

    }

    public void setOnHeadlineSelectedListener(OnHeadlineSelectedListener callback) {
        this.callback = callback;
    }
    //-------------------------------------------------------------------------

    /**
     * Init the List of Participants in RecyclerView
     */
    private void initListParticipant(List<Participant> mParticipants, List<Participant> mParticipantsOfMeeting, List<Meeting> meetingList) {
        List<Participant> mParticioantRecyclerView = new ArrayList<>(mParticipants);
        int sizeOfListSelected = mParticipantsOfMeeting.size();
        int sizeListOfAllPartticipants = mParticipants.size();

        for(int i = 0 ; i < sizeOfListSelected ; i++){
            for(int x = 0 ; x < sizeListOfAllPartticipants ; x++){
                if(mParticipantsOfMeeting.get(i).getId() == mParticipants.get(x).getId()){
                    mParticipants.get(x).setDispo(1);
                    break;
                }
            }
        }

    if(mParticipantsOfMeeting.size()==0){
        for(Participant mParticipant : mParticioantRecyclerView) {
            mParticipant.setDispo(0);
        }
        mRecyclerView.setAdapter(new NewMeetingParticipantRecyclerViewAdapter(mParticipants));
    }else{
        int sizeOfList = mParticipants.size();
        int sizeOfListPresent = mParticipantsOfMeeting.size();
        int index = 0;

        for (Participant mParticipantOfMeeting : mParticipantsOfMeeting) {
            if (!mParticipants.contains(mParticipantOfMeeting)) {
                Participant part = mParticipantOfMeeting;
                mParticioantRecyclerView.add(index, part);
                index += 1;
            }
        }
        for(int i = 0 ; i <sizeOfList ; i++){
            mParticioantRecyclerView.get(i).setDispo(0);
            for(int x = 0 ; x <sizeOfListPresent ; x++){
                if(mParticioantRecyclerView.get(i).getId()==mParticipantsOfMeeting.get(x).getId()){
                    mParticioantRecyclerView.get(i).setDispo(1);
                }
            }
        }
        mRecyclerView.setAdapter(new NewMeetingParticipantRecyclerViewAdapter(mParticioantRecyclerView));

    }
}



    @Override
    public void onStart() {
        super.onStart();
        //enregistre l’objet destination en temps que receveur auprès d’EventBus, en prenant en compte le cycle de vie de l’activité ou du fragment
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }



    @Subscribe
    public void onChangeDispo(DispoParticipantEvent event) {
        callback.callParticipantChanged(event.participant, 1);
    }

    @Subscribe
    public void onChangeNotDispo(NotDispoParticipantEvent event) {
        callback.callParticipantChanged(event.participant, 0);
    }


}
