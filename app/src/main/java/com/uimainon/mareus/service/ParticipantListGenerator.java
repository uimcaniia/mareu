package com.uimainon.mareus.service;

import com.uimainon.mareus.model.Participant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ParticipantListGenerator {

    public static List<Participant> PARTICIPANTS_DISPO;
    public static List<Participant> CLONE_DUMMY;


    public static List<Participant> DUMMY_PARTICIPANTS = Arrays.asList(
            new Participant(1,   "caroline@lamzone.com",0),
            new Participant(2,  "Jack@lamzone.com",0),
            new Participant(3,  "Chloé@lamzone.com",0),
            new Participant(4,  "Vincent@lamzone.com",0),
            new Participant(5,  "Elodie@lamzone.com",0),
            new Participant(6,  "Sylvain@lamzone.com",0),
            new Participant(7,  "Laetitia@lamzone.com",0),
            new Participant(8,  "Dan@lamzone.com",0),
            new Participant(9,  "Joseph@lamzone.com",0),
            new Participant(10, "Emma@lamzone.com",0),
            new Participant(11, "Patrick@lamzone.com",0),
            new Participant(12,   "Anais@lamzone.com",0),
            new Participant(13,  "Alfred@lamzone.com",0),
            new Participant(14,  "Souleymane@lamzone.com",0),
            new Participant(15,  "Flo@lamzone.com",0),
            new Participant(16,  "Acesan@lamzone.com",0),
            new Participant(17,   "Meian@lamzone.com",0)

    );

    /**
     * génère la liste des participant disponible
     * @return
     */
    public static List<Participant> generateParticipantsDispo() {
        CLONE_DUMMY = generateAllParticipants();
        PARTICIPANTS_DISPO = new ArrayList<>();
        return PARTICIPANTS_DISPO;
    }

    /**
     * génère la liste des participant disponible
     * @return
     */
    public static List<Participant> generateAllParticipants() {
        return new ArrayList<>(DUMMY_PARTICIPANTS);
    }
}
