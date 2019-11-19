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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListMeetingFragment extends Fragment {

    private List<Meeting> mMeetings;
    private RecyclerView mRecyclerView;
    private TextView mTextViewNothing;
    private MeetingService mMeetingService;
    private Meeting mNewMeeting;
    private List<Participant> mParticipant;
    private Room mRoom;
    private DateService mDate;


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
        switch (id) {
            case R.id.action_filter_date:
                filterBy("date");
                return true;

            case R.id.action_filter_room:
                filterBy("room");
                return true;
        }
        return super.onOptionsItemSelected(item);
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

                int sizeOfAllMeeting = mMeetingService.AllMeetings().size();
                List<Meeting>mListAllMeeting = mMeetingService.AllMeetings();
                int idMeeting = 0;
                if(mListAllMeeting.size() != 0){ // si des réunion existe déjà, vu qu'en cas de suppression, des ID peuvent disparaitre et faire un trou dans la chaîne, on récupe la dernière réunion, et on utilise son ID +1
                    idMeeting =  mListAllMeeting.get(sizeOfAllMeeting-1).getIdMeeting()+1;
                }

                int mYear = mDate.giveYear();
                int mMonth = mDate.giveMonth();
                int mDay = mDate.giveDay();
                int mHour = mDate.giveHour();
                int mMinute = mDate.giveMinute()+5;//(petite marge pour le temps de création et enregistrement')

                Date today = mDate.formatDateToCompare(mYear, mMonth, mDay);
                int goodHourInFrance = 0;
                try { //donne la bonne heure en France (heure d'été et hivers
                    goodHourInFrance = mDate.giveTheGoodHourInFrance(mYear ,mHour, today);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String sDateToday = mDate.getStringDate(mDate.addCalendarToDate(today, 0));
                List<Room> mRooms = mMeetingService.getListRoomForThisDate(sDateToday, goodHourInFrance, mMinute);

                if(mRooms.size()==0) { // si aucune Room de dispo, on cherche le prochaine horaire
                    mMinute = 0; // on remet les minutes à zéro
                    int resultHour = mMeetingService.searchIfRoomDispoForOtherTime(sDateToday, goodHourInFrance, mMinute); // retourne une heure dispo (0 si rien dans la journée dans aucune room)
                    if(resultHour != 0){ // si une room au moins est disponible avec ce nouvel horaire
                        mRooms = mMeetingService.getListRoomForThisDate(sDateToday, resultHour, mMinute);
                        mNewMeeting = new Meeting(idMeeting, sDateToday, resultHour, mMinute, "Aucun sujet", new ParticipantsList(), mRooms.get(0));
                    }
                    if(resultHour == 0){ // si aucune heure de dispo dans aucune salle dans la même journée, alors on change de jour (+1)
                        Calendar oneDayAfter;
                        for (int i = 1 ; i < 365 ; i++){ //on boucle sur 1 an au max
                            oneDayAfter = mDate.addCalendarToDate(today, i); // Nouveau calendrier, on avance d'un jour
                            sDateToday = mDate.getStringDate(oneDayAfter);
                            goodHourInFrance = mDate.giveHourStartDay(); // on remet l'heure de début d'une journée (définit dans la classe DateService)
                            mRooms = mMeetingService.getListRoomForThisDate(sDateToday, mDate.giveHourStartDay(), mMinute);
                            if(mRooms.size()!=0){ // si dans cette nouvelle journée, une salle est au moins dispo, on stoppe la boucle
                                break;
                            }else{ // sinon on cherche sur un autre crénaux horaire de cette nouvelle journée
                                resultHour = mMeetingService.searchIfRoomDispoForOtherTime(sDateToday, 0, mMinute); // on remet l'heure à zéro pour repartir sur l'heure de départ d'une journée
                                if(resultHour != 0){ // si dans ce nouveau crénau horaire de cette nouvelle journée, une salle est au moins dispo, on stoppe la boucle
                                    goodHourInFrance = resultHour;
                                    mRooms = mMeetingService.getListRoomForThisDate(sDateToday, goodHourInFrance, mMinute);
                                    break;
                                }
                            }
                        }
                        if(mRooms.size()==0) { // si malgré tout Il n'y a pas de place
                            System.out.println("ici 1 => ");
                            Toast.makeText(getContext(), "Aucune salle n'est dispo. Aucune réunion ne peut etre crée. Agrandissez les locaux!", Toast.LENGTH_SHORT).show();
                        }else {
                            mRoom = mRooms.get(0);
                            System.out.println("1 => room enregistré => "+mRoom.getName());
                            mNewMeeting = new Meeting(idMeeting, sDateToday, goodHourInFrance, mMinute, "Aucun sujet", new ParticipantsList(), mRoom);
                        }
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
