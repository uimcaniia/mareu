package com.uimainon.mareus.rooteur;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TimePicker;

import com.google.android.material.tabs.TabLayout;
import com.uimainon.mareus.R;

import com.uimainon.mareus.base.BaseActivity;
import com.uimainon.mareus.controlleur.refreshInfosFragments;
import com.uimainon.mareus.controlleur.OnHeadlineSelectedListener;
import com.uimainon.mareus.di.DI;
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.ParticipantsList;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.controlleur.MeetingService;
import com.uimainon.mareus.view.NewMeetingDateHourFragment;
import com.uimainon.mareus.view.NewMeetingParticipantFragment;
import com.uimainon.mareus.view.NewMeetingRoomFragment;
import com.uimainon.mareus.view.NewMeetingPagerAdapter;


import java.util.ArrayList;
import java.util.List;

public class AddMeetingActivity extends BaseActivity implements NewMeetingDateHourFragment.OnHeadlineSelectedListener, NewMeetingParticipantFragment.OnHeadlineSelectedListener, NewMeetingRoomFragment.OnHeadlineSelectedListener{//OnHeadlineSelectedListener {

    NewMeetingPagerAdapter mPagerAdapter;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    Fragment fragDate;
    Fragment fragParticipant;
    Fragment fragRoom;
    protected ArrayList<Fragment> aFragments;
    private MeetingService mMeetingService;
    private Meeting mNewOrModifMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);
        mTabLayout = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.containerNewMeeting);
       // detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout_detail);

        //A - We only add DetailFragment in Tablet mode (If found frame_layout_detail)


        mMeetingService = DI.getMeetingService();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        mNewOrModifMeeting = bundle.getParcelable("receiveMeeting");
        //On récupère la liste des meeting pour vérifier et si besoin, récupérer le meeting déjà créé et à modifier
        List<Meeting>mListMeeting = mMeetingService.AllMeetings();

        if (mListMeeting.size()!=0){
            int sizeList = mListMeeting.size();
            for(int i=0 ; i<sizeList ; i++){
                if(mListMeeting.get(i).getIdMeeting()==mNewOrModifMeeting.getIdMeeting()){
                    // Attention!! le chemin référenciel n'est plus le même une fois transféré avec bundle entre les 2 activités!!
                    mNewOrModifMeeting = mListMeeting.get(i);// On copie les données de l'object de la liste vers son clone afin de lui redonner son chemin référenciel initial
                }
            }
        }
        Bundle argsMeeting = new Bundle();
        argsMeeting.putParcelable("meeting", mNewOrModifMeeting);


        aFragments = new ArrayList<>();
        fragDate = NewMeetingDateHourFragment.newInstance();
        fragParticipant = NewMeetingParticipantFragment.newInstance();
        fragRoom = NewMeetingRoomFragment.newInstance();

        fragDate.setArguments(argsMeeting);
        fragParticipant.setArguments(argsMeeting);
        fragRoom.setArguments(argsMeeting);

        aFragments.add(fragDate);
        aFragments.add(fragParticipant);
        aFragments.add(fragRoom);
        //---------------------------------------------------------------------------
        mPagerAdapter = new NewMeetingPagerAdapter(getSupportFragmentManager(), aFragments);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof NewMeetingDateHourFragment) {
            NewMeetingDateHourFragment mFragDateHour = (NewMeetingDateHourFragment) fragment;
            mFragDateHour.setOnHeadlineSelectedListener(this);
        }
        if (fragment instanceof NewMeetingParticipantFragment) {
            NewMeetingParticipantFragment mFragParticipant = (NewMeetingParticipantFragment) fragment;
            mFragParticipant.setOnHeadlineSelectedListener(this);
        }
        if (fragment instanceof NewMeetingRoomFragment) {
            NewMeetingRoomFragment mFragRoom = (NewMeetingRoomFragment) fragment;
            mFragRoom.setOnHeadlineSelectedListener(this);
        }
    }

    //******************************************************************************************************
    /**
     * rafraichit les infos des fragment suite au changement d'heure dans le fragment 0 et l'inscrit dans l'object meeting en cour
     * @param view
     * @param hour
     * @param minute
     */
    @Override
    public void callTimeChanged(TimePicker view, int hour, int minute) {
        Fragment mFragPart = mPagerAdapter.getItem(1);
        Fragment mFragRoom = mPagerAdapter.getItem(2);

        mNewOrModifMeeting.setHour(hour);
        mNewOrModifMeeting.setMinute(minute);
        Bundle args = new Bundle();
        args.putParcelable("meeting", mNewOrModifMeeting);

        FragmentTransaction transactionPart = getSupportFragmentManager().beginTransaction();
        mFragPart.setArguments(args);
        if(mFragPart.isAdded()) {
            transactionPart.detach(mFragPart);
            transactionPart.attach(mFragPart);
        }
        transactionPart.commit();

        FragmentTransaction transactionRoom = getSupportFragmentManager().beginTransaction();
        mFragRoom.setArguments(args);
        if(mFragRoom.isAdded()) {
            transactionRoom.detach(mFragRoom);
            transactionRoom.attach(mFragRoom);
        }
        transactionRoom.commit();
    }
    //******************************************************************************************************
    /**
     * rafraichit les infos des fragments suite au changement de date dans le fragment 0 et l'inscrit dans l'object meeting en cour
     * @param view
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void callDateChanged(CalendarView view, int year, int month, int day) {
        Fragment mFragPart = mPagerAdapter.getItem(1);
        Fragment mFragRoom = mPagerAdapter.getItem(2);

        String putZeroMonth = "";
        String putZeroDay = "";

        if(month<10){
            putZeroMonth = "0";
        }
        if(day<10){
            putZeroDay = "0";
        }
        String newDate = year+"-"+putZeroMonth+month+"-"+putZeroDay+day;

        mNewOrModifMeeting.setDate(newDate);
        Bundle args = new Bundle();
        args.putParcelable("meeting", mNewOrModifMeeting);

        FragmentTransaction transactionPart = getSupportFragmentManager().beginTransaction();
        mFragPart.setArguments(args);
        if(mFragPart.isAdded()) {
            transactionPart.detach(mFragPart);
            transactionPart.attach(mFragPart);
        }
        transactionPart.commit();

        FragmentTransaction transactionRoom = getSupportFragmentManager().beginTransaction();
        mFragRoom.setArguments(args);
        if(mFragRoom.isAdded()) {
            transactionRoom.detach(mFragRoom);
            transactionRoom.attach(mFragRoom);
        }
        transactionRoom.commit();
    }
    //******************************************************************************************************
    /**
     * rafraichit les infos des fragments suite au changement de participant dans le fragment 1 et l'inscrit dans l'object meeting en cour
     * 1 => plus dispo (on l'ajoute dans la liste)
     * 0 => dispo (on le retire de la liste
     * @param participants
     */
    @Override
    public void callParticipantChanged(Participant participants, int b) {
        Fragment mFragHour = mPagerAdapter.getItem(0);
        Fragment mFragRoom = mPagerAdapter.getItem(2);

        if(b == 1){
            Participant addParticipant = participants;
            addParticipant.setDispo(1);
            mNewOrModifMeeting.getParticipants().add(addParticipant);
        }else{

            Participant addParticipant = participants;
            addParticipant.setDispo(0);
            mNewOrModifMeeting.getParticipants().remove(addParticipant);
        }
        Bundle args = new Bundle();
        args.putParcelable("meeting", mNewOrModifMeeting);

        FragmentTransaction transactionHour = getSupportFragmentManager().beginTransaction();
        mFragHour.setArguments(args);
        if(mFragHour.isAdded()) {
            transactionHour.detach(mFragHour);
            transactionHour.attach(mFragHour);
        }
        transactionHour.commit();

        FragmentTransaction transactionRoom = getSupportFragmentManager().beginTransaction();
        mFragRoom.setArguments(args);
        if(mFragRoom.isAdded()) {
            transactionRoom.detach(mFragRoom);
            transactionRoom.attach(mFragRoom);
        }
        transactionRoom.commit();
    }

    /**
     * Récupère la salle de réunion sélectionné et l'inscrit dans l'object meeting en cour
     * @param mRoom

     */
    @Override
    public void callRoomChanged(Room mRoom) {
        Fragment mFragHour = mPagerAdapter.getItem(0);
        Fragment mFragPart = mPagerAdapter.getItem(1);

        mNewOrModifMeeting.setRoom(mRoom);
        Bundle args = new Bundle();
        args.putParcelable("meeting", mNewOrModifMeeting);

        FragmentTransaction transactionPart = getSupportFragmentManager().beginTransaction();
        mFragPart.setArguments(args);
        if(mFragPart.isAdded()) {
            transactionPart.detach(mFragPart);
            transactionPart.attach(mFragPart);
        }
        transactionPart.commit();

        FragmentTransaction transactionHour = getSupportFragmentManager().beginTransaction();
        mFragHour.setArguments(args);
        if(mFragHour.isAdded()) {
            transactionHour.detach(mFragHour);
            transactionHour.attach(mFragHour);
        }
        transactionHour.commit();
    }

    /** Valide la nouvelle réunion */
    @Override
    public void callValidNewActivity() {
        finish();
    }
}
