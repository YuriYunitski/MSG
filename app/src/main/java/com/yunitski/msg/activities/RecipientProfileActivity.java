package com.yunitski.msg.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yunitski.msg.R;
import com.yunitski.msg.adapters.ImageAdapter;
import com.yunitski.msg.adapters.VideoAdapter;
import com.yunitski.msg.data.ImagesInProfile;
import com.yunitski.msg.data.VideosInProfile;

import java.util.ArrayList;
import java.util.Collections;

public class RecipientProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView userNameRecipientProfileTextView, photoTextView, videoTextView, fileTextView, audioTextView, noMediaTextView;
    ArrayList<String> urisList;
    ArrayList<String> videoUrisList;
    ArrayList<String> audioUrisList;
    ArrayList<ImagesInProfile> imagesInProfileArrayList;
    ArrayList<VideosInProfile> videosInProfileArrayList;
    ImageAdapter adapter;
    VideoAdapter videoAdapter;
    RecyclerView recyclerView;
    ImageView recipientUserAvatar;
    ImageButton recipientImageButton;
    private boolean photo, video, file, audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_profile);
        recyclerView = findViewById(R.id.imagesRecyclerView);
        photo = true;
        video = false;
        file = false;
        audio = false;
        userNameRecipientProfileTextView = findViewById(R.id.userNameRecipientProfileTextView);
        recipientUserAvatar = findViewById(R.id.recipientProfileImageView);
        recipientImageButton = findViewById(R.id.recipientImageButton);
        photoTextView = findViewById(R.id.photoTextView);
        videoTextView = findViewById(R.id.videoTextView);
        fileTextView = findViewById(R.id.fileTextView);
        audioTextView = findViewById(R.id.audioTextView);
        noMediaTextView = findViewById(R.id.noMediaTextView);
        photoTextView.setOnClickListener(this);
        videoTextView.setOnClickListener(this);
        fileTextView.setOnClickListener(this);
        audioTextView.setOnClickListener(this);
        recipientImageButton.setOnClickListener(this);
        Intent intent = getIntent();
        urisList = intent.getStringArrayListExtra("imageUris");
        videoUrisList = intent.getStringArrayListExtra("videosUris");
        audioUrisList = intent.getStringArrayListExtra("audioUris");
        userNameRecipientProfileTextView.setText(intent.getStringExtra("recipientUserName"));
        Glide.with(recipientUserAvatar).load(intent.getStringExtra("recipientUserAvatar")).into(recipientUserAvatar);
        photoTextView.setTextColor(Color.BLACK);
        videoTextView.setTextColor(Color.LTGRAY);
        fileTextView.setTextColor(Color.LTGRAY);
        audioTextView.setTextColor(Color.LTGRAY);
        updateUI();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.recipientImageButton){
            RecipientProfileActivity.this.finish();
        } else if (v.getId() == R.id.photoTextView){
            photoTextView.setTextColor(Color.BLACK);
            videoTextView.setTextColor(Color.LTGRAY);
            fileTextView.setTextColor(Color.LTGRAY);
            audioTextView.setTextColor(Color.LTGRAY);
            photo = true;
            video = false;
            file = false;
            audio = false;
            updateUI();
        } else if (v.getId() == R.id.videoTextView){
            photoTextView.setTextColor(Color.LTGRAY);
            videoTextView.setTextColor(Color.BLACK);
            fileTextView.setTextColor(Color.LTGRAY);
            audioTextView.setTextColor(Color.LTGRAY);
            photo = false;
            video = true;
            file = false;
            audio = false;
            updateUI();
        } else if (v.getId() == R.id.fileTextView){
            photoTextView.setTextColor(Color.LTGRAY);
            videoTextView.setTextColor(Color.LTGRAY);
            fileTextView.setTextColor(Color.BLACK);
            audioTextView.setTextColor(Color.LTGRAY);
            photo = false;
            video = false;
            file = true;
            audio = false;
            updateUI();
        } else if (v.getId() == R.id.audioTextView){
            photoTextView.setTextColor(Color.LTGRAY);
            videoTextView.setTextColor(Color.LTGRAY);
            fileTextView.setTextColor(Color.LTGRAY);
            audioTextView.setTextColor(Color.BLACK);
            photo = false;
            video = false;
            file = false;
            audio = true;
            updateUI();
        }
    }

    private void updateUI(){
        if (photo && !video && !file && !audio){
            if (urisList.size() == 0){
                noMediaTextView.setText("Нет фотографий");
            }
            imagesInProfileArrayList = new ArrayList<>();
            Collections.reverse(urisList);
            for (int i = 0; i<urisList.size(); i++){
                imagesInProfileArrayList.add(new ImagesInProfile(urisList.get(i)));
            }

            LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ImageAdapter( imagesInProfileArrayList);
            recyclerView.setAdapter(adapter);

            adapter.setOnImageClickListener(new ImageAdapter.OnImageClickListener() {
                @Override
                public void onImageClick(int position) {
                    Intent intent = new Intent(RecipientProfileActivity.this, PhotoActivity.class);
                    intent.putExtra("photoUrl", imagesInProfileArrayList.get(position).getImageUrl());
                    startActivity(intent);
                }
            });
        } else if (!photo && video && !file && !audio){
            if (videoUrisList.size() == 0){
            noMediaTextView.setText("Нет видео");
        }

            videosInProfileArrayList = new ArrayList<>();
            Collections.reverse(videoUrisList);
            for (int i = 0; i < videoUrisList.size(); i++){
                videosInProfileArrayList.add(new VideosInProfile(videoUrisList.get(i)));
            }

            LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            videoAdapter = new VideoAdapter(videosInProfileArrayList);
            recyclerView.setAdapter(videoAdapter);

            videoAdapter.setOnVideoClickListener(new VideoAdapter.OnVideoClickListener() {
                @Override
                public void onVideoClick(int position) {
                    Intent intent = new Intent(RecipientProfileActivity.this, VideoActivity.class);
                    intent.putExtra("videoUrl", videosInProfileArrayList.get(position).getVideoUrl());
                    startActivity(intent);
                }
            });
        } else if (!photo && !video && file && !audio){

        } else if (!photo && !video && !file && audio){

        }


    }
}