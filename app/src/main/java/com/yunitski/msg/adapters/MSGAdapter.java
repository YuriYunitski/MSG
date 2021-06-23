package com.yunitski.msg.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.yunitski.msg.R;
import com.yunitski.msg.data.MSGmessage;

import java.util.List;

public class MSGAdapter extends ArrayAdapter<MSGmessage> {

    private List<MSGmessage> gmessages;

    private Activity activity;
    private OnPhotoClickListener listener;
    private OnLongMessageClickListener listenerMess;

    public MSGAdapter(@NonNull Activity context, int resource, List<MSGmessage> messages) {
        super(context, resource, messages);
        this.gmessages = messages;
        this.activity = context;
    }

    public interface OnPhotoClickListener{
        void onUserClick(int position);
    }

    public void setOnPhotoClickListener(OnPhotoClickListener listener){
        this.listener = listener;
    }

    public interface OnLongMessageClickListener{
        void onMessageClick(int position);
    }

    public void setOnLongMessageClickListener(OnLongMessageClickListener listener){
        this.listenerMess = listener;
    }

    private int listPosition;
    public int getPositionList(){
        return listPosition;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        MSGmessage msGmessage = getItem(position);
        int layoutResource = 0;
        int viewType = getItemViewType(position);

        if (viewType == 0){
            layoutResource = R.layout.your_message_item;
        } else {
            layoutResource = R.layout.my_message_item;
        }

        if (convertView != null){
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }


//        viewHolder.selectMessageCheckBox.setVisibility(View.GONE);
        boolean isText = msGmessage.getImageUrl() == null;
        if (isText){
            viewHolder.messageTextView.setVisibility(View.VISIBLE);
            viewHolder.photoImageView.setVisibility(View.GONE);
            viewHolder.messageTextView.setText(msGmessage.getText());
            viewHolder.messageTimeTextView.setText(msGmessage.getTime());
        } else {

            viewHolder.messageTextView.setVisibility(View.GONE);
            viewHolder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.photoImageView.getContext()).load(msGmessage.getImageUrl()).into(viewHolder.photoImageView);
            viewHolder.messageTimeTextView.setText(msGmessage.getTime());
            viewHolder.photoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getPosition(msGmessage);
                    listener.onUserClick(position);
                }
            });
            viewHolder.photoImageView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                }
            });
//            viewHolder.photoImageView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    int position = getPosition(msGmessage);
//                    listenerMess.onMessageClick(position);
//                    return true;
//                }
//            });
        }
        viewHolder.messageLinearLayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, getPosition(msGmessage), "action 1");
                //установка бэкграунда контекстного меню
                int positionOfMenuItem = 0;

                listPosition = getPosition(msGmessage);
                MenuItem item = menu.getItem(positionOfMenuItem);
                SpannableString s = new SpannableString("Удалить");
                s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
                item.setTitle(s);
            }
        });
//        viewHolder.messageLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                int position = getPosition(msGmessage);
//                listenerMess.onMessageClick(position);
//                return true;
//            }
//        });
        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        int flag;
        MSGmessage msGmessage = gmessages.get(position);
        if (msGmessage.isMine()){
            flag = 0;
        } else {
            flag = 1;
        }

        return flag;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder{
        private TextView messageTextView, messageTimeTextView;
        private ImageView photoImageView;
        private LinearLayout messageLinearLayout;
//        private CheckBox selectMessageCheckBox;

        public ViewHolder(View view){
            photoImageView = view.findViewById(R.id.photoImageView);
            messageTextView = view.findViewById(R.id.messageTextView);
            messageTimeTextView = view.findViewById(R.id.messageTimeTextView);
            messageLinearLayout = view.findViewById(R.id.messageLinearLayout);
//            selectMessageCheckBox = view.findViewById(R.id.selectMessageCheckBox);
        }
    }

    @Override
    public int getCount() {
        return gmessages.size();
    }
}
