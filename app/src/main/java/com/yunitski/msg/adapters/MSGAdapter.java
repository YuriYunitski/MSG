package com.yunitski.msg.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

    public MSGAdapter(@NonNull Activity context, int resource, List<MSGmessage> messages) {
        super(context, resource, messages);
        this.gmessages = messages;
        this.activity = context;
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
        }
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

        public ViewHolder(View view){
            photoImageView = view.findViewById(R.id.photoImageView);
            messageTextView = view.findViewById(R.id.messageTextView);
            messageTimeTextView = view.findViewById(R.id.messageTimeTextView);
        }
    }
}
