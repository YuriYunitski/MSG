package com.yunitski.msg.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yunitski.msg.R;
import com.yunitski.msg.data.AudioInProfile;
import com.yunitski.msg.data.VideosInProfile;

import java.util.ArrayList;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {

    private List<AudioInProfile> audioInProfileList;

    private OnAudioClickListener listener;


    public interface OnAudioClickListener{
        void onAudioClick(int position, ImageView view);
    }

    public void setOnVideoClickListener(OnAudioClickListener listener){
        this.listener = listener;
    }

    public AudioAdapter(ArrayList<AudioInProfile> audioInProfileList){
        this.audioInProfileList = audioInProfileList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_in_profile, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioAdapter.ViewHolder holder, int position) {

        AudioInProfile videosInProfile = audioInProfileList.get(position);
        Glide.with(holder.imageViewAudio).load(videosInProfile.getAudioUrl()).into(holder.imageViewAudio);
    }

    @Override
    public int getItemCount() {
        return audioInProfileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewAudio;

        public ViewHolder(@NonNull View itemView, OnAudioClickListener listener) {
            super(itemView);

            imageViewAudio = itemView.findViewById(R.id.audioImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onAudioClick(position, imageViewAudio);
                        }
                    }
                }
            });
        }
    }
}