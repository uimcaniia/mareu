package com.uimainon.mareus.controlleur;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.service.MeetingListGenerator;
import com.uimainon.mareus.service.ParticipantListApiService;
import com.uimainon.mareus.service.RoomListApiService;

import java.util.List;

public class FakeMeetingApiService implements MeetingApiService {

    private List<Meeting> mListAllMeetings = MeetingListGenerator.generateArrMeeting();
    private RoomListApiService rommService = new RoomListApiService();
    private ParticipantListApiService participantService = new ParticipantListApiService();


    /** renvoie la liste des réunions  qui s'affichera dans le recyclerView */
    @Override
    public List<Meeting> AllMeetings() {
        return mListAllMeetings;
    }

    @Override
    public List<Room> getListAllRooms() {
        return rommService.getListAllRooms();
    }
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

    /**
     * TRouve la liste des participants disponible à la date et aux horaires souhaités
     * on retire ceux déjà en réunion avant et après - de 70 min avant et après l'heure voulue
     * @param date
     * @param hour
     * @param minute
     * @return
     */
    @Override
    public List<Participant> getListParticipantForThisDate(String date, int hour, int minute, Meeting meeting){
        mListAllMeetings = AllMeetings();
        return participantService.getListParticipantForThisDate(mListAllMeetings, date, hour, minute, meeting);
    }

    /**
     * TRouve la liste des Room disponible à la date et aux horaires souhaités
     * on retire celles déjà réservées réunion avant et après - de 70 min avant et après l'heure voulue
     */
    @Override
    public List<Room> getListRoomForThisDate(String date, int hour, int minute){
        mListAllMeetings = AllMeetings();
        return rommService.getListRoomDispo(mListAllMeetings, date, hour, minute);
    }

    @Override
    public Boolean IsThatPossibleParticipant(Meeting meeting) {
        mListAllMeetings = AllMeetings();
        return participantService.IsThatPossibleParticipant(mListAllMeetings, meeting);
    }

    @Override
    public Boolean IsThatPossibleRoom(Meeting meeting) {
        mListAllMeetings = AllMeetings();
        return rommService.IsThatPossibleRoom(mListAllMeetings, meeting);
    }

}
