package com.uimainon.mareus;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.ParticipantsList;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.service.DateService;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DateServiceTest {

    private ParticipantsList mListParticipant = new ParticipantsList();
    private Room mRoom = new Room(1, "Réunion A", "#fed1c8");
    private Meeting mBeforeMeeting = new Meeting(0, "2018-11-18", 14, 10, "Aucun sujet", mListParticipant, mRoom);
    private Meeting mFuturMeeting = new Meeting(0, "2022-11-14", 14, 10, "Aucun sujet", mListParticipant, mRoom);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private DateService mDate;

    @Before
    public void setup() {
        mDate = new DateService();
    }

    /**renvoie la bonne heure en france suivant l'heure d'été ou hiver
     * été => 31 mars
     * hivers => 27 octobre
     */
    @Test
    public void giveTheGoodHourInFranceWithSuccess() throws ParseException {
        int hourInWeb = 8;
        Date dateEte = sdf.parse(2019+"-"+10+"-"+26);
        Date dateHivers = sdf.parse(2019+"-"+10+"-"+28);
        int resEte = mDate.giveTheGoodHourInFrance( 2019,  hourInWeb, dateEte);
        int resHivers =mDate.giveTheGoodHourInFrance( 2019,  hourInWeb, dateHivers);
        assertEquals(hourInWeb+2, resEte);
        assertEquals(hourInWeb+1, resHivers);
    }



    /**renvoie la date a partir d'un Calendar au format String*/
    @Test
    public void getStringDateWithSuccess() {
        Date today = mDate.formatDateToCompare(2019, 10, 05);
        String sDateToday = mDate.getStringDate(mDate.addCalendarToDate(today, 0));
        assertEquals("2019-09-05", sDateToday);
    }

    /**renvoie la date au format français ( JJ - MM - AAAA)*/
    @Test
    public void getGoodFormatFrenchDateWithSuccess() {
        String dateFr =  mDate.getGoodFormatFrenchDate( "2019-09-05");
        assertEquals("05-10-2019", dateFr);
    }

    /**vérifie si la date est déjà passé ou non par rapport à la date actuelle de la demande*/
    @Test
    public void isThatPossibleDateWithSuccess() throws ParseException {
        int dateBefore = mDate.isThatPossibleDate(mBeforeMeeting);
        int dateFutur = mDate.isThatPossibleDate(mFuturMeeting);

        assertEquals(-1, dateBefore);
        assertEquals(1, dateFutur);
    }

    /**compare deux horaire et renvoie un int pour savoir si l'heure est avant, après ou en même temps que l'autre*/
    @Test
    public void compareTwoHourMinuteWithSuccess() {
        List<Integer> firstTime = Arrays.asList(10, 55);
        List<Integer>secondTime = Arrays.asList(21, 05);
        int hourBefore = mDate.compareTwoHourMinute(firstTime.get(0), firstTime.get(1), secondTime.get(0), secondTime.get(1));
        int hourFutur = mDate.compareTwoHourMinute(secondTime.get(0), secondTime.get(1), firstTime.get(0), firstTime.get(1));
        int sameHour = mDate.compareTwoHourMinute(firstTime.get(0), firstTime.get(1), firstTime.get(0), firstTime.get(1));
        assertEquals(-1, hourBefore);
        assertEquals(1, hourFutur);
        assertEquals(0, sameHour);
    }

    /**renvoie la date en String a partir des 3 int d'un calendarView*/
    @Test
    public void getStringDateWithIntNumberWithSuccess(){
       String DateWithInt = mDate.getStringDateWithIntNumber(2020, 12, 23);
       assertEquals("2020-12-23", DateWithInt);
        String DateWithIntZeroMustAdd = mDate.getStringDateWithIntNumber(2020, 1, 2);
        assertEquals("2020-01-02", DateWithIntZeroMustAdd);
    }
}
