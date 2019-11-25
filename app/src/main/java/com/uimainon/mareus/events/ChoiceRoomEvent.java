package com.uimainon.mareus.events;

import com.uimainon.mareus.model.Room;

public class ChoiceRoomEvent {
    /** Meeting to delete */
    public Room room;

    /**
     * Constructor.
     * @param  room sélectionné
     */
    public ChoiceRoomEvent(Room room) {
        this.room = room;
    }
}
