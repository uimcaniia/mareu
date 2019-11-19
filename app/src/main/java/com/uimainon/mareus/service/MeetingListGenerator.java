package com.uimainon.mareus.service;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.ParticipantsList;
import com.uimainon.mareus.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MeetingListGenerator {

    public static List<Meeting> mListAllMeetings = new ArrayList<Meeting>();

    public static List<Meeting> generateArrMeeting(){
        return mListAllMeetings;
    }
    public static List<Meeting> DUMMY_INITIAL_MEETING_NO_FILTER = Arrays.asList(
            new Meeting(2,"17-12-2019", 9, 10, "Aucun sujet", new ParticipantsList(), new Room(2, "Réunion B", "#fed1c8")),
            new Meeting(3,"13-11-2019", 10, 10, "Aucun sujet", new ParticipantsList(), new Room(5, "Réunion E", "#b4cf87")),
            new Meeting(5,"13-11-2019", 14, 10, "Aucun sujet", new ParticipantsList(), new Room(1, "Réunion A", "#87c0cf")),
            new Meeting(10,"17-12-2019", 15, 10, "Aucun sujet", new ParticipantsList(), new Room(4, "Réunion D","#7f84cb")),
            new Meeting(11,"17-12-2019", 14, 10, "Aucun sujet", new ParticipantsList(), new Room(3, "Réunion C","#7f84cb")),
            new Meeting(12,"12-11-2019", 14, 10, "Aucun sujet", new ParticipantsList(), new Room(6, "Réunion F", "#be7fcb"))
    );


    public static List<Meeting> DUMMY_MEETING_GOOD_ORDER_ROOM = Arrays.asList(
            DUMMY_INITIAL_MEETING_NO_FILTER.get(2),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(0),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(4),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(3),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(1),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(5)
    );

    public static List<Meeting> DUMMY_MEETING_GOOD_ORDER_DATE = Arrays.asList(
            DUMMY_INITIAL_MEETING_NO_FILTER.get(5),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(1),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(2),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(0),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(4),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(3)

    );

}