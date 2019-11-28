package com.uimainon.mareus;

import com.uimainon.mareus.controlleur.MeetingService;
import com.uimainon.mareus.di.DI;
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.ParticipantsList;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.service.MeetingListGenerator;
import com.uimainon.mareus.service.ParticipantListGenerator;
import com.uimainon.mareus.service.RoomListGenerator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(JUnit4.class)
public class MeetingServiceTest {

    private MeetingService mMeetingService;
    private ParticipantsList mListParticipant = new ParticipantsList();
    private Room mRoom = new Room(1, "Réunion A", "#fed1c8");
    private Meeting mNewMeeting = new Meeting(0, "2019-11-14", 14, 10, "Aucun sujet", mListParticipant, mRoom);


    @Before
    public void setup() {
        mMeetingService = DI.getNewInstanceMeetingService();
    }

    /**récupère la liste de tous les participants avec succès*/
    @Test
    public void getParticipantWithSuccess() {
        List<Participant> part = mMeetingService.AllParticipants();
        List<Participant> partDummy = ParticipantListGenerator.DUMMY_PARTICIPANTS;
        assertThat(part, containsInAnyOrder(Objects.requireNonNull(partDummy.toArray())));
    }
    /**récupère la liste de toutes les Room avec succès*/
    @Test
    public void getRoomWithSuccess() {
        List<Room> room = mMeetingService.AllRooms();
        List<Room> roomDummy = RoomListGenerator.DUMMY_ROOMS;
        assertThat(room, containsInAnyOrder(Objects.requireNonNull(roomDummy.toArray())));
    }
    /**ajoute un Meeting à la liste avec succès*/
    @Test
    public void AddMeetingToListMeetingWithSuccess() {
        mMeetingService.AllMeetings().clear();
        mMeetingService.addMeetingToList(mNewMeeting);
        assertEquals(1, mMeetingService.AllMeetings().size());
        assertEquals(mNewMeeting.getIdMeeting(), mMeetingService.AllMeetings().get(0).getIdMeeting());
    }
    /**supprime un meeting de la liste avec succès*/
    @Test
    public void deleteMeetingWithSuccess(){
        mMeetingService.AllMeetings().clear();
        mMeetingService.addMeetingToList(mNewMeeting);
        List<Meeting> mListMeeting = mMeetingService.AllMeetings();
        mMeetingService.deleteMeeting(mListMeeting.get(0));
        assertEquals(0, mMeetingService.AllMeetings().size());
    }
    /**filtre la liste de Meeting par ROOM*/
    @Test
    public void makeGoogOrderRoomListMeetingWithSuccess(){
        List<Meeting> mListMeetingNoFilter = MeetingListGenerator.DUMMY_INITIAL_MEETING_NO_FILTER;
        List<Meeting> mListMeetingAlreadyFilterByRoom = MeetingListGenerator.DUMMY_MEETING_GOOD_ORDER_ROOM;
        List<Meeting> newListFilter = mMeetingService.makeGoogOrderRoomListMeeting(mListMeetingNoFilter, mRoomSelect);
        Assert.assertEquals(mListMeetingAlreadyFilterByRoom, newListFilter);
    }
    /**filtre la liste de Meeting par Date*/
    @Test
    public void makeGoogOrderDateListMeetingWithSuccess() throws ParseException {
        List<Meeting> mListMeetingNoFilter = MeetingListGenerator.DUMMY_INITIAL_MEETING_NO_FILTER;
        List<Meeting> mListMeetingAlreadyFilterByRoom = MeetingListGenerator.DUMMY_MEETING_GOOD_ORDER_DATE;
        List<Meeting> newListFilter = mMeetingService.makeGoogOrderDateListMeeting(mListMeetingNoFilter, yearSelect, monthSelect, daySelect);
        Assert.assertEquals(mListMeetingAlreadyFilterByRoom, newListFilter);
    }

    /**renvoie la liste des Room disponible (qui ne sont pas déjà utilisé pendant une réunion
     * En selectionnant celles a la meme date et heure que la réunion ajouté*/
    @Test
    public void makeListRoomDispoForThisDate() throws ParseException {
        mMeetingService.AllMeetings().clear();
        List<Room> mListRoom = mMeetingService.getListRoomForThisDate(mNewMeeting.getDate(), mNewMeeting.getHour(), mNewMeeting.getMinute());
        Room roomNotDispo = mListRoom.get(0);
        Room roomDispo = mListRoom.get(1);

        Meeting mNewMeetingToTestRoom = new Meeting(0, "2019-11-14", 14, 10, "Aucun sujet", mListParticipant, roomNotDispo);
        mMeetingService.addMeetingToList(mNewMeetingToTestRoom);
        List<Room> mListRoomReste = mMeetingService.getListRoomForThisDate(mNewMeetingToTestRoom.getDate(), mNewMeetingToTestRoom.getHour(), mNewMeetingToTestRoom.getMinute());

        assertFalse(mListRoomReste.contains(roomNotDispo));
        assertTrue(mListRoomReste.contains(roomDispo));
        assertEquals(mListRoom.size(), mListRoomReste.size(), 1);
    }

    /**renvoie la liste des Participant disponible (qui ne sont pas déjà utilisé pendant une réunion
     * En selectionnant ceux a la meme date et heure que la réunion ajouté*/
    @Test
    public void makeListParticipantDispoForThisDate() throws ParseException {
        mMeetingService.AllMeetings().clear();
        List<Participant> mListParticipantDispo = mMeetingService.getListParticipantForThisDate(mNewMeeting.getDate(), mNewMeeting.getHour(), mNewMeeting.getMinute());
        Participant mPartNotDispo = mListParticipantDispo.get(0);
        Participant mPartDispo = mListParticipantDispo.get(1);

        Meeting mNewMeetingToTestParticipant = new Meeting(0, "2019-11-14", 14, 10, "Aucun sujet", mListParticipant, mRoom);
        mNewMeetingToTestParticipant.getParticipants().add(mPartNotDispo);
        mMeetingService.addMeetingToList(mNewMeetingToTestParticipant);

        List<Participant> mListParticipantDispoReste = mMeetingService.getListParticipantForThisDate(mNewMeetingToTestParticipant.getDate(), mNewMeetingToTestParticipant.getHour(), mNewMeetingToTestParticipant.getMinute());
        assertFalse(mListParticipantDispoReste.contains(mPartNotDispo));
        assertTrue(mListParticipantDispoReste.contains(mPartDispo));
    }

}
