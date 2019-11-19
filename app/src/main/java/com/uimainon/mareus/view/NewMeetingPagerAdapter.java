package com.uimainon.mareus.view;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class NewMeetingPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList fragments;

    public NewMeetingPagerAdapter(FragmentManager fm, ArrayList fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return (Fragment) fragments.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void instantiateItem(int containerNewMeeting) {
    }

}
