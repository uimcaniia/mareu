package com.uimainon.mareus.controlleur;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.Room;

import java.util.Date;
import java.util.List;

public interface MeetingApiService {


    /**renvoie la liste des réunions*/
    List<Meeting> AllMeetings();

    /** Supprime une réunion */
    void deleteMeeting(Meeting meeting);

    /** Ajoute une réunion */
    void addMeetingToList(Meeting meeting);

    List<Participant> getListParticipantForThisDate(String date, int hour, int minute);

    List<Room> getListRoomForThisDate(String date, int hour, int minute);

    List<Room> getListAllRooms();
    List<Participant> getListAllParticipant();

    Boolean IsThatPossibleParticipant(Meeting meetingToModif);

    Boolean IsThatPossibleRoom(Meeting meetingToModif);

}
