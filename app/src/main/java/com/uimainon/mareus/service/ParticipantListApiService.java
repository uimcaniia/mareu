package com.uimainon.mareus.service;

import androidx.collection.ArraySet;

import com.uimainon.mareus.controlleur.MeetingService;
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ParticipantListApiService {

    private List<Participant> mListAllParticipants = ParticipantListGenerator.generateAllParticipants();
    private DateService mDateService = new DateService();

    /**renvoie la liste de tous les participant de la boite*/

    public List<Participant> getListAllParticipant(){
        return mListAllParticipants;
    }

    /**
     * TRouve la liste des participants disponible à la date et aux horaires souhaités
     *  on retire ceux déjà en réunion avant et après - de 70 min avant et après l'heure voulue
     * @param mListAllMeetingsCreate récupère toutes les réunions
     * @param date
     * @param hour
     * @param minute
     */

    public List<Participant> getListParticipantForThisDate(List<Meeting> mListAllMeetingsCreate, String date, int hour, int minute){
        Set<Participant> mListParticipantNotDispo = new ArraySet<>(); //=> contiendra les participant disponible
        List<Participant> mListOfAllParticipants = getListAllParticipant(); // => contient tous les participants de la boite
        List<Participant> mListParticipantDispo = new ArrayList<>(mListOfAllParticipants); /// contiendra les participants déjà en réunion

        if(mListAllMeetingsCreate.size() == 0){
            return mListOfAllParticipants;
        }else{
            List<Meeting> mListMeetingForThisDate = getListMeetingForThisDate(mListAllMeetingsCreate, date);
            if(mListMeetingForThisDate.size()==0){ // si Aucune réunion à la même date, on renvoie toute la liste des Rooms
                return mListOfAllParticipants;
            }else {
                int sizeRestant = mListMeetingForThisDate.size();
                for (int i = 0; i < sizeRestant; i++) {
                    int timeDiff = mDateService.getTimeBetweenTwoHours(mListMeetingForThisDate.get(i).getHour(), mListMeetingForThisDate.get(i).getMinute(), hour, minute);
                    if (timeDiff < 70) { // si l'une des réunions existante est sur le temps souhaité, on ajoute les participants dans une autre liste (vu qu'il ne seront pas dispo)
                        int sizeListParticipantMeeting = mListMeetingForThisDate.get(i).getParticipants().size();
                        for (int x = 0; x < sizeListParticipantMeeting; x++) {
                            Participant part = mListMeetingForThisDate.get(i).getParticipants().get(x);
                            mListParticipantNotDispo.add(part);
                        }
                    }
                }
                // on supprime de la liste globale, les participant pas dispo qui s'y trouve
                for (Participant x : mListParticipantNotDispo) {
                    if (mListParticipantDispo.contains(x)) {
                        mListParticipantDispo.remove(x);
                    }
                }
                return mListParticipantDispo;
            }
        }
    }

    public Boolean IsThatPossibleParticipant(List<Meeting> mListAllMeetingsCreate, Meeting meetingToVerif){
        Boolean b = true;
        if(mListAllMeetingsCreate.size() == 0){
            return b;
        }else{
            int size = mListAllMeetingsCreate.size();// on récupère les réunions à la même date pour ensuite vérifier si le créneau est disponible
            List<Meeting> mListMeetingForThisDate = getListMeetingForThisDate(mListAllMeetingsCreate, meetingToVerif.getDate());

            if(mListMeetingForThisDate.size()!=0) { // si il y a des réunion à la même date
                int sizeRestant = mListMeetingForThisDate.size();
                for (int i = 0; i < sizeRestant; i++) {
                    if (mListMeetingForThisDate.get(i).getIdMeeting() != meetingToVerif.getIdMeeting()) { // si c'est pas la réunion a vérifier, mais qu'elle est dans la même journée, on vérif si l'heure va poser problème
                        int timeDiff = mDateService.getTimeBetweenTwoHours(mListMeetingForThisDate.get(i).getHour(), mListMeetingForThisDate.get(i).getMinute(), meetingToVerif.getHour(), meetingToVerif.getMinute());
                        if (timeDiff < 70) { // si l'une des réunions existante est sur le temps de celle a vérifier, on vérifie si des participant sont en commun
                            int sizeListParticipantMeeting = mListMeetingForThisDate.get(i).getParticipants().size();
                            for (int x = 0; x < sizeListParticipantMeeting; x++) {
                                int sizeParticipantToVerif = meetingToVerif.getParticipants().size();
                                for (int z = 0; z < sizeParticipantToVerif; z++) { // si un des participants est commun
                                    if (mListMeetingForThisDate.get(i).getParticipants().get(x).getId() == meetingToVerif.getParticipants().get(z).getId()) {
                                        b = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return b;
        }
    }

    /** compare et retourne une liste de réunion suivant la date en paramètre */
    private List<Meeting> getListMeetingForThisDate(List<Meeting> meetingList, String date){
        List<Meeting> mListMeetingForThisDate = new ArrayList<>();
        int size = meetingList.size(); // on récup le nombre de réunion
        for (int i = 0 ; i < size ; i++) {// on récupère les réunions à la même date
            if (meetingList.get(i).getDate().equals(date)) {
                mListMeetingForThisDate.add(meetingList.get(i));
            }
        }
        return mListMeetingForThisDate;
    }
}
