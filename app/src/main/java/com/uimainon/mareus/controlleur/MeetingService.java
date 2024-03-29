package com.uimainon.mareus.controlleur;

import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.ParticipantsList;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.service.DateService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MeetingService {

    private MeetingApiService apiService;
    private DateService mDateService = new DateService();
    private Meeting mNewMeeting;

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

    /** TRouve la liste des participants disponible à la date et aux horaires souhaités */
    public List<Participant> getListParticipantForThisDate(String date, int hour, int minute){
        return apiService.getListParticipantForThisDate(date, hour, minute);
    }

    /** TRouve la liste des Room disponible à la date et aux horaires souhaités */
    public List<Room> getListRoomForThisDate(String date, int hour, int minute){
        return apiService.getListRoomForThisDate(date, hour, minute);
    }

    /** vérifie si les participants choisie sont effectivement disponible à la date et aux horaire choisir dans la réunion*/
    public Boolean IsThatPossibleParticipant(Meeting meetingToVerif) {
        return apiService.IsThatPossibleParticipant(meetingToVerif);
    }
    /** vérifie si la Room choisie est effectivement disponible à la date et aux horaire choisir dans la réunion*/
    public Boolean IsThatPossibleRoom(Meeting meetingToVerif) {
        return apiService.IsThatPossibleRoom(meetingToVerif);
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
            hour = mDateService.giveHourStartDay();
        }
        List<Room> searchNewListeOfRooms;
        for(int i = hour ; i < mDateService.giveHourEndDay(); i++){
            searchNewListeOfRooms = getListRoomForThisDate(date, i, minute);
            if(searchNewListeOfRooms.size()!=0){
                goodHour = i;
                break;
            }
        }
        return goodHour;
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
     * renvoie la liste des réunion suivant le filtre "par Room"
     * @param mMeetingFilter liste des réunion à réorganiser suivant la room sélectionnée
     * @param mRoomSelect room sélectionnée servant de repaire
     * @return liste de réunion (peut etre vide)
     */
    public List<Meeting> makeGoogOrderRoomListMeeting(List<Meeting> mMeetingFilter, Room mRoomSelect) {
        int mLetterIndex = mRoomSelect.getName().length();
        String mLetterFilter = mRoomSelect.getName().substring(mLetterIndex-1); // lettre filtre

        List<Meeting> listMeeting = new ArrayList<>(mMeetingFilter);
        List<Meeting> listGoodOrder = new ArrayList<>();

        int sizeOfMeeting = listMeeting.size();

        for(int i = 0 ; i <sizeOfMeeting ; i++){
            int mLetterRoom = listMeeting.get(i).getRoom().getName().length();
            String sLetterRoom = listMeeting.get(i).getRoom().getName().substring(mLetterRoom-1);
            if(sLetterRoom.equals(mLetterFilter)){
                listGoodOrder.add(listMeeting.get(i));
            }
        }
        return listGoodOrder;
    }

    /**
     * reéorganise et renvoie la liste des réunion suivant le filtre "par date"
     * @param mMeetingFilter liste des réunion à filtrer suivant la date à comparer, puis réorganisée par ordre chronologique
     * @param yearSelect année a comparer
     * @param monthSelect mois a comparer
     * @param daySelect jour a comparer
     * @return liste de réunion (peut etre vide)
     * @throws ParseException
     */
    public List<Meeting> makeGoogOrderDateListMeeting(List<Meeting> mMeetingFilter, int yearSelect, int monthSelect, int daySelect) throws ParseException {
        String dateToCompare = mDateService.getStringDateWithIntNumber(yearSelect, monthSelect, daySelect);

        List<Meeting> listMeeting = new ArrayList<>();
        List<Meeting> listGoodOrder = new ArrayList<>();

        int sizeOfMeetingWhithGoodDate = mMeetingFilter.size(); // on conserve que les meeting à la bonne date
        for (int y = 0; y < sizeOfMeetingWhithGoodDate; y++) {
            if (mMeetingFilter.get(y).getDate().equals(dateToCompare)) {
                listMeeting.add(mMeetingFilter.get(y));
            }
        }
        int sizeOfMeeting = listMeeting.size(); // on les réorganise du plus récent au plus ancien
        if (sizeOfMeeting == 0) { // si il n'y a rien, on renvoie la liste vide
            return listGoodOrder;
        }
        if (sizeOfMeeting == 1) { // si il n'y a qu'une réunion, on renvoie la liste
            listGoodOrder.add(listMeeting.get(0));
            return listGoodOrder;
        }
        if (sizeOfMeeting > 1) {
            listGoodOrder.add(listMeeting.get(0));// on ajoute le premier dans la liste pour commencer la comparaison
            for (int i = 1; i < sizeOfMeeting; i++) {
                int sizeListGoodOrder = listGoodOrder.size();
                for (int x = 0; x < sizeListGoodOrder; x++) {
                    int result = mDateService.compareTwoDate(listMeeting.get(i).getDate(), listGoodOrder.get(x).getDate());
                    if (result < 0) {
                        listGoodOrder.add(x, listMeeting.get(i));
                        break;
                    }
                    if (result == 0) {
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
                    }
                    if (result > 0) {
                        if (x == sizeListGoodOrder - 1) {
                            listGoodOrder.add(listMeeting.get(i));
                            break;
                        }
                    }
                }
            }
        }
        return listGoodOrder;
    }

    /**
     * retourne le prochain Id pour la futur nouvelle réunion
     * @return
     */
    public int searchNextIdForNewMeeting(){
        int sizeOfAllMeeting = AllMeetings().size();
        List<Meeting>mListAllMeeting = AllMeetings();
        int idMeeting = 0;
        if(mListAllMeeting.size() != 0){ // si des réunion existe déjà, vu qu'en cas de suppression, des ID peuvent disparaitre et faire un trou dans la chaîne, on récupe la dernière réunion, et on utilise son ID +1
            idMeeting =  mListAllMeeting.get(sizeOfAllMeeting-1).getIdMeeting()+1;
        }
        return idMeeting;
    }


    /** prend par défault la date d'aujourd'hui et l'heure courante.
     * vérifie si dans ce crénaux, une salle de réunion est disponible.
     *      => si rien de dispo, avance l'heure de 1h jusqu'à la fin de la journée
     *      => si toujours rien de dispo, cherche au jour d'après la prochaine dispo  (sur max 365 jours)
     * return un Meeting avec une Room instancié ou qui vaut null
     */
    public Meeting searchOtherDateAndTimeForNewMeeting(String sDateToday, Date today, int goodHourInFrance, int idMeeting){
        int mMinute = 0; // on remet les minutes à zéro
        int resultHour = searchIfRoomDispoForOtherTime(sDateToday, goodHourInFrance, mMinute); // retourne une heure dispo (0 si rien dans la journée dans aucune room)
        List<Room> mRooms = getListRoomForThisDate(sDateToday, goodHourInFrance, mMinute);

        if(resultHour != 0){ // si une room au moins est disponible avec ce nouvel horaire
            mRooms = getListRoomForThisDate(sDateToday, resultHour, mMinute);
            mNewMeeting = new Meeting(idMeeting, sDateToday, resultHour, mMinute, "Aucun sujet", new ParticipantsList(), mRooms.get(0));
        }
        if(resultHour == 0){ // si aucune heure de dispo dans aucune salle dans la même journée, alors on change de jour (+1)
            Calendar oneDayAfter;
            for (int i = 1 ; i < 365 ; i++){ //on boucle sur 1 an au max
                oneDayAfter = mDateService.addCalendarToDate(today, i); // Nouveau calendrier, on avance d'un jour
                sDateToday = mDateService.getStringDate(oneDayAfter);
                goodHourInFrance = mDateService.giveHourStartDay(); // on remet l'heure de début d'une journée (définit dans la classe DateService)
                mRooms = getListRoomForThisDate(sDateToday, mDateService.giveHourStartDay(), mMinute);
                if(mRooms.size()!=0){ // si dans cette nouvelle journée, une salle est au moins dispo, on stoppe la boucle
                    break;
                }else{ // sinon on cherche sur un autre crénaux horaire de cette nouvelle journée
                    resultHour = searchIfRoomDispoForOtherTime(sDateToday, 0, mMinute); // on remet l'heure à zéro pour repartir sur l'heure de départ d'une journée
                    if(resultHour != 0){ // si dans ce nouveau crénau horaire de cette nouvelle journée, une salle est au moins dispo, on stoppe la boucle
                        goodHourInFrance = resultHour;
                        mRooms = getListRoomForThisDate(sDateToday, goodHourInFrance, mMinute);
                        break;
                    }
                }
            }
            if(mRooms.size()==0) { // si malgré tout Il n'y a pas de place
                mNewMeeting = new Meeting(idMeeting, sDateToday, goodHourInFrance, mMinute, "Aucun sujet", new ParticipantsList(), null);

            }else {
                Room mRoom = mRooms.get(0);
                mNewMeeting = new Meeting(idMeeting, sDateToday, goodHourInFrance, mMinute, "Aucun sujet", new ParticipantsList(), mRoom);
            }
        }
        return mNewMeeting;
    }

}
