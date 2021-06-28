package com.yunitski.msg.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.dynamicanimation.animation.SpringAnimation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.yunitski.msg.R;
import com.yunitski.msg.data.MSGmessage;

import java.util.ArrayList;
import java.util.List;

public class MSGAdapter extends ArrayAdapter<MSGmessage> {

    private List<MSGmessage> gmessages;

    private Activity activity;
    private OnPhotoClickListener listener;
    private List<MSGmessage> selectedList = new ArrayList<>();
    private List<MSGmessage> filteredList = new ArrayList<>();

    private boolean multiSelect = false;


    public MSGAdapter(@NonNull Activity context, int resource, List<MSGmessage> messages) {
        super(context, resource, messages);
        this.gmessages = messages;
        this.activity = context;
        filteredList.addAll(gmessages);
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            for (MSGmessage msGmessage : selectedList){
                int pos = getPosition(msGmessage);
        FirebaseDatabase  database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = database.getReference();
        if (gmessages.get(pos).isMine()){
            mDatabaseRef.child("messages").child(gmessages.get(pos).getPusId()).removeValue();
        } else {

            mDatabaseRef.child("messages").child(gmessages.get(pos).getPusId()).child("deleted").setValue(true);

        }
                gmessages.remove(pos);
            }
            notifyDataSetChanged();
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            multiSelect = false;
            selectedList.clear();
            notifyDataSetChanged();
        }
    };


    public interface OnPhotoClickListener{
        void onUserClick(int position);
    }

    public void setOnPhotoClickListener(OnPhotoClickListener listener){
        this.listener = listener;
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
        viewHolder.update(msGmessage);
//        boolean isText = msGmessage.getImageUrl() == null && msGmessage.getVideoUrl() == null && msGmessage.getAudioUrl() == null;
//        if (isText){
//            viewHolder.messageTextView.setVisibility(View.VISIBLE);
//            viewHolder.photoImageView.setVisibility(View.GONE);
//            viewHolder.audioImageView.setVisibility(View.GONE);
//            viewHolder.messageTextView.setText(msGmessage.getText());
//            viewHolder.messageTimeTextView.setText(msGmessage.getTime());
//        } else if (msGmessage.getVideoUrl() == null && msGmessage.getAudioUrl() == null){
//
//            viewHolder.messageTextView.setVisibility(View.GONE);
//            viewHolder.photoImageView.setVisibility(View.VISIBLE);
//            viewHolder.audioImageView.setVisibility(View.GONE);
////            if (viewHolder.photoImageView.getLayoutParams().width < 900){
////                viewHolder.photoImageView.getLayoutParams().width = 500;
////            }
//
//            //viewHolder.photoImageView.requestLayout();
//            //Picasso.get().load(msGmessage.getImageUrl()).resize(0, 600).into(viewHolder.photoImageView);
//            Glide.with(viewHolder.photoImageView.getContext()).asBitmap().load(msGmessage.getImageUrl()).into(viewHolder.photoImageView);
//            viewHolder.messageTimeTextView.setText(msGmessage.getTime());
//            viewHolder.photoImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getPosition(msGmessage);
//                    listener.onUserClick(position);
//                }
//            });
////            viewHolder.photoImageView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
////                @Override
////                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
////
////                }
////            });
////            viewHolder.photoImageView.setOnLongClickListener(new View.OnLongClickListener() {
////                @Override
////                public boolean onLongClick(View v) {
////                    int position = getPosition(msGmessage);
////                    listenerMess.onMessageClick(position);
////                    return true;
////                }
////            });
//        } else if (msGmessage.getImageUrl() == null && msGmessage.getAudioUrl() == null){
//
//            viewHolder.messageTextView.setVisibility(View.VISIBLE);
//            viewHolder.messageTextView.setText("ᐅ");
//            viewHolder.photoImageView.setVisibility(View.VISIBLE);
//            viewHolder.audioImageView.setVisibility(View.GONE);
//            Glide.with(viewHolder.photoImageView.getContext()).load(msGmessage.getVideoUrl()).into(viewHolder.photoImageView);
//            viewHolder.photoImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getPosition(msGmessage);
//                    listener.onUserClick(position);
//                }
//            });
//            viewHolder.messageTimeTextView.setText(msGmessage.getTime());
//        } else if (msGmessage.getImageUrl() == null && msGmessage.getVideoUrl() == null){
//
//            viewHolder.messageTextView.setVisibility(View.GONE);
//            viewHolder.photoImageView.setVisibility(View.GONE);
//            viewHolder.audioImageView.setVisibility(View.VISIBLE);
//            viewHolder.messageTimeTextView.setText(msGmessage.getTime());
//            viewHolder.audioImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getPosition(msGmessage);
//                    listener.onUserClick(position);
//                }
//            });
//        if (!msGmessage.isAudioPlaying()){
//            viewHolder.audioImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24);
//        } else {
//            viewHolder.audioImageView.setImageResource(R.drawable.ic_baseline_pause_24);
//        }
//    }
////        viewHolder.messageLinearLayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
////            @Override
////            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//////                menu.add(0, 1, getPosition(msGmessage), "action 1");
//////                //установка бэкграунда контекстного меню
//////                int positionOfMenuItem = 0;
//////
//////                listPosition = getPosition(msGmessage);
//////                MenuItem item = menu.getItem(positionOfMenuItem);
//////                SpannableString s = new SpannableString("Удалить");
//////                s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
//////                item.setTitle(s);
////            }
////        });
////        viewHolder.messageLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
////            @Override
////            public boolean onLongClick(View v) {
////                int position = getPosition(msGmessage);
////                listenerMess.onMessageClick(position);
////                return true;
////            }
////        });
//
//
//            viewHolder.messageLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    ((AppCompatActivity) v.getContext()).startSupportActionMode(actionModeCallbacks);
//                    if (multiSelect) {
//
//                        if (selectedList.contains(msGmessage)) {
////            viewHolder.messageLinearLayout.setBackgroundColor(Color.WHITE);
//
//                                selectedList.remove(msGmessage);
//                            if (getItemViewType(position) == 0) {
//                                viewHolder.messageLinearLayout.setBackgroundResource(R.drawable.message_outcome_new);
//                            } else {
//                                viewHolder.messageLinearLayout.setBackgroundResource(R.drawable.message_income_new);
//                            }
//                        } else {
////            viewHolder.messageLinearLayout.setBackgroundColor(Color.LTGRAY);
//
//                            selectedList.add(msGmessage);
//                            if (getItemViewType(position) == 0) {
//                                viewHolder.messageLinearLayout.setBackgroundResource(R.drawable.selected_outome_mess);
//                            } else {
//                                viewHolder.messageLinearLayout.setBackgroundResource(R.drawable.selected_income_mess);
//                            }
//                        }
//                    }
//                    return true;
//                }
//            });
//            viewHolder.messageLinearLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (multiSelect) {
//
//                        if (selectedList.contains(msGmessage)) {
////            viewHolder.messageLinearLayout.setBackgroundColor(Color.WHITE);
//
//                            selectedList.remove(msGmessage);
//                            if (getItemViewType(position) == 0) {
//                                viewHolder.messageLinearLayout.setBackgroundResource(R.drawable.message_outcome_new);
//                            } else {
//                                viewHolder.messageLinearLayout.setBackgroundResource(R.drawable.message_income_new);
//                            }
//                        } else {
////            viewHolder.messageLinearLayout.setBackgroundColor(Color.LTGRAY);
//
//                            selectedList.add(msGmessage);
//                            if (getItemViewType(position) == 0) {
//                                viewHolder.messageLinearLayout.setBackgroundResource(R.drawable.selected_outome_mess);
//                            } else {
//                                viewHolder.messageLinearLayout.setBackgroundResource(R.drawable.selected_income_mess);
//                            }
//                        }
//                    }
//                    else {
//                        listen.onMessageSelected(gmessages.get(position));
//                    }
//                }
//            });
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
        private ImageView photoImageView, audioImageView;
        private LinearLayout messageLinearLayout;
//        private CheckBox selectMessageCheckBox;

        public ViewHolder(View view){
            photoImageView = view.findViewById(R.id.photoImageView);
            messageTextView = view.findViewById(R.id.messageTextView);
            messageTimeTextView = view.findViewById(R.id.messageTimeTextView);
            messageLinearLayout = view.findViewById(R.id.messageLinearLayout);
            audioImageView = view.findViewById(R.id.audioImageView);
//            selectMessageCheckBox = view.findViewById(R.id.selectMessageCheckBox);

        }
        private void selectItem(MSGmessage msGmessage){

            if (multiSelect) {

                if (selectedList.contains(msGmessage)) {
//            viewHolder.messageLinearLayout.setBackgroundColor(Color.WHITE);

                    selectedList.remove(msGmessage);
                    if (getItemViewType(getPosition(msGmessage)) == 0) {
                        messageLinearLayout.setBackgroundResource(R.drawable.message_outcome_new);
                    } else {
                        messageLinearLayout.setBackgroundResource(R.drawable.message_income_new);
                    }
                } else {
//            viewHolder.messageLinearLayout.setBackgroundColor(Color.LTGRAY);

                    selectedList.add(msGmessage);
                    if (getItemViewType(getPosition(msGmessage)) == 0) {
                        messageLinearLayout.setBackgroundResource(R.drawable.selected_outome_mess);
                    } else {
                        messageLinearLayout.setBackgroundResource(R.drawable.selected_income_mess);
                    }
                }
            }
        }

        void update(MSGmessage msGmessage){
            boolean isText = msGmessage.getImageUrl() == null && msGmessage.getVideoUrl() == null && msGmessage.getAudioUrl() == null;
            if (isText){
                messageTextView.setVisibility(View.VISIBLE);
                photoImageView.setVisibility(View.GONE);
                audioImageView.setVisibility(View.GONE);
                messageTextView.setText(msGmessage.getText());
                messageTimeTextView.setText(msGmessage.getTime());
            } else if (msGmessage.getVideoUrl() == null && msGmessage.getAudioUrl() == null){

                messageTextView.setVisibility(View.GONE);
                photoImageView.setVisibility(View.VISIBLE);
                audioImageView.setVisibility(View.GONE);
//            if (viewHolder.photoImageView.getLayoutParams().width < 900){
//                viewHolder.photoImageView.getLayoutParams().width = 500;
//            }

                //viewHolder.photoImageView.requestLayout();
                //Picasso.get().load(msGmessage.getImageUrl()).resize(0, 600).into(viewHolder.photoImageView);
                Glide.with(photoImageView.getContext()).asBitmap().load(msGmessage.getImageUrl()).into(photoImageView);
                messageTimeTextView.setText(msGmessage.getTime());
                photoImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getPosition(msGmessage);
                        listener.onUserClick(position);
                    }
                });
//            viewHolder.photoImageView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//                @Override
//                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//
//                }
//            });
//            viewHolder.photoImageView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    int position = getPosition(msGmessage);
//                    listenerMess.onMessageClick(position);
//                    return true;
//                }
//            });
            } else if (msGmessage.getImageUrl() == null && msGmessage.getAudioUrl() == null){

                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText("ᐅ");
                photoImageView.setVisibility(View.VISIBLE);
                audioImageView.setVisibility(View.GONE);
                Glide.with(photoImageView.getContext()).load(msGmessage.getVideoUrl()).into(photoImageView);
                photoImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getPosition(msGmessage);
                        listener.onUserClick(position);
                    }
                });
                messageTimeTextView.setText(msGmessage.getTime());
            } else if (msGmessage.getImageUrl() == null && msGmessage.getVideoUrl() == null){

                messageTextView.setVisibility(View.GONE);
                photoImageView.setVisibility(View.GONE);
                audioImageView.setVisibility(View.VISIBLE);
                messageTimeTextView.setText(msGmessage.getTime());
                audioImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getPosition(msGmessage);
                        listener.onUserClick(position);
                    }
                });
                if (!msGmessage.isAudioPlaying()){
                    audioImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                } else {
                    audioImageView.setImageResource(R.drawable.ic_baseline_pause_24);
                }
            }
            if (selectedList.contains(msGmessage)){

                if (getItemViewType(getPosition(msGmessage)) == 0) {
                    messageLinearLayout.setBackgroundResource(R.drawable.selected_outome_mess);
                } else {
                    messageLinearLayout.setBackgroundResource(R.drawable.selected_income_mess);
                }
            } else {
                if (getItemViewType(getPosition(msGmessage)) == 0) {
                    messageLinearLayout.setBackgroundResource(R.drawable.message_outcome_new);
                } else {
                    messageLinearLayout.setBackgroundResource(R.drawable.message_income_new);
                }
            }
            messageLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((AppCompatActivity)v.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(msGmessage);
                    return true;
                }
            });
            messageLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (multiSelect){
                        selectItem(msGmessage);
                    }
                }
            });
        }
    }

    @Override
    public int getCount() {
        return gmessages.size();
    }

    public interface MessageAdapterListener{
        void onMessageSelected(MSGmessage msGmessage);
    }
}
