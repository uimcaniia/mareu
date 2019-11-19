package com.uimainon.mareus.service;

import com.uimainon.mareus.model.Meeting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateService {

    private Calendar mCalendar = Calendar.getInstance();

    private int mYear = mCalendar.get(Calendar.YEAR);
    private int mMonth = mCalendar.get(Calendar.MONTH)+1;
    private int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

    private int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
    private int mMinute = mCalendar.get(Calendar.MINUTE);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public int giveYear(){
        return mYear;
    }
    public int giveMonth(){
        return mMonth;
    }
    public int giveDay(){
        return mDay;
    }
    public int giveHour(){
        return mHour;
    }
    public int giveMinute(){
        return mMinute;
    }

    public int giveHourStartDay(){
        return 8;
    }
    public int giveHourEndDay(){
        return 17;
    }



    public int giveTheGoodHourInFrance(int year, int hour, Date todayToCompareWithSaison) throws ParseException {

        Date eteInYear = sdf.parse(year+"-03-31");
        Date hiversInYear = sdf.parse(year+"-10-27");
        Date eteAfterYear = sdf.parse((year+1)+"-03-31");

        int giveGoodHour = hour;

        int changeHourEte =  todayToCompareWithSaison.compareTo(eteInYear);
        int changeHourHiver =  todayToCompareWithSaison.compareTo(hiversInYear);
        int changeHourAfter =  todayToCompareWithSaison.compareTo(eteAfterYear);

        if((changeHourEte == 0)||(changeHourEte > 0)&&(changeHourHiver < 0)){
            giveGoodHour = hour+2;
        }
        if((changeHourHiver == 0)||(changeHourHiver > 0)&&(changeHourAfter < 0)){
            giveGoodHour = hour+1;
        }
       // System.out.println(giveGoodHour);
        return giveGoodHour;
    }

    public Date formatDateToCompare(int year, int month, int day){
        Date date = null;
        try {
            date = sdf.parse(year+"-"+month+"-"+day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public Calendar addCalendarToDate(Date date, int nbDays){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, nbDays);
        return cal;
    }

    public String getStringDate(Calendar otherDate) {
        int year = otherDate.get(Calendar.YEAR);
        int month = otherDate.get(Calendar.MONTH);
        int day = otherDate.get(Calendar.DAY_OF_MONTH);

        String putZeroMonth = "";
        String putZeroDay = "";

        if(month<10){
            putZeroMonth = "0";
        }
        if(day<10){
            putZeroDay = "0";
        }
        return year +"-"+putZeroMonth+month+"-"+putZeroDay+day;
    }

    public String getGoodFormatFrenchDate(String dateUs){
        String[] splitArray = dateUs.split("-");
        String putZeroMonth = "";
        String putZeroDay = "";

        int mYear = Integer.parseInt(splitArray[0]);
        int mMonth = Integer.parseInt(splitArray[1])+1;
        int mDay = Integer.parseInt(splitArray[2]);

        if(mMonth<10){
            putZeroMonth = "0";
        }
        if(mDay<10){
            putZeroDay = "0";
        }

        return putZeroDay+mDay +"-"+putZeroMonth+mMonth+"-"+mYear;
    }

    public int isThatPossibleDate(Meeting mMeeting) throws ParseException {
        Calendar mNewCalendar = Calendar.getInstance();

        int year = mNewCalendar.get(Calendar.YEAR);
        int month = mNewCalendar.get(Calendar.MONTH)+1;
        int day = mNewCalendar.get(Calendar.DAY_OF_MONTH);

        String[] splitArray = mMeeting.getDate().split("-");
        int meetingYear = Integer.parseInt(splitArray[0]);
        int meetingMonth = Integer.parseInt(splitArray[1])+1;
        int meetingDay = Integer.parseInt(splitArray[2]);

        Date dDateMeeting = sdf.parse(meetingYear+"-"+meetingMonth+"-"+meetingDay);
        Date today = sdf.parse(year+"-"+month+"-"+day);

        return dDateMeeting.compareTo(today);
    }

    public int compareTwoDate(String firstDate, String secondDate) throws ParseException {
        Date first = sdf.parse(firstDate);
        Date second = sdf.parse(secondDate);
        return first.compareTo(second);
    }

    public int compareTwoHourMinute(int firstHour, int firstMinute,  int secondHour, int secondMinute){
        int timeFirst = firstHour*60 + firstMinute;
        int timeSecond = secondHour*60 + secondMinute;
        int result = 0;
        if(timeFirst > timeSecond){
            result = 1;
        }
        if(timeFirst < timeSecond){
            result = -1;
        }
        return result;
    }

    public int getTimeBetweenTwoHours(int firstHour, int firstMinute,  int secondHour, int secondMinute){
        int firstTime = firstHour * 60 + firstMinute;
        int secondTime = secondHour * 60 + secondMinute;
        int timeDiff = 0;
        if (firstTime >= secondTime) {
            timeDiff = firstTime - secondTime;
        } else {
            timeDiff = secondTime - firstTime;
        }
        return timeDiff;
    }

    public Boolean verifIfTimeIsPossible(Meeting mMeeting) {
        Calendar mNewCalendar = Calendar.getInstance();
        boolean b = true;

        int hour = mNewCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mNewCalendar.get(Calendar.MINUTE);

        int year = mNewCalendar.get(Calendar.YEAR);
        int month = mNewCalendar.get(Calendar.MONTH)+1;
        int day = mNewCalendar.get(Calendar.DAY_OF_MONTH);

        Date today = formatDateToCompare(year, month, day);
        int goodHourInFrance = 0;
        try {
            goodHourInFrance = giveTheGoodHourInFrance(year ,hour, today);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int timeMeeting = mMeeting.getHour() * 60 + mMeeting.getMinute();
        int timeToday = goodHourInFrance * 60 + minute;
      // System.out.println(timeMeeting + "  "+ timeToday+"   ");
        if(timeToday > timeMeeting){
            b = false;
        }
        return b;
    }
}
