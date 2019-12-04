package com.uimainon.mareus.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.uimainon.mareus.R;
import com.uimainon.mareus.events.DeleteMeetingEvent;
import com.uimainon.mareus.model.Meeting;
import com.uimainon.mareus.rooteur.AddMeetingActivity;
import com.uimainon.mareus.service.DateService;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MeetingRecyclerViewAdapter extends RecyclerView.Adapter<MeetingRecyclerViewAdapter.ViewHolder>{

    private final List<Meeting> mMeeting;
    private DateService mDateService;

    public MeetingRecyclerViewAdapter(List<Meeting> items) {
        mMeeting = items;
        mDateService = new DateService();
    }

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_meeting_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Meeting meeting = mMeeting.get(position);

        String mListInfos="";
        String dateFr = mDateService.getGoodFormatFrenchDate(meeting.getDate());
        if(meeting.getMinute() < 10){
            mListInfos = dateFr +" - "+meeting.getHour()+"h0"+meeting.getMinute()+" - "+meeting.getSubject();
        }else{
            mListInfos = dateFr +" - "+meeting.getHour()+"h"+meeting.getMinute()+" - "+meeting.getSubject();
        }
        holder.mNameRoomHour.setText(mListInfos); // infos de la réunion (date, heure et thème)

        String listMail ="";
        int sizeOfMail = meeting.getParticipants().size();
        for (int i = 0 ; i < sizeOfMail ; i++){
            if(i == sizeOfMail-1){
                listMail += meeting.getParticipants().get(i).getEmail();
            }else {
                listMail += meeting.getParticipants().get(i).getEmail() + ", ";
            }
        }
        holder.mListMail.setText(listMail); // liste des mail des participants de la réunion

        int color = Color.parseColor(meeting.getRoom().getColor());
        holder.mCircleColor.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.MULTIPLY); // cercle de couleur représentant la réunion
        int mLetterIndex = meeting.getRoom().getName().length();
        String mLetter = meeting.getRoom().getName().substring(mLetterIndex-1);
        holder.mLetterRoom.setText(mLetter); // lettre de la réunion

        holder.mClickListener.setOnClickListener(new View.OnClickListener() {
            @Override  // pour modifier une réunion existante
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent ( context , AddMeetingActivity.class );
                intent.putExtra("receiveMeeting", (Parcelable)meeting);
                context.startActivity (intent);
            }
        });

        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override // pour supprimer une réunion
            public void onClick(View v) {
                EventBus.getDefault().post(new DeleteMeetingEvent(meeting));
            }
        });
    }


    @Override
    public int getItemCount() {
        return mMeeting.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mCircleColor;
        private TextView mNameRoomHour;
        private TextView mListMail;
        private ImageButton mDeleteBtn;
        public View mClickListener;
        private TextView mLetterRoom;

        public ViewHolder(View view) {
            super(view);

            mCircleColor = (ImageView) view.findViewById(R.id.item_circle_color_meeting);
            mNameRoomHour = (TextView) view.findViewById(R.id.item_list_name_room);
            mListMail = (TextView) view.findViewById(R.id.item_list_mail_participant);
            mDeleteBtn = (ImageButton) view.findViewById(R.id.item_list_delete_button);
            mLetterRoom = (TextView) view.findViewById(R.id.item_list_letter_Room);

            this.mClickListener = view;
        }
    }

}
