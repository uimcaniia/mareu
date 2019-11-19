package com.uimainon.mareus.controlleur;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.service.DateService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeetingService {

    private final MeetingApiService apiService;
    private DateService mDateService = new DateService();
    private int hourStartDay = mDateService.giveHourStartDay();
    private int hourEndDay = mDateService.giveHourEndDay();

    public MeetingService(MeetingApiService apiService) {
        this.apiService = apiService;
    }

    /** renvoie la liste des réunions  qui s'affichera dans le recyclerView */
    public List<Meeting> AllMeetings() {
        return apiService.AllMeetings();
    }

    /** renvoie la liste de toutes les ROOMs */
    public List<Room> AllRooms() {
        return apiService.getListAllRooms();
    }

    /** renvoie la liste de tous les participants */
    public List<Participant> AllParticipants() {
        return apiService.getListAllParticipant();
    }

    /** Supprime une réunion */
    public void deleteMeeting(Meeting meeting) { apiService.deleteMeeting(meeting); }

    /** Ajoute une réunion à la liste des réunions */
    public void addMeetingToList(Meeting meeting) {
       apiService.addMeetingToList(meeting);
    }

    /**
     * TRouve la liste des participants disponible à la date et aux horaires souhaités
     * on retire ceux déjà en réunion avant et après - de 70 min avant et après l'heure voulue
     */
    public List<Participant> getListParticipantForThisDate(String date, int hour, int minute, Meeting meeting){
        return apiService.getListParticipantForThisDate(date, hour, minute, meeting);
    }

    /**
     * TRouve la liste des Room disponible à la date et aux horaires souhaités
     * on retire celles déjà réservées réunion avant et après - de 70 min avant et après l'heure voulue
     */
    public List<Room> getListRoomForThisDate(String date, int hour, int minute){
        return apiService.getListRoomForThisDate(date, hour, minute);
    }

    /**
     * Trouve l'heure où des Room sont dispo en augmentant l'heure de recherche de +1 si il n'y a aucune Room de dispo
     * @param date
     * @param hour
     * @param minute
     * @return l'heure dispo pour effectuer une recherche de liste sinon 0
     */
    public int searchIfRoomDispoForOtherTime(String date, int hour, int minute) {
        int goodHour = 0;
        if(hour == 0){ // si on cherche sur une nouvelle journée, hour sera à 0 sinon elle vaudra l'heure de la précédente recherche dans la même journée
            hour = hourStartDay;
        }
        List<Room> searchNewListeOfRooms;
        for(int i = hour ; i < hourEndDay ; i++){
            searchNewListeOfRooms = getListRoomForThisDate(date, i, minute);
            if(searchNewListeOfRooms.size()!=0){
                goodHour = i;
                break;
            }
        }
        return goodHour;
    }

/**
 * vérifie après avoir voulu valider la création (ou modification) d'une réunion, si les participant sont effectivement disponible
 * (en cas de modif de la date pour validation sans savoir si les participant sont dispo)
 * */
    public Boolean IsThatPossibleParticipant(Meeting meetingToVerif) {
        return apiService.IsThatPossibleParticipant(meetingToVerif);
    }
    /**
     * vérifie après avoir voulu valider la création (ou modification) d'une réunion, si la Room est effectivement disponible
     * (en cas de modif de la date pour validation sans savoir si la Room est dispo)
     * */
    public Boolean IsThatPossibleRoom(Meeting meetingToVerif) {
        return apiService.IsThatPossibleRoom(meetingToVerif);
    }

    /**
     * complète et réorganise la liste des Room DISPONIBLE dans l'ordre alphabétique.
     *      => En cas de création de réunion, la room instanciée dans l'object Meeting était une Room dispo, et donc présent dans la liste, donc il ne faut pas la rajouter
     *      => En cas de modification d'une réunion, la room enregistré dans l'object Meeting ne sera pas présent, donc à rajouter
     * @param mRooms liste des salles de réunions DISPONIBLE à la date et à l'heure définit dans mMeeting
     * @param mMeeting réunion concernée. On ajoute sa Room enregistrée sauf si elle vaut NULL
     *      => vaut NULL si lors de la validation, la salle est finalement pas dispo.
     * @param BtnvalidAndError vaut true si le bouton valider été activé
     * @return liste de Rooms dispo et dans la bon ordre
     */
    public List<Room> makeGoogOrderListRoom(List<Room> mRooms, Meeting mMeeting, Boolean BtnvalidAndError){
        List<Room> goodList = new ArrayList<>(mRooms);
        List<Integer> goodInt = new ArrayList<>();
        int nbrInit = mRooms.size();
        boolean r = false;

        for(int x = 0 ; x < nbrInit ; x++){
            goodInt.add(mRooms.get(x).getId()); // on insère les ID des rooms dans la liste
            if(mMeeting.getRoom()!=null) { // si la room de la réunion en cours de création ou modification ne vaut pas NULL
                if (mRooms.get(x).getId() == mMeeting.getRoom().getId()) {
                    r = true;  // et qu'elle est déjà présente dans la liste ( en cas de création), on évite de la réinsérer pour ne pas avoir de doublon
                }
            }
        }
        if(!r) { // si la room de la réunion concernée n'est pas déjà présente dans la liste (en cas de modification), on la rajoute
            goodList.clear();
            if(mMeeting.getRoom()!=null) { // si elle ne vaut pas null,
                goodInt.add(mMeeting.getRoom().getId()); // on ajoute son ID a la liste
                mRooms.add(mMeeting.getRoom()); //on l'ajoute a la liste des ROOm (qui est en désordre)
            }
            Collections.sort(goodInt); // réoganise par ordre croissant la liste des id.
            int sizeOfInt = goodInt.size();
            for(int i = 0 ; i < sizeOfInt ; i++){
                for(int z = 0 ; z < sizeOfInt ; z++){ // oc compare les ID de la liste des ID avec les ID de la liste des ROOMS pour les ajouter dans le bon ordre
                    if (goodInt.get(i)== mRooms.get(z).getId()){
                        goodList.add(mRooms.get(z));
                        break;
                    }
                }
            }
        }
        if(BtnvalidAndError){ // Si en validant, la salle enregistré de la réunion concernée n'est pas dispo finalement, on la supprime
            int sizeRoomsFinal = goodList.size();
            for(int j = 0 ; j < sizeRoomsFinal ; j++){
                if(mMeeting.getRoom()!=null){
                    if(goodList.get(j).getId() == mMeeting.getRoom().getId()){
                        goodList.remove(goodList.get(j));
                        break;
                    }
                }
            }
        }
        return goodList;
    }

    /**
     * réorganise la liste des réunion suivant le filtre "par Room"
     * @param mMeetingFilter liste des réunion à réorganiser
     * @return liste de réunion
     */
    public List<Meeting> makeGoogOrderRoomListMeeting(List<Meeting> mMeetingFilter) {
        List<Meeting> listMeeting = new ArrayList<>(mMeetingFilter);
        List<Meeting> listGoodOrder = new ArrayList<>();
        List<Room> mRoomList= apiService.getListAllRooms();
        int sizeOfMeeting = listMeeting.size();
        int sizeOfRoom = mRoomList.size();
        for(int i = 0 ; i <sizeOfRoom ; i++){
            int index = i;
            for(int x = 0 ; x <sizeOfMeeting ; x++){
                if(listMeeting.get(x).getRoom().getId() == index){
                    listGoodOrder.add(listMeeting.get(x));
                }
            }
        }
        return listGoodOrder;
    }

    /**
     * reéorganise la liste des réunion suivant le filtre "par date"
     * @param mMeetingFilter liste des réunion à réorganiser
     * @return liste de réunion
     * @throws ParseException
     */
    public List<Meeting> makeGoogOrderDateListMeeting(List<Meeting> mMeetingFilter) throws ParseException {
        List<Meeting> listMeeting = new ArrayList<>(mMeetingFilter);
        List<Meeting> listGoodOrder = new ArrayList<>();

        int sizeOfMeeting = mMeetingFilter.size();

        listGoodOrder.add(listMeeting.get(0));// on ajoute le premier dans la liste pour commencer la comparaison
        for(int i = 1 ; i <sizeOfMeeting ; i++){
            int sizeListGoodOrder = listGoodOrder.size();
            for (int x = 0; x < sizeListGoodOrder; x++) {
                int result = mDateService.compareTwoDate(listMeeting.get(i).getDate(), listGoodOrder.get(x).getDate());
                if(result < 0) {
                    listGoodOrder.add(x, listMeeting.get(i));
                    break;
                }if (result == 0) {
                    int resHourMinute = mDateService.compareTwoHourMinute(listMeeting.get(i).getHour(), listMeeting.get(i).getMinute(), listGoodOrder.get(x).getHour(), listGoodOrder.get(x).getMinute());
                    if (resHourMinute < 0) {
                        listGoodOrder.add(x, listMeeting.get(i));
                        break;
                    }
                    if (resHourMinute == 0) {
                        listGoodOrder.add(listMeeting.get(i));
                        break;
                    }
                    if (x == sizeListGoodOrder - 1) {
                        listGoodOrder.add(listMeeting.get(i));
                    }
                }if (result > 0) {
                    if(x == sizeListGoodOrder-1){
                        listGoodOrder.add(listMeeting.get(i));
                        break;
                    }
                }
            }
        }
        return listGoodOrder;
    }
}
