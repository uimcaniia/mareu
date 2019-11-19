package com.uimainon.mareus.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ParticipantsList extends ArrayList<Participant> implements Parcelable {


    public ParticipantsList() {}


    public ParticipantsList(Parcel in){
            this.getFromParcel(in);
            }


    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

            public ParticipantsList createFromParcel(Parcel in) {
                return new ParticipantsList(in);
                }


        @Override
        public Object[] newArray(int size) {
                return null;
            }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Taille de la liste
        int size = this.size();
        dest.writeInt(size);
        for(int i=0; i < size; i++)
        {
            Participant pers = this.get(i); //On vient lire chaque objet personne
            dest.writeInt ( pers.id );
            dest.writeString ( pers.email );
            dest.writeInt(pers.isDispo);
        }
    }


    public void getFromParcel(Parcel in) {
        // On vide la liste avant tout remplissage
        this.clear();

        //Récupération du nombre d'objet
        int size = in.readInt();

        //On repeuple la liste avec de nouveau objet
        for(int i = 0; i < size; i++)
        {
            Participant pers = new Participant();
            pers.setId(in.readInt());
            pers.setEmail(in.readString());
            pers.setDispo(in.readInt());
            this.add(pers);
        }

    }


}
