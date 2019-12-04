package com.uimainon.mareus.controlleur;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.Room;

import java.util.List;

public interface MeetingApiService {


    /**renvoie la liste des réunions*/
    List<Meeting> AllMeetings();

    /** renvoie la liste de TOUT les Room*/
    List<Room> getListAllRooms();

    /** renvoie la liste de TOUT les Participants*/
    List<Participant> getListAllParticipant();

    /** Supprime une réunion */
    void deleteMeeting(Meeting meeting);

    /** Ajoute une réunion */
    void addMeetingToList(Meeting meeting);

    /** TRouve la liste des participants disponible à la date et aux horaires souhaités */
    List<Participant> getListParticipantForThisDate(String date, int hour, int minute);

    /** TRouve la liste des Room disponible à la date et aux horaires souhaités */
    List<Room> getListRoomForThisDate(String date, int hour, int minute);

    /** vérifie si les participants choisie sont effectivement disponible à la date et aux horaire choisir dans la réunion*/
    Boolean IsThatPossibleParticipant(Meeting meetingToModif);

    /** vérifie si la Room choisie est effectivement disponible à la date et aux horaire choisir dans la réunion*/
    Boolean IsThatPossibleRoom(Meeting meetingToModif);

}
