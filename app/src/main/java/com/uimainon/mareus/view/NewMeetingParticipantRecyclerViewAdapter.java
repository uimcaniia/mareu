package com.uimainon.mareus.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.uimainon.mareus.R;
import com.uimainon.mareus.events.DispoParticipantEvent;
import com.uimainon.mareus.events.NotDispoParticipantEvent;
import com.uimainon.mareus.model.Participant;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class NewMeetingParticipantRecyclerViewAdapter extends RecyclerView.Adapter<NewMeetingParticipantRecyclerViewAdapter.ViewHolder>{

    private final List<Participant> mParticipants;

    public NewMeetingParticipantRecyclerViewAdapter(List<Participant> items) {
        mParticipants = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_add_meeting_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Participant participant = mParticipants.get(position);
        holder.mMailParticipant.setText(participant.getEmail());
        holder.mCheckBtn.setOnCheckedChangeListener(null);

        if(participant.getDispo()==0){
            holder.mCheckBtn.setChecked(false);
        }else{
            holder.mCheckBtn.setChecked(true);
        }
        holder.mCheckBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    EventBus.getDefault().post(new DispoParticipantEvent(participant));

                } else {
                    EventBus.getDefault().post(new NotDispoParticipantEvent(participant));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mMailParticipant;
        private CheckBox mCheckBtn;
        public View clickListener;

        public ViewHolder(View view) {
            super(view);

            mMailParticipant = (TextView) view.findViewById(R.id.item_list_email_participant);
            mCheckBtn = (CheckBox) view.findViewById(R.id.checkBox);
            this.clickListener = view;
        }
    }
}
