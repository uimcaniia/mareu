package com.uimainon.mareus.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uimainon.mareus.R;
import com.uimainon.mareus.di.DI;
import com.uimainon.mareus.events.DeleteMeetingEvent;
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.ParticipantsList;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.rooteur.AddMeetingActivity;
import com.uimainon.mareus.service.DateService;
import com.uimainon.mareus.controlleur.MeetingService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListMeetingFragment extends Fragment implements DialogFragmentDateAndRoom.DialogListener {

    private static final int RESULT_OK = 1;
    private List<Meeting> mMeetings;
    private RecyclerView mRecyclerView;
    private TextView mTextViewNothing;
    private MeetingService mMeetingService;
    private Meeting mNewMeeting;
    private List<Participant> mParticipant;
    private Room mRoom;
    private DateService mDate;
    private DialogFragmentDateAndRoom mDialogFragment;
/*    public static final int RESULT_ROOM = 1; //
    public static final int RESULT_DATE = 2;*/



    public static ListMeetingFragment newInstance() {
        ListMeetingFragment fragment = new ListMeetingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDate = new DateService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meeting, container, false);
        configBtnAddNewMeeting(rootView);
        mRecyclerView = rootView.findViewById(R.id.list_meeting); // s'affichera si des réunion existe
        mTextViewNothing = rootView.findViewById(R.id.textViewNothing); // s'affichera si aucune réunion existe
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMeetingService = DI.getMeetingService();
        mMeetings = mMeetingService.AllMeetings();
        try {
            initList(mMeetings, null, 0, 0, 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    /**
     * création du menu de filtrage des réunions
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       menu.clear();
       inflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * item du menu (filtre room ou filtre date)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        mMeetings = mMeetingService.AllMeetings();
        DialogFragmentDateAndRoom mDialogFragmentRoom = new DialogFragmentDateAndRoom();
        FragmentManager fm = getFragmentManager();
        Bundle args = new Bundle();
        if(mMeetings.size() == 0){
            args.putBoolean("showDateRoom", false);
        }else{
            args.putBoolean("showDateRoom", true);
        }
        switch (id) {
            case R.id.action_filter_date:
                args.putBoolean("showRoom", false);
                mDialogFragmentRoom.setArguments(args);
                assert getFragmentManager() != null;
                mDialogFragmentRoom.setTargetFragment(ListMeetingFragment.this, 300);
                mDialogFragmentRoom.show(fm, "MyRoomDialog");
               // filterBy("date");
                return true;

            case R.id.action_filter_room:
                args.putBoolean("showRoom", true);
                mDialogFragmentRoom.setArguments(args);
                assert getFragmentManager() != null;
                mDialogFragmentRoom.setTargetFragment(ListMeetingFragment.this, 300);
                mDialogFragmentRoom.show(fm, "MyRoomDialog");
                //filterBy("date");
                return true;

            case R.id.action_filter_none:
                filterBy(null, 0,0, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void callDialogDate(int year, int month, int day) {
        Room roomNull = null;
        filterBy(roomNull, year,month, day);
    }

    @Override
    public void callDialogRoom(Room mRoom) {
        filterBy(mRoom, 0,0, 0);
    }

    /**
     * réinitialise la liste des réunion en fonction du filtre choisie
     * @param
     */
    private void filterBy(Room mRoomSelect, int yearSelect, int monthSelect, int daySelect){
        mMeetings = mMeetingService.AllMeetings();
        try {
            initList(mMeetings, mRoomSelect, yearSelect, monthSelect, daySelect);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Init la Liste des meeting
     * @param mMeetings
     * @param  yearSelect sélectionné pour l'ordre d'affichage de la liste(room ou date) sinon vaut 0 et affichere suivant l'ordre des ID des réunion
     */
    private void initList(List<Meeting> mMeetings, Room mRoomSelect, int yearSelect, int monthSelect, int daySelect) throws ParseException {
        List<Meeting> mMeetingFilter = new ArrayList<>(mMeetings);

        if(mMeetingFilter.size()!=0) {
            if((yearSelect != 0)&&(mRoomSelect == null)){ // on vérifie si il y a un filtre
                mMeetingFilter = mMeetingService.makeGoogOrderDateListMeeting(mMeetingFilter, yearSelect, monthSelect, daySelect);
            }if((yearSelect == 0)&&(mRoomSelect != null)){
                mMeetingFilter = mMeetingService.makeGoogOrderRoomListMeeting(mMeetingFilter, mRoomSelect);
            }
        }

        if(mMeetingFilter.size()==0){ // si aucune réunion n'existe
            mTextViewNothing.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            mTextViewNothing.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            Context context = getActivity();
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setAdapter(new MeetingRecyclerViewAdapter(mMeetingFilter));
        }
    }

    /** configure le bouton de création de nouvelle réunion
     * prend par défault la date d'aujourd'hui et l'heure courante.
     * vérifie si dans ce crénaux, une salle de réunion est disponible.
     *      => si rien de dispo, avance l'heure de 1h jusqu'à la fin de la journée
     *      => si toujours rien de dispo, cherche au jour d'après la prochaine dispo  (sur max 365 jours)
     * @param view
     */
    private void configBtnAddNewMeeting(View view) {
        FloatingActionButton mAddButton = (FloatingActionButton) view.findViewById(R.id.fab_add_new_metting);
        mAddButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Context context = getContext();
                Intent intent = new Intent ( context , AddMeetingActivity.class );

                int idMeeting = mMeetingService.searchNextIdForNewMeeting();
                int mMinute = mDate.giveMinute()+5;//(petite marge pour le temps de création et enregistrement')

                Date today = mDate.formatDateToCompare(mDate.giveYear(), mDate.giveMonth(), mDate.giveDay());
                int goodHourInFrance = 0;
                try { //donne la bonne heure en France (heure d'été et hivers
                    goodHourInFrance = mDate.giveTheGoodHourInFrance(mDate.giveYear() ,mDate.giveHour() , today);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String sDateToday = mDate.getStringDate(mDate.addCalendarToDate(today, 0));
                List<Room> mRooms = mMeetingService.getListRoomForThisDate(sDateToday, goodHourInFrance, mMinute);

                if(mRooms.size()==0) { // si aucune Room de dispo, on cherche le prochaine horaire
                    mNewMeeting =  mMeetingService.searchOtherDateAndTimeForNewMeeting(sDateToday, today, goodHourInFrance, idMeeting);
                    if(mNewMeeting.getRoom() == null){
                        Toast.makeText(getContext(), "Aucune salle n'est dispo. Aucune réunion ne peut etre crée. Agrandissez les locaux!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mRoom = mRooms.get(0);
                    mNewMeeting = new Meeting(idMeeting, sDateToday, goodHourInFrance, mMinute, "Aucun sujet", new ParticipantsList(), mRoom);
                }
                assert context != null;
                intent.putExtra("receiveMeeting", mNewMeeting);
                context.startActivity (intent);
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this); //enregistre l’objet destination en temps que receveur auprès d’EventBus, en prenant en compte le cycle de vie de l’activité ou du fragment
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * eventBus lors d'une suppression de réunion
     * @param event
     */
    @Subscribe
    public void onDeleteMeeting(DeleteMeetingEvent event) {
        mMeetingService.deleteMeeting(event.meeting);
        mMeetings = mMeetingService.AllMeetings();
        try {
            initList(mMeetings, null, 0,0,0);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
