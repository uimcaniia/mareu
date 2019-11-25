package com.uimainon.mareus.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uimainon.mareus.R;
import com.uimainon.mareus.events.ChoiceRoomEvent;
import com.uimainon.mareus.events.DeleteMeetingEvent;
import com.uimainon.mareus.model.Room;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ListRoomGridAdapter extends BaseAdapter {

    private List<Room> mRoom;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListRoomGridAdapter(List<Room> mRoom, Context aContext) {
        this.context = aContext;
        this.mRoom = mRoom;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return mRoom.size();
    }

    @Override
    public Object getItem(int position) {
        return mRoom.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Room mRoomGrid = mRoom.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item_room, null);

            holder = new ViewHolder();
            holder.mCircleColor = (ImageView) convertView.findViewById(R.id.imageView_room);
            holder.mLetterRoom = (TextView) convertView.findViewById(R.id.textView_room);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int color = Color.parseColor(mRoomGrid.getColor());
        holder.mCircleColor.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.MULTIPLY); // cercle de couleur représentant la réunion
        int mLetterIndex = mRoomGrid.getName().length();
        String mLetter = mRoomGrid.getName().substring(mLetterIndex-1);
        holder.mLetterRoom.setText(mLetter); // lettre de la réunion

        holder.mCircleColor.setOnClickListener(new View.OnClickListener() {
            @Override // pour supprimer une réunion
            public void onClick(View v) {
                EventBus.getDefault().post(new ChoiceRoomEvent(mRoomGrid));
            }
        });


        return convertView;
    }


    public class ViewHolder {
        private ImageView mCircleColor;
        private TextView mLetterRoom;

    }
}
