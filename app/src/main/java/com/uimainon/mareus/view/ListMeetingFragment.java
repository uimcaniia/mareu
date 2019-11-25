package com.uimainon.mareus.view;

import android.app.Activity;
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

public class ListMeetingFragment extends Fragment {

    private static final int RESULT_OK = 1;
    private List<Meeting> mMeetings;
    private RecyclerView mRecyclerView;
    private TextView mTextViewNothing;
    private MeetingService mMeetingService;
    private Meeting mNewMeeting;
    private List<Participant> mParticipant;
    private Room mRoom;
    private DateService mDate;
    private DialogFragmentDateAndRoom mDialogFragmentRoom;
    public static final int RESULT_ROOM = 1; //
    public static final int RESULT_DATE = 2;



    public static ListMeetingFragment newInstance() {
        ListMeetingFragment fragment = new ListMeetingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setHasOptionsMenu(true);
        mMeetingService = DI.getMeetingService();
        mDate = new DateService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nothing, container, false);
        configBtnAddNewMeeting(rootView);
        mRecyclerView = rootView.findViewById(R.id.list_meeting); // s'affichera si des réunion existe
        mTextViewNothing = rootView.findViewById(R.id.textViewNothing); // s'affichera si aucune réunion existe
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMeetings = mMeetingService.AllMeetings();
        try {
            initList(mMeetings, "");
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
        mDialogFragmentRoom = new DialogFragmentDateAndRoom();
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
                mDialogFragmentRoom.setTargetFragment(this, RESULT_DATE);
                assert getFragmentManager() != null;
                mDialogFragmentRoom.show(getFragmentManager().beginTransaction(), "MyRoomDialog");
                filterBy("date");
                return true;

            case R.id.action_filter_room:
                args.putBoolean("showRoom", true);
                mDialogFragmentRoom.setArguments(args);
                mDialogFragmentRoom.setTargetFragment(this, RESULT_ROOM);
                assert getFragmentManager() != null;
                mDialogFragmentRoom.show(getFragmentManager().beginTransaction(), "MyRoomDialog");
                filterBy("room");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("fermeture dialogu" + requestCode);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //String mLetter = "";
                    assert this.getArguments() != null;
                    Room mRoomFilter = this.getArguments().getParcelable("room");
                   // Bundle bundle = data.getExtras();
                    //String mMonth = bundle.getString("letter", mLetter);
                    System.out.println("oui room");
                    System.out.println("coucou "+mRoomFilter);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    System.out.println("rien room");
                }

            case 2:
                if (resultCode == RESULT_OK) {
                    //String mLetter = "";
                    assert this.getArguments() != null;
                    int mYearFilter = this.getArguments().getInt("YearFilter");
                    int mMonthFilter = this.getArguments().getInt("MonthFilter");
                    int mDayFilter = this.getArguments().getInt("DayFilter");
                    // Bundle bundle = data.getExtras();
                    //String mMonth = bundle.getString("letter", mLetter);
                    System.out.println("coucou "+mYearFilter);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    System.out.println("rien date ");
                }

                break;
        }
    }

    /**
     * réinitialise la liste des réunion en fonction du filtre choisie
     * @param filterName
     */
    private void filterBy(String filterName){
        mMeetings = mMeetingService.AllMeetings();
        try {
            initList(mMeetings, filterName);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Init la Liste des meeting
     * @param mMeetings
     * @param keyMenu filtre sélectionné pour l'ordre d'affichage de la liste(room ou date) sinon vaut rien et affichere suivant l'ordre des ID des réunion
     */
    private void initList(List<Meeting> mMeetings, String keyMenu) throws ParseException {
        List<Meeting> mMeetingFilter = new ArrayList<>(mMeetings);

        if(mMeetingFilter.size()!=0) {
            if(keyMenu.equals("date")){
                mMeetingFilter = mMeetingService.makeGoogOrderDateListMeeting(mMeetingFilter);
            }if(keyMenu.equals("room")){
                mMeetingFilter = mMeetingService.makeGoogOrderRoomListMeeting(mMeetingFilter);
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
            initList(mMeetings, "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
