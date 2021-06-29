package com.yunitski.msg.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yunitski.msg.R;
import com.yunitski.msg.data.VideosInProfile;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private List<VideosInProfile> videosInProfileList;

    private OnVideoClickListener listener;


    public interface OnVideoClickListener{
        void onVideoClick(int position);
    }

    public void setOnVideoClickListener(OnVideoClickListener listener){
        this.listener = listener;
    }

    public VideoAdapter(ArrayList<VideosInProfile> videosInProfileList){
        this.videosInProfileList = videosInProfileList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_in_profile, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {

        VideosInProfile videosInProfile = videosInProfileList.get(position);
        Glide.with(holder.imageViewVideo).load(videosInProfile.getVideoUrl()).into(holder.imageViewVideo);
    }

    @Override
    public int getItemCount() {
        return videosInProfileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewVideo;

        public ViewHolder(@NonNull View itemView, OnVideoClickListener listener) {
            super(itemView);

            imageViewVideo = itemView.findViewById(R.id.imageInProfileImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onVideoClick(position);
                        }
                    }
                }
            });
        }
    }
}
