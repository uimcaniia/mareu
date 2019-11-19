package com.uimainon.mareus.rooteur;

import android.os.Bundle;

import com.uimainon.mareus.R;
import com.uimainon.mareus.base.BaseActivity;
import com.uimainon.mareus.view.ListMeetingFragment;

public class ListMeetingActivity extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_meeting);
        getSupportFragmentManager().beginTransaction().add(R.id.container, ListMeetingFragment.newInstance()).commit();
        getMeetingService();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
