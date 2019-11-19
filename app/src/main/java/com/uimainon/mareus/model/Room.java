package com.uimainon.mareus.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {

    /**numéro due al salle de réunion*/
    private Integer id;
    /**nom de la salle*/
    private String name;
    /**couleur de la salle*/
    private String color;

    public Room(){}
    /**
     * Constructor
     * @param id
     * @param name
     * @param color
     */
    public Room(Integer id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    //************************************************************************************
    //Second constructeur qui sera appelé lors de la "Deparcelablisation"
    public Room(Parcel in)
    {
        this.getFromParcel(in);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable. Creator CREATOR = new Parcelable . Creator () {

        public Room createFromParcel ( Parcel in ) {
            return new Room ( in );
        }

        @Override
        public Object[] newArray(int size) {
            return null;
        }
    };
    @Override
    public int describeContents () {
        return 0 ;
    }
    @Override
    public void writeToParcel ( Parcel dest , int flags ) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.color);

    }
    // Parcelling part
    public void getFromParcel ( Parcel in ){
        this.id = in .readInt ();
        this.name = in .readString ();
        this.color = in.readString();
    }



}
