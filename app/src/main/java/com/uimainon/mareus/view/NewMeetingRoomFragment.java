package com.uimainon.mareus.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.uimainon.mareus.R;

import com.uimainon.mareus.di.DI;
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.Room;
import com.uimainon.mareus.controlleur.MeetingService;
import com.uimainon.mareus.service.DateService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewMeetingRoomFragment extends Fragment{

    private MeetingService mMeetingService;
    private TextInputEditText mTexteSubject;
    private OnHeadlineSelectedListener callback;
    private int mLastSpinnerPosition = 0;
    private DateService mDateService;
    private String mNameOfRoomSelected;
    private List<Room> listGoodOrder = new ArrayList<>();

    public static NewMeetingRoomFragment newInstance() {
        NewMeetingRoomFragment fragment = new NewMeetingRoomFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMeetingService = DI.getMeetingService();
        mDateService = new DateService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View drawer = inflater.inflate(R.layout.fragment_add_meeting_room, container, false);
        assert this.getArguments() != null;
        Meeting mMeeting = this.getArguments().getParcelable("meeting");
        initHourMinute(drawer, mMeeting.getHour(), mMeeting.getMinute());
        initThemeOfMeeting(drawer, mMeeting.getSubject());
        initDateActual(drawer, mMeeting.getDate());
        initListOfParticipant(drawer, mMeeting.getParticipants(), mMeeting);
        initListRooms(drawer, mMeeting, false);
        configBtnValidNewMeeting(drawer, mMeeting);
        configBtnAnnulNewMeeting(drawer);

        return drawer;
    }

    //-------------------------------------------------------------------------
    public interface OnHeadlineSelectedListener {
        public void callValidNewActivity();
        public void callRoomChanged(Room room);
    }

    public void setOnHeadlineSelectedListener(OnHeadlineSelectedListener callback) {
        this.callback = callback;
    }
    //-------------------------------------------------------------------------

/** configutration du bouton pour annuler la création ou la modification de la réunion*/
    private void configBtnAnnulNewMeeting(View drawer) {
        FloatingActionButton mAnnulBtn = drawer.findViewById(R.id.btn_back);
        mAnnulBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.callValidNewActivity();
            }
        });
    }

    /** affiche le sujet de la réunion*/
    private void initThemeOfMeeting(View drawer, String subject) {
        mTexteSubject = drawer.findViewById(R.id.theme_meeting);
        mTexteSubject.setText(subject);
    }

    /** Affiche heure et minute sélectionnée dans le fragment des dates et heures */
    private void initHourMinute(View drawer, int hour, int minute){
        TextView mHourMinute = drawer.findViewById(R.id.hourMeeting);
        String text;
        if(minute < 10){
            text = hour +" : 0"+minute;
        }else{
            text = hour +" : "+minute;
        }
        mHourMinute.setText(text);
    }

    /** Affiche la date actuelle dans le fragment en attendant d'être modifier dans le fragment des dates et heures */
    private void initDateActual(View drawer, String dateSelected) {
        TextView mDate = drawer.findViewById(R.id.dateMeeting);
        String mDateSelected = mDateService.getGoodFormatFrenchDate(dateSelected);
        mDate.setText(mDateSelected);
    }

    /** Init  List of Participants selected */
    private void initListOfParticipant(View drawer, List<Participant> mParticipants, Meeting mMeeting) {
        TextView mTexteParticipant = drawer.findViewById(R.id.listParticipantAdded);
        String mList ="";
        int sizeFinal = mParticipants.size();
        if(sizeFinal != 0){
            for(int i = 0 ; i < sizeFinal ; i++){
                mList += mParticipants.get(i).getEmail() +"\n";
            }
        }else{
            mList += "Il n'y a aucun participant pour le moment";
        }
        mTexteParticipant.setText(mList);
    }

    /**
     * Initialise la liste des Rooms en fonction de la date et de l'heure choisie pour n'afficher que les disponibles dans le SPINNER.
     * Met à jour la ROOM de la réunion concernée (meeting) :
     *      => enregistrera NULL (si aucune salle est dispo) et n'affichera rien dans le spinner. Stop le traitement (pas d'écouteur et pas de callback).
     *      => enregistrera la salle sélectionné dans le spinner si des salles sont disponibles (écouteur + callback pour mettre a jour les autres fragment)
     * @param drawer
     * @param meeting la réunion concernée
     * @param BtnvalidAndError bouton pour valider la réunion.  false si activé et en état d'échec (lorsque la salle est sélectionné, que l'on change l'heure juste après, mais que ca tombe cette fois là sur un créneau déjà prit)
     */
    private void initListRooms(View drawer, Meeting meeting, Boolean BtnvalidAndError) {
        List<Room> mRooms = mMeetingService.getListRoomForThisDate(meeting.getDate(), meeting.getHour(), meeting.getMinute());
        ArrayList<String> mStringRooms = new ArrayList<>();
        Spinner mSpinner = drawer.findViewById(R.id.spinnerRoom);

        if(mRooms.size()==0) {// Si aucune salle n'est disponible
            mStringRooms.add("Aucune salle de disponible");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, mStringRooms);
            mSpinner.setAdapter(adapter);
            mSpinner.setSelection(0);
            meeting.setRoom(null); // on enregistre dans la réunion que la salle vaut NULL pour empêcher la validation.
        }else { // Si des salles sont disponible
            listGoodOrder = mMeetingService.makeGoogOrderListRoom(mRooms, meeting, BtnvalidAndError);
            if(meeting.getRoom()== null) { // si la réunion avait NULL en guise de ROOM, on lui affecte la première salle de dispo dans la liste
                meeting.setRoom(listGoodOrder.get(0));
            }
            int nbr = listGoodOrder.size();
            for (int i = 0; i < nbr; i++) {
                mStringRooms.add(listGoodOrder.get(i).getName()); // liste String des salles de réunion disponible
                if (meeting.getRoom().getName().equals(listGoodOrder.get(i).getName())) {
                    mLastSpinnerPosition = i; // on fait pointer le spinner sur l'item de la réunion concernée
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_style, mStringRooms); // mise en place des item du spinner
            mSpinner.setAdapter(adapter); // envoie à l'adapter du spinner
            mSpinner.setSelection(mLastSpinnerPosition); // position par défault du pointeur du spinner en fonction de la ROOM déjà enregistré dan la réunion concernée
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {  // A la mise en place du pointeur du spinner, ce dernier va valider le choix du pointeur.
                    if (mLastSpinnerPosition != pos) { // vérifie si le pointeur n'est pas égale
                        mNameOfRoomSelected = adapterView.getItemAtPosition(pos).toString();
                        for (int i = 0; i < nbr; i++) {
                            if (listGoodOrder.get(i).getName().equals(mNameOfRoomSelected)) {
                                meeting.setRoom(listGoodOrder.get(i));
                            }
                        }
                        mLastSpinnerPosition = pos;
                        callback.callRoomChanged(meeting.getRoom());
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }

    /**
     * Action du bouton pour valider la création de la nouvelle réunion, ou la modification d'une réunion existante
     * vérifie si la ROOM, la liste des participants, la date et l'heure choisis, sont valide et ou existante
     * Met a jour la réunion, ou ajoute dans la liste, la nouvelle réunion en vérifiant que l'ID de la réunion existe déjà
     * @param drawer
     * @param mMeeting
     */
    private void configBtnValidNewMeeting(View drawer, Meeting mMeeting) {
        FloatingActionButton mValidBtn = drawer.findViewById(R.id.valid_meeting);
        List<Meeting>mListMeeting = mMeetingService.AllMeetings(); // liste de toutes les réunions existante
        mValidBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if((mMeeting.getParticipants().size()==0)||(mMeeting.getRoom()==null)){ // vérifie l'existance de participant
                    emptyInputShowError(mMeeting);
                }else {
                    Boolean a = verifIfThisRoomIsPossible(mMeeting); // vérifie si la salle choisie est disponible en cas de modif horaire sur un meeting déjà existant qui empièterait
                    Boolean b = verifIfThisParticipantIsPossible(mMeeting); // on vérifie si les participant peuvent effectivement participer en cas de modif horaire sur un meeting déjà existant qui empièterait
                    Boolean d = null;
                    try {
                        d = verifIfThisDateIsPossible(mMeeting); // vérifie si la date et l'heure ne sont pas déjà passée
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if((!a)||(!b)||(!d)){
                        notPossibleDataShowError(a, b, d, mMeeting, drawer);
                    }else { // Si aucune erreur, on sauvegarde
                        saveActivity(mListMeeting, mMeeting, drawer);
                        callback.callValidNewActivity();
                    }
                }
            }
        });
    }

    /**
     * Sauvegarde la création ou la modification de la réunion
     */
    private void saveActivity(List<Meeting> mListMeeting, Meeting mMeeting, View drawer){
        int size = mListMeeting.size();
        int indexExiste = 0;
        boolean isExiste = false;

        for (int i = 0; i < size; i++) { // vérifie si c'est une réunion déjà existante ou non dans la liste
            if (mListMeeting.get(i).getIdMeeting() == mMeeting.getIdMeeting()) {
                isExiste = true;
                indexExiste = mListMeeting.indexOf(mMeeting);
                break;
            }
        }
        mTexteSubject = drawer.findViewById(R.id.theme_meeting);
        String subject = Objects.requireNonNull(mTexteSubject.getText()).toString();
        if(subject.length()==0){
            subject = "Aucun sujet";
        }
        mMeeting.setSubject(subject);
        if (isExiste) { // si la réunion est existante et donc, à mettre à jour
            mListMeeting.set(indexExiste, mMeeting);
            Toast.makeText(getContext(), "La réunion a bien été modifiée", Toast.LENGTH_SHORT).show();
        } else { // si la réunion est à créer
            mMeetingService.addMeetingToList(mMeeting);
            Toast.makeText(getContext(), "La réunion a bien été ajoutée", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * affiche des messages d'erreur lors de l'enregistrement de la réunion
     */
    private void emptyInputShowError(Meeting mMeeting){
        if(mMeeting.getParticipants().size()==0){
            Toast.makeText(getContext(), "Vous n'avez sélectionné aucun participant pour la réunion", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Vous n'avez sélectionné aucune salle pour la réunion", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * affiche des messages d'erreur lors de l'enregistrement de la réunion
     */
    private void notPossibleDataShowError(Boolean a, Boolean b, Boolean d, Meeting mMeeting, View drawer){
        if(!a) {
            Toast.makeText(getContext(), "La salle "+mMeeting.getRoom().getName()+" est déjà utilisée pour une autre réunion sur la même plage horaire", Toast.LENGTH_SHORT).show();
            initListRooms(drawer, mMeeting, true); // on réinitialise la liste des rooms
        }if(!b) {
            Toast.makeText(getContext(), "Un ou plusieurs des participants est déjà inscrit à une autre réunion sur la même plage horaire", Toast.LENGTH_SHORT).show();
        }if(!d){
            Toast.makeText(getContext(), "La date ou l'heure choisie n'est pas correct", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * vérifie si la date choisie est possible, si elle n'est pas déjà passée
     * compare la date voulue avec la date d'aujourd'hui.
     * Si c'est la date d'aujourd'hui, vérifie si l'heure n'est pas passée
     * @param mMeeting
     * @return boolean
     * @throws ParseException
     */
    private Boolean verifIfThisDateIsPossible(Meeting mMeeting) throws ParseException {
        int isPossible = mDateService.isThatPossibleDate(mMeeting);
        Boolean b = true;
        if(isPossible < 0){
            b = false;
        }if(isPossible > 0){
            b = true;
        }if(isPossible == 0){
           b = mDateService.verifIfTimeIsPossible(mMeeting);
        }
        return b;
    }

    /**
     * vérifie si la liste des participants choisie est possible en cas de modification d'une réunion déjà existante (avec donc une liste déjà enregistrée)
     * @param mMeeting
     * @return
     */
    private Boolean verifIfThisParticipantIsPossible(Meeting mMeeting) {
        Boolean b = mMeetingService.IsThatPossibleParticipant(mMeeting);
        return b;
    }

    /**
     * vérifie si la ROOM choisie est possible en cas de modification d'une réunion déjà existante (avec donc une ROOM déjà enregistrée)
     * @param mMeeting
     * @return
     */
    private Boolean verifIfThisRoomIsPossible(Meeting mMeeting){
        Boolean b = mMeetingService.IsThatPossibleRoom(mMeeting);
        return b;
    }

}
