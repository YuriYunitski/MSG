package com.yunitski.msg.adapters;

import android.app.Activity;
import android.content.Context;
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
    public MSGAdapter(@NonNull Context context, int resource, List<MSGmessage> messages) {
        super(context, resource, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.message_item, parent, false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
        TextView textTextView = convertView.findViewById(R.id.textTextView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);

        MSGmessage gmessage = getItem(position);

        boolean isText = gmessage.getImageUrl() == null;

        if (isText){
            textTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            textTextView.setText(gmessage.getText());
        } else {
            textTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext()).load(gmessage.getImageUrl()).into(photoImageView);
        }
        nameTextView.setText(gmessage.getName());

        return convertView;
    }
}
