package com.uimainon.mareus.base;

import androidx.appcompat.app.AppCompatActivity;

import com.uimainon.mareus.di.DI;
import com.uimainon.mareus.controlleur.MeetingService;

public abstract class BaseActivity extends AppCompatActivity {

    private MeetingService mMeetingService;

    public MeetingService getMeetingService() {
       // mMeetingService = DI.getNewInstanceMeetingService();
        if (mMeetingService == null) mMeetingService = DI.getNewInstanceMeetingService();
        return mMeetingService;
    }
}
