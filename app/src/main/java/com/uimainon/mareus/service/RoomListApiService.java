package com.uimainon.mareus.service;

import androidx.collection.ArraySet;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.Room;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomListApiService {

    private List<Room> mListOfAllRooms = RoomListGenerator.generateAllRooms();
    private DateService mDateService = new DateService();
    /**
     * renvoie la liste de toutes les salle de réunion de la boite
     * @return
     */
    public List<Room> getListAllRooms(){
        return mListOfAllRooms;
    }

    /**
     * TRouve la liste des Room disponible à la date et aux horaires souhaités à partir des réunions existante déjà enregistrée
     * on retire celles déjà réservées pour les réunions avant et après - de 70 min avant et après l'heure voulue
     * @param mListAllMeetingsCreate récupère toutes les réunions
     * @param date
     * @param hour
     * @param minute
     * @return
     */
    public List<Room> getListRoomDispo(List<Meeting> mListAllMeetingsCreate, String date, int hour, int minute) {
        List<Room> mListOfAllRooms = new ArrayList<>(RoomListGenerator.generateAllRooms()); // récupère toutes les salles
        List<Room> listAllRoomToDispoToReturn = new ArrayList<>(mListOfAllRooms);
        Set<Room> mSetRoomNotDispo = new HashSet<>();

        if(mListAllMeetingsCreate.size() == 0){ // on vérifie si il existe déjà des réunions et on renvoie toute la liste si il n'y a rien
            return mListOfAllRooms;
        }else{ // si il y a déjà des réunion de créés, on récupère les réunions à la même date pour ensuite vérifier si le créneau est disponible
            List<Meeting> mListMeetingForThisDate = getListMeetingForThisDate(mListAllMeetingsCreate, date);
            if(mListMeetingForThisDate.size()==0){ // si Aucune réunion à la même date, on renvoie toute la liste des Rooms
                return mListOfAllRooms;
            }else {
                int sizeRestant = mListMeetingForThisDate.size(); // on récup le nombre de réunion de la même journée
                for (int i = 0; i < sizeRestant; i++) {
                    int timeDiff = mDateService.getTimeBetweenTwoHours(mListMeetingForThisDate.get(i).getHour(), mListMeetingForThisDate.get(i).getMinute(), hour, minute);
                    if (timeDiff < 70) {  // si on a moins de 70 min entre les 2 horaires
                        mSetRoomNotDispo.add(mListMeetingForThisDate.get(i).getRoom());
                    }
                }
                List<Room> listRoomNotDispo = new ArrayList<Room>(mSetRoomNotDispo); // convertion Set et List pour la boucle
                int sizeRoomPasDispo = listRoomNotDispo.size();
                int sizeRoom = mListOfAllRooms.size();
                for (int y = 0; y < sizeRoom; y++) {
                    for (int z = 0; z < sizeRoomPasDispo; z++) {
                        if (mListOfAllRooms.get(y).getId() == listRoomNotDispo.get(z).getId()) {
                            listAllRoomToDispoToReturn.remove(mListOfAllRooms.get(y));
                        }
                    }
                }
                return listAllRoomToDispoToReturn;
            }
        }
    }

    /**
     * vérifie après avoir tenter de valider la création (ou modificaton) d'une réunion, si la Room est toujours dispo
     * (si problème lors du rafraichissement des fragments en cas de motif de la date)
     * @param mListAllMeetingsCreate liste de toute les réunion déjà créé
     * @param meetingToVerif meeting en création ou modification
     * @return
     */
    public Boolean IsThatPossibleRoom(List<Meeting> mListAllMeetingsCreate, Meeting meetingToVerif) {
        boolean b = true;
        if(mListAllMeetingsCreate.size() == 0){ // on vérifie si il existe déjà des réunions
            return b;
        }else{// si il y a déjà des réunion de créés, on récupère les réunions à la même date pour ensuite vérifier si le créneau est disponible
            List<Meeting> mListMeetingForThisDate = getListMeetingForThisDate(mListAllMeetingsCreate, meetingToVerif.getDate());
            if (mListMeetingForThisDate.size() != 0) { // si il y a effectivement des réunions dans la meme journée
                int sizeRestant = mListMeetingForThisDate.size(); // on récup le nombre de réunion de la même journée}
                for (int i = 0; i < sizeRestant; i++) {
                    if(mListMeetingForThisDate.get(i).getIdMeeting() == meetingToVerif.getIdMeeting()){
                        if(mListMeetingForThisDate.get(i).getRoom().getId() == meetingToVerif.getRoom().getId()){
                            b = true; // si il s'agit d'une réunion déjà enregistré et de la meme salle, on valide direct comme quoi elle est dispo
                            break;
                        }
                    }else{// si c'est pas la même que la réunion a vérifier, mais qu'elle est dans la même journée, on vérif si l'heure va poser problème
                        int timeDiff = mDateService.getTimeBetweenTwoHours(mListMeetingForThisDate.get(i).getHour(), mListMeetingForThisDate.get(i).getMinute(), meetingToVerif.getHour(), meetingToVerif.getMinute());
                        if (timeDiff < 70) { // si on a moins de 70 min entre les 2 horaires
                            if(mListMeetingForThisDate.get(i).getRoom().getId() == meetingToVerif.getRoom().getId()){
                                b=false;
                                break;
                            }
                        }
                    }
                }
            }
            return b;
        }
    }

    /** compare et retourne une liste de réunion suivant la date en paramètre */
    private List<Meeting> getListMeetingForThisDate(List<Meeting> meetingList, String date){
        List<Meeting> mListMeetingForThisDate = new ArrayList<>();
        int size = meetingList.size(); // on récup le nombre de réunion
        for (int i = 0 ; i < size ; i++) {// on récupère les réunions à la même date
            if (meetingList.get(i).getDate().equals(date)) {
                mListMeetingForThisDate.add(meetingList.get(i));
            }
        }
        return mListMeetingForThisDate;
    }

}
