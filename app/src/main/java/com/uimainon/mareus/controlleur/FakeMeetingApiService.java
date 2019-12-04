package com.uimainon.mareus.controlleur;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.service.MeetingListGenerator;
import com.uimainon.mareus.service.ParticipantListApiService;
import com.uimainon.mareus.service.RoomListApiService;

import java.util.List;

public class FakeMeetingApiService implements MeetingApiService {

    private final List<Meeting> mListAllMeetings;
    private final RoomListApiService rommService;
    private final ParticipantListApiService participantService;

    public FakeMeetingApiService() {
        mListAllMeetings = MeetingListGenerator.generateArrMeeting();
        rommService = new RoomListApiService();
        participantService = new ParticipantListApiService();
    }

    /** renvoie la liste des réunions  qui s'affichera dans le recyclerView */
    @Override
    public List<Meeting> AllMeetings() {
        return mListAllMeetings;
    }

    /** renvoie la liste de TOUT les Room*/
    @Override
    public List<Room> getListAllRooms() {
        return rommService.getListAllRooms();
    }
    /** renvoie la liste de TOUT les Participants*/
    @Override
    public List<Participant> getListAllParticipant(){
        return participantService.getListAllParticipant();
    }

    /** Supprime une réunion */
    @Override
    public void deleteMeeting(Meeting meeting) { mListAllMeetings.remove(meeting); }

    /** Ajoute une réunion à la liste des réunions */
    @Override
    public void addMeetingToList(Meeting meeting) {
        mListAllMeetings.add(meeting);
    }

    /** TRouve la liste des participants disponible à la date et aux horaires souhaités */
    @Override
    public List<Participant> getListParticipantForThisDate(String date, int hour, int minute){
        return participantService.getListParticipantForThisDate(mListAllMeetings, date, hour, minute);
    }

    /** TRouve la liste des Room disponible à la date et aux horaires souhaités */
    @Override
    public List<Room> getListRoomForThisDate(String date, int hour, int minute){
        return rommService.getListRoomDispo(mListAllMeetings, date, hour, minute);
    }

     /** vérifie si les participants choisie sont effectivement disponible à la date et aux horaire choisir dans la réunion*/
    @Override
    public Boolean IsThatPossibleParticipant(Meeting meeting) {
        return participantService.IsThatPossibleParticipant(mListAllMeetings, meeting);
    }

    /** vérifie si la Room choisie est effectivement disponible à la date et aux horaire choisir dans la réunion*/
    @Override
    public Boolean IsThatPossibleRoom(Meeting meeting) {
        return rommService.IsThatPossibleRoom(mListAllMeetings, meeting);
    }

}
