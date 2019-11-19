package com.uimainon.mareus.events;

import com.uimainon.mareus.model.Meeting;

public class DeleteMeetingEvent {


    /** Meeting to delete */
    public Meeting meeting;

    /**
     * Constructor.
     * @param  meeting sélectionné
     */
    public DeleteMeetingEvent(Meeting meeting) {
        this.meeting = meeting;
    }
}
