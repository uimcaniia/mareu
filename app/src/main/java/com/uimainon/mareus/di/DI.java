package com.uimainon.mareus.di;

import com.uimainon.mareus.controlleur.FakeMeetingApiService;
import com.uimainon.mareus.controlleur.MeetingApiService;
import com.uimainon.mareus.controlleur.MeetingService;

public class DI {

    private static MeetingService service;


    public static MeetingService getMeetingService(){
        service.setMeetingService(new FakeMeetingApiService());
        return service;
    }

    /**
     * Get an instance on @{@link MeetingApiService}
     * @return
     */
    public static MeetingService getNewInstanceMeetingService() {
        service = new MeetingService(new FakeMeetingApiService());
        return service;
    }
}
