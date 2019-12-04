package com.uimainon.mareus.model;

import android.os.Parcel;
import android.os.Parcelable;

/** Object complexe qui passera entre les 2 activités et qui implémente donc Parcelable*/
public class Meeting implements Parcelable {

    int idMeeting;
    /** date de la réunion*/
    private String date;

    /** heure de la réunion*/
    private int hour;

    /** heure de la réunion*/
    private int minute;

/** sujet de la réunion*/
    private String subject;

    /** list des participants*/
    private ParticipantsList participants;

    /** lieu de la réunion*/
     private Room room;

    public Meeting(int idMeeting, String date, int hour, int minute, String subject, ParticipantsList participants, Room room) {
        this.idMeeting = idMeeting;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.subject = subject;
        this.participants = participants;
        this.room = room;
    }
    public int getIdMeeting() {
        return idMeeting;
    }

    public void setIdMeeting(int idMeeting) {
        this.idMeeting = idMeeting;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ParticipantsList getParticipants() {
        return participants;
    }

    public void setParticipants(ParticipantsList participants) {
        this.participants = participants;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }




//*************************************************************************************************
    public static final Parcelable . Creator CREATOR = new Parcelable . Creator () {

        public Meeting createFromParcel ( Parcel in ) {
            return new Meeting ( in );
        }

        public Meeting [] newArray ( int size ) {
            return new Meeting [ size ];
        }
    };

  // Parcelling part
    public Meeting ( Parcel in ){
        this.idMeeting = in .readInt ();
        this.date = in .readString ();
        this.hour = in .readInt ();
        this.minute = in .readInt ();
        this.subject = in .readString ();
        this.participants = in.readParcelable(ParticipantsList.class.getClassLoader());
        this.room = in.readParcelable (Room.class.getClassLoader());

    }

    @Override public int describeContents () {
        return 0 ;
    }

    /**Là on écrit dans la Parcel les informations importantes de ta classe.
     * Les informations vont transiter via la Parcel, et ensuite nous recréerons notre instance de Meeting depuis la Parcel.
     * Ce qui va passer entre les deux activités ce n'est pas directement l'instance de Meeting, mais c'est l'objet de type Parcel (et qui contient les informations de l'instance de Meeting).*/
    @Override public void writeToParcel ( Parcel dest , int flags ) {
        dest.writeInt (this.idMeeting);
        dest.writeString (this.date);
        dest.writeInt (this.hour);
        dest.writeInt (this.minute);
        dest.writeString (this.subject);
        dest.writeParcelable(this.participants, flags);
        dest.writeParcelable(this.room, flags);
    }

}
