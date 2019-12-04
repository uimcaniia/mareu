package com.uimainon.mareus.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Participant implements Parcelable {

    /** participant Id */
    protected int id;

    /** participant email */
    protected String email;

    protected int isDispo;

    @Override
    public String toString() {
        return "Participant{" +
                "isDispo=" + isDispo +
                '}';
    }

    public Participant(){}

    public Participant(int id, String email , int isDispo) {
        this.id = id;
        this.email = email;
        this.isDispo = isDispo;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDispo() {
        return this.isDispo;
    }

    public void setDispo(int dispo) {
        isDispo = dispo;
    }

    //************************************************************************************
    //Second constructeur qui sera appelé lors de la "Deparcelablisation"
    public Participant(Parcel in)
    {
        this.getFromParcel(in);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable . Creator CREATOR = new Parcelable . Creator () {

        public Participant createFromParcel ( Parcel in ) {
            return new Participant ( in );
        }

        @Override
        public Object[] newArray(int size) {
            return null;
        }
    };

    @Override public int describeContents () {
        return 0 ;
    }

    //On ecrit dans le parcel les données de notre objet
    @Override public void writeToParcel (Parcel dest, int flags) {
        dest.writeInt ( this.id );
        dest.writeString ( this.email );
        dest.writeInt(this.isDispo);
    }

    //On va ici hydrater notre objet à partir du Parcel
    public void getFromParcel(Parcel in)
    {
        this.id = in .readInt ();
        this.email = in .readString ();
        this.isDispo = in.readInt();
    }
}
