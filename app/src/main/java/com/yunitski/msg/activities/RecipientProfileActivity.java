package com.yunitski.msg.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yunitski.msg.R;
import com.yunitski.msg.adapters.AudioAdapter;
import com.yunitski.msg.adapters.FileAdapter;
import com.yunitski.msg.adapters.ImageAdapter;
import com.yunitski.msg.adapters.VideoAdapter;
import com.yunitski.msg.data.AudioInProfile;
import com.yunitski.msg.data.FileInProfile;
import com.yunitski.msg.data.ImagesInProfile;
import com.yunitski.msg.data.VideosInProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class RecipientProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView userNameRecipientProfileTextView, photoTextView, videoTextView, fileTextView, audioTextView, noMediaTextView;
    ArrayList<String> urisList;
    ArrayList<String> videoUrisList;
    ArrayList<String> audioUrisList;
    ArrayList<String> audioNameList;
    ArrayList<String> fileUrisList;
    ArrayList<String> fileNameList;
    ArrayList<ImagesInProfile> imagesInProfileArrayList;
    ArrayList<VideosInProfile> videosInProfileArrayList;
    ArrayList<AudioInProfile> audioInProfileArrayList;
    ArrayList<FileInProfile> fileInProfileArrayList;
    ImageAdapter adapter;
    VideoAdapter videoAdapter;
    AudioAdapter audioAdapter;
    FileAdapter fileAdapter;
    RecyclerView recyclerView;
    ImageView recipientUserAvatar;
    ImageButton recipientImageButton;
    private boolean photo, video, file, audio, isPlay;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_profile);
        recyclerView = findViewById(R.id.imagesRecyclerView);
        photo = true;
        video = false;
        file = false;
        audio = false;
        isPlay = false;
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
        audioNameList = intent.getStringArrayListExtra("audioName");
        fileUrisList = intent.getStringArrayListExtra("fileUris");
        fileNameList = intent.getStringArrayListExtra("fileName");
        userNameRecipientProfileTextView.setText(intent.getStringExtra("recipientUserName"));
        Glide.with(recipientUserAvatar).load(intent.getStringExtra("recipientUserAvatar")).into(recipientUserAvatar);
        photoTextView.setTextColor(Color.BLACK);
//        videoTextView.setTextColor(Color.LTGRAY);
//        fileTextView.setTextColor(Color.LTGRAY);
//        audioTextView.setTextColor(Color.LTGRAY);
        videoTextView.setTextColor(Color.WHITE);
        fileTextView.setTextColor(Color.WHITE);
        audioTextView.setTextColor(Color.WHITE);
        updateUI();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.recipientImageButton){
            RecipientProfileActivity.this.finish();
        } else if (v.getId() == R.id.photoTextView){
            photoTextView.setTextColor(Color.BLACK);
//            videoTextView.setTextColor(Color.LTGRAY);
//            fileTextView.setTextColor(Color.LTGRAY);
//            audioTextView.setTextColor(Color.LTGRAY);
            videoTextView.setTextColor(Color.WHITE);
            fileTextView.setTextColor(Color.WHITE);
            audioTextView.setTextColor(Color.WHITE);
            photo = true;
            video = false;
            file = false;
            audio = false;
            isPlay = false;
            updateUI();
        } else if (v.getId() == R.id.videoTextView){
            videoTextView.setTextColor(Color.BLACK);
//            photoTextView.setTextColor(Color.LTGRAY);
//            fileTextView.setTextColor(Color.LTGRAY);
//            audioTextView.setTextColor(Color.LTGRAY);
            photoTextView.setTextColor(Color.WHITE);
            fileTextView.setTextColor(Color.WHITE);
            audioTextView.setTextColor(Color.WHITE);
            photo = false;
            video = true;
            file = false;
            audio = false;
            isPlay = false;
            updateUI();
        } else if (v.getId() == R.id.fileTextView){
            fileTextView.setTextColor(Color.BLACK);
//            photoTextView.setTextColor(Color.LTGRAY);
//            videoTextView.setTextColor(Color.LTGRAY);
//            audioTextView.setTextColor(Color.LTGRAY);
            photoTextView.setTextColor(Color.WHITE);
            videoTextView.setTextColor(Color.WHITE);
            audioTextView.setTextColor(Color.WHITE);
            photo = false;
            video = false;
            file = true;
            audio = false;
            isPlay = false;
            updateUI();
        } else if (v.getId() == R.id.audioTextView){
//            photoTextView.setTextColor(Color.LTGRAY);
//            videoTextView.setTextColor(Color.LTGRAY);
//            fileTextView.setTextColor(Color.LTGRAY);
            audioTextView.setTextColor(Color.BLACK);
            photoTextView.setTextColor(Color.WHITE);
            videoTextView.setTextColor(Color.WHITE);
            fileTextView.setTextColor(Color.WHITE);
            photo = false;
            video = false;
            file = false;
            audio = true;
            isPlay = false;
            updateUI();
        }
    }

    private void updateUI(){
        if (photo && !video && !file && !audio){
            if (urisList.size() == 0){
                noMediaTextView.setText("Нет фотографий");
            } else {
                noMediaTextView.setText("");
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
        } else {
                noMediaTextView.setText("");
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
        } else if (!photo && !video && !file && audio){
            if (audioNameList.size() == 0){
                noMediaTextView.setText("Нет аудио");
            } else {
                noMediaTextView.setText("");
            }

            audioInProfileArrayList = new ArrayList<>();
            Collections.reverse(audioUrisList);
            Collections.reverse(audioNameList);
            for (int i = 0; i < audioUrisList.size(); i++){
                audioInProfileArrayList.add(new AudioInProfile(audioUrisList.get(i), audioNameList.get(i)));
            }

            LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            audioAdapter = new AudioAdapter(audioInProfileArrayList);
            recyclerView.setAdapter(audioAdapter);
            audioAdapter.setOnAudioClickListener(new AudioAdapter.OnAudioClickListener() {
                @Override
                public void onAudioClick(int position, ImageView view) {
                    if (!isPlay){
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(RecipientProfileActivity.this, Uri.parse(audioUrisList.get(position)));
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        view.setImageResource(R.drawable.ic_baseline_pause_24);
                        isPlay = true;
                    } else {
                        mediaPlayer.stop();
                        view.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        isPlay = false;
                    }
//                    Runnable task = new Runnable() {
//                        @Override
//                        public void run() {
//                        }
//                    };
//                    Thread thread = new Thread(task);
//                    thread.start();
                }
            });
        } else if (!photo && !video && file && !audio){
            if (fileNameList.size() == 0){
                noMediaTextView.setText("Нет файлов");
            } else {
                noMediaTextView.setText("");
            }
            fileInProfileArrayList = new ArrayList<>();
            Collections.reverse(fileNameList);
            Collections.reverse(fileUrisList);
            for (int i = 0; i < fileUrisList.size(); i++){
                fileInProfileArrayList.add(new FileInProfile(fileUrisList.get(i), fileNameList.get(i)));
            }
            LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            fileAdapter = new FileAdapter(fileInProfileArrayList);
            recyclerView.setAdapter(fileAdapter);
            fileAdapter.setOnFileClickListener(new FileAdapter.OnFileClickListener() {
                @Override
                public void onFileClick(int position) {
                    Intent intent13 = new Intent(Intent.ACTION_VIEW);
                    intent13.setDataAndType(Uri.parse(fileUrisList.get(position)), "application/*");
                    intent13.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent13);
                }
            });
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null){
            mediaPlayer.stop();
            isPlay = false;
        }
    }
}