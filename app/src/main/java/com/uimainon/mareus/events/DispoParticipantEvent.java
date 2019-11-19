package com.uimainon.mareus.events;

import com.uimainon.mareus.model.Participant;

public class DispoParticipantEvent {


    /**
     * Participant a ajouter de la liste des dispo
     */
    public Participant participant;

    /**
     * Constructor.
     * @param participant
     */
    public DispoParticipantEvent(Participant participant) {
        this.participant = participant;
    }
}
