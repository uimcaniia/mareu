package com.uimainon.mareus.rooteur;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.uimainon.mareus.R;
import com.uimainon.mareus.base.BaseActivity;
import com.uimainon.mareus.view.ListMeetingFragment;

public class ListMeetingActivity extends BaseActivity{//} implements ListMeetingFragment.OnHeadlineSelectedListener{

    private Dialog mDialogRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_meeting);

        //mDialogRoom = rootView.findViewById(R.id.)
        getSupportFragmentManager().beginTransaction().add(R.id.container, ListMeetingFragment.newInstance()).commit();
        getMeetingService();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

/*    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ListMeetingFragment) {
            ListMeetingFragment mFragDateHour = (ListMeetingFragment) fragment;
            mFragDateHour.setOnHeadlineSelectedListener(this);
        }

    }*/

/*    @Override
    public void callDialogRoom() {

    }*/
}
