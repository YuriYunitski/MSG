package com.yunitski.msg.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetHost;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.menu.MenuView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yunitski.msg.R;
import com.yunitski.msg.data.MSGmessage;

import java.util.ArrayList;
import java.util.List;

public class MSGAdapter extends ArrayAdapter<MSGmessage> {

    private final List<MSGmessage> gmessages;

    private final Activity activity;
    private OnPhotoClickListener listener;
    private OnAudioClickListener audioClickListener;
    private final List<MSGmessage> selectedList = new ArrayList<>();
    private List<MSGmessage> filteredList = new ArrayList<>();

    ImageView playImageView;
    ImageView stopImageView;
    private boolean multiSelect = false;


    public MSGAdapter(@NonNull Activity context, int resource, List<MSGmessage> messages) {
        super(context, resource, messages);
        this.gmessages = messages;
        this.activity = context;
        filteredList.addAll(gmessages);
    }

    private final ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

//            if (selectedList.size() == 0){
//                mode.finish();
//            }
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Удалить сообщения?");
            builder.setCancelable(false);
            builder.setPositiveButton("да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
                }
            });
            builder.setNegativeButton("отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
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

    public interface OnAudioClickListener{
        void onAudioClick(int position, ImageView view);
    }

    public void setOnAudioClickListener(OnAudioClickListener audioClickListener){
        this.audioClickListener = audioClickListener;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        MSGmessage msGmessage = getItem(position);
        int layoutResource;
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

        viewHolder.update(msGmessage);

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
        private final TextView messageTextView;
        private final TextView messageTimeTextView;
        private final ImageView photoImageView;
        private final ImageView audioImageView;
        private final LinearLayout messageLinearLayout;

        public ViewHolder(View view){
            photoImageView = view.findViewById(R.id.photoImageView);
            messageTextView = view.findViewById(R.id.messageTextView);
            messageTimeTextView = view.findViewById(R.id.messageTimeTextView);
            messageLinearLayout = view.findViewById(R.id.messageLinearLayout);
            audioImageView = view.findViewById(R.id.audioImageView);

        }
        private void selectItem(MSGmessage msGmessage){

            if (multiSelect) {

                if (selectedList.contains(msGmessage)) {

                    selectedList.remove(msGmessage);
                    if (getItemViewType(getPosition(msGmessage)) == 0) {
                        messageLinearLayout.setBackgroundResource(R.drawable.out_mess_var2);
                    } else {
                        messageLinearLayout.setBackgroundResource(R.drawable.message_income_new);
                    }
                } else {

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
            } else if (msGmessage.getImageUrl() != null){

                messageTextView.setVisibility(View.GONE);
                photoImageView.setVisibility(View.VISIBLE);
                audioImageView.setVisibility(View.GONE);
                Glide.with(photoImageView.getContext()).asBitmap().load(msGmessage.getImageUrl()).into(photoImageView);
                messageTimeTextView.setText(msGmessage.getTime());

            } else if (msGmessage.getVideoUrl() != null){

                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText("ᐅ");
                photoImageView.setVisibility(View.VISIBLE);
                audioImageView.setVisibility(View.GONE);
                Glide.with(photoImageView.getContext()).load(msGmessage.getVideoUrl()).into(photoImageView);
                messageTimeTextView.setText(msGmessage.getTime());
            } else if (msGmessage.getAudioUrl() != null){

                messageTextView.setVisibility(View.GONE);
                photoImageView.setVisibility(View.GONE);
                audioImageView.setVisibility(View.VISIBLE);
                messageTimeTextView.setText(msGmessage.getTime());
//                if (!msGmessage.isAudioPlaying()){
//                    audioImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24);
//                } else {
//                    audioImageView.setImageResource(R.drawable.ic_baseline_pause_24);
//                }
            }
            if (selectedList.contains(msGmessage)){

                if (getItemViewType(getPosition(msGmessage)) == 0) {
                    messageLinearLayout.setBackgroundResource(R.drawable.selected_outome_mess);
                } else {
                    messageLinearLayout.setBackgroundResource(R.drawable.selected_income_mess);
                }
            } else {
                if (getItemViewType(getPosition(msGmessage)) == 0) {
                    messageLinearLayout.setBackgroundResource(R.drawable.out_mess_var2);
                } else {
                    messageLinearLayout.setBackgroundResource(R.drawable.message_income_new);
                }
            }
            messageLinearLayout.setOnLongClickListener(v -> {
                ((AppCompatActivity)v.getContext()).startSupportActionMode(actionModeCallbacks);
                selectItem(msGmessage);
                return true;
            });
            messageLinearLayout.setOnClickListener(v -> {
                if (multiSelect){
                    selectItem(msGmessage);
                }
            });
            photoImageView.setOnClickListener(v -> {
                if (multiSelect){
                    selectItem(msGmessage);
                } else {
                    int position = getPosition(msGmessage);
                    listener.onUserClick(position);
                }
            });
            photoImageView.setOnLongClickListener(v -> {
                ((AppCompatActivity)v.getContext()).startSupportActionMode(actionModeCallbacks);
                selectItem(msGmessage);
                return true;
            });
            audioImageView.setOnClickListener(v -> {
                if (multiSelect){
                    selectItem(msGmessage);
                } else {
                    int position = getPosition(msGmessage);
                    audioClickListener.onAudioClick(position, audioImageView);

                }
            });
            audioImageView.setOnLongClickListener(v -> {
                ((AppCompatActivity)v.getContext()).startSupportActionMode(actionModeCallbacks);
                selectItem(msGmessage);
                return true;
            });
        }

    }




    @Override
    public int getCount() {
        return gmessages.size();
    }

}
