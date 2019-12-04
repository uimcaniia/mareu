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
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewMeetingParticipantFragment extends Fragment {
    private MeetingService mMeetingService;
    private RecyclerView mRecyclerView;
    private OnHeadlineSelectedListener callback;

    /**
     * Create and return a new instance
     * @return @{@link NewMeetingParticipantFragment}
     */
    public static NewMeetingParticipantFragment newInstance() {
        return new NewMeetingParticipantFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMeetingService = DI.getMeetingService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        assert this.getArguments() != null;
        Meeting mMeeting = this.getArguments().getParcelable("meeting");
        assert mMeeting != null;
        List<Participant> mParticipants = mMeetingService.getListParticipantForThisDate(mMeeting.getDate(), mMeeting.getHour(), mMeeting.getMinute());
        List<Participant>mParticipantsOfMeeting = mMeeting.getParticipants();

        if(mParticipants.size() != 0){
            View drawer = inflater.inflate(R.layout.fragment_add_metting_participant_list, container, false);
            Context context = drawer.getContext();
            mRecyclerView = (RecyclerView) drawer.findViewById(R.id.list_participant);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context)); // va positionner les élément en ligne, par défault de haut en bas
            mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
            initListParticipant(mParticipants, mParticipantsOfMeeting);
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
     * @param mParticipants la liste complète de tous les participants
     * @param mParticipantsOfMeeting liste des participant de la réunion consernée. Vide si nouvelle réunion et pleine si c'est une réunion à modifier
     */
    private void initListParticipant(List<Participant> mParticipants, List<Participant> mParticipantsOfMeeting) {
        List<Participant> mParticioantRecyclerView = new ArrayList<>(mParticipants);

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
                if (!mParticipants.contains(mParticipantOfMeeting)) { // positionne en haut de liste les participants déjà sélectionné de la réunion
                    Participant part = mParticipantOfMeeting;
                    mParticioantRecyclerView.add(index, part);
                    index += 1;
                }
            }
            for(int i = 0 ; i <sizeOfList ; i++){// positionne le status de PAS sélectionné pour la checkbox du recyclerView
                mParticioantRecyclerView.get(i).setDispo(0);
                for(int x = 0 ; x <sizeOfListPresent ; x++){ // positionne le status de DEJA sélectionné pour la checkbox du recyclerView
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
