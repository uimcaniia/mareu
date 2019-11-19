package com.uimainon.mareus.controlleur;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.view.NewMeetingPagerAdapter;

public class refreshInfosFragments {

    private Fragment fragDate;
    private Fragment fragParticipant;
    private Fragment fragRoom;

    public refreshInfosFragments(NewMeetingPagerAdapter mPagerAdapter) {
        this.fragDate = mPagerAdapter.getItem(0);
        this.fragParticipant = mPagerAdapter.getItem(1);
        this.fragRoom = mPagerAdapter.getItem(2);
    }

    private Bundle giveBundleArgements( Meeting mNewMeeting){
        Bundle args = new Bundle();
        args.putParcelable("meeting", mNewMeeting);
        return args;
    }



    public void detachAttachFragment(Fragment frag, FragmentTransaction ft){
        //isAdded = Renvoie true si le fragment est actuellement ajouté à son activité. Cela n'ajoutera pas ce fragment s'il est déjà ajouté
        if(frag.isAdded()) {
           // System.out.println("refresh bundle oui");
            ft.detach(frag);
            ft.attach(frag);

       }else{
            //System.out.println("refresh bundle non");
            ft.attach(frag);
        }
        //System.out.println("refresh bundle =>  "+frag);
    }

    /**
     * rafraichit les infos des fragments
     * @param ft
     * @param mNewMeeting
     */
    public void refreshFragments(FragmentTransaction ft, Meeting mNewMeeting) {
        Bundle args = giveBundleArgements(mNewMeeting);

        fragRoom.setArguments(args);
        fragDate.setArguments(args);
        fragParticipant.setArguments(args);

        detachAttachFragment(fragDate, ft);
        detachAttachFragment(fragRoom, ft);
        detachAttachFragment(fragParticipant, ft);

        ft.commit();
    }

}
