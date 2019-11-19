package com.uimainon.mareus.events;

import com.uimainon.mareus.model.Participant;

public class NotDispoParticipantEvent {


    /**
     * Participant a retirer de la liste des dispo
     */
    public Participant participant;

    /**
     * Constructor.
     * @param participant
     */
    public NotDispoParticipantEvent(Participant participant) {
        this.participant = participant;

    }
}
