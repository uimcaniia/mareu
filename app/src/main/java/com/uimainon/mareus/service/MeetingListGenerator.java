package com.uimainon.mareus.service;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.ParticipantsList;
import com.uimainon.mareus.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class MeetingListGenerator {

    public static List<Meeting> mListAllMeetings = new ArrayList<Meeting>();

    public static List<Meeting> generateArrMeeting(){
        return new ArrayList<Meeting>();
    }
    public static List<Meeting> DUMMY_INITIAL_MEETING_NO_FILTER = Arrays.asList(
            new Meeting(2,"2019-12-17", 9, 10, "Aucun sujet", new ParticipantsList(), new Room(2, "Réunion B", "#fed1c8")),
            new Meeting(3,"2019-11-13", 10, 10, "Aucun sujet", new ParticipantsList(), new Room(5, "Réunion B", "#b4cf87")),
            new Meeting(5,"2019-11-13", 14, 10, "Aucun sujet", new ParticipantsList(), new Room(1, "Réunion A", "#87c0cf")),
            new Meeting(10,"2019-12-17", 15, 10, "Aucun sujet", new ParticipantsList(), new Room(4, "Réunion A","#7f84cb")),
            new Meeting(11,"2019-12-17", 14, 10, "Aucun sujet", new ParticipantsList(), new Room(3, "Réunion A","#7f84cb")),
            new Meeting(12,"2019-11-13", 14, 10, "Aucun sujet", new ParticipantsList(), new Room(6, "Réunion A", "#be7fcb"))
    );


    public static List<Meeting> DUMMY_MEETING_GOOD_ORDER_ROOM_A = Arrays.asList(
            DUMMY_INITIAL_MEETING_NO_FILTER.get(2),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(4),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(3),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(5)
    );

    public static List<Meeting> DUMMY_MEETING_GOOD_ORDER_ROOM_B = Arrays.asList(
            DUMMY_INITIAL_MEETING_NO_FILTER.get(0),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(1)
    );

    public static List<Meeting> DUMMY_MEETING_GOOD_ORDER_ROOM_C = Collections.emptyList();

    public static List<Meeting> DUMMY_MEETING_GOOD_ORDER_DATE_A = Arrays.asList(
            DUMMY_INITIAL_MEETING_NO_FILTER.get(0),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(4),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(3)
    );

    public static List<Meeting> DUMMY_MEETING_GOOD_ORDER_DATE_B = Arrays.asList(
            DUMMY_INITIAL_MEETING_NO_FILTER.get(5),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(1),
            DUMMY_INITIAL_MEETING_NO_FILTER.get(2)
    );

    public static List<Meeting> DUMMY_MEETING_GOOD_ORDER_DATE_C = Collections.emptyList();

}