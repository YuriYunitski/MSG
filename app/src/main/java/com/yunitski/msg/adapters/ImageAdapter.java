package com.yunitski.msg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.yunitski.msg.R;
import com.yunitski.msg.data.ImagesInProfile;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<ImagesInProfile> imagesInProfileList;

    private OnImageClickListener listener;


    public interface OnImageClickListener{
        void onImageClick(int position);
    }

    public void setOnImageClickListener(OnImageClickListener listener){
        this.listener = listener;
    }

    public ImageAdapter(ArrayList<ImagesInProfile> imagesInProfileList){
        this.imagesInProfileList = imagesInProfileList;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_in_profile, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {

        ImagesInProfile imagesInProfile = imagesInProfileList.get(position);
        //Picasso.get().load(imagesInProfile.getImageUrl()).into(holder.imageView);
        Glide.with(holder.imageView).load(imagesInProfile.getImageUrl()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return imagesInProfileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        public ViewHolder(@NonNull View itemView, OnImageClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageInProfileImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onImageClick(position);
                        }
                    }
                }
            });
        }
    }
}
