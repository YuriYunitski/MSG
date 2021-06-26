package com.yunitski.msg.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import com.yunitski.msg.R;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView videoPlayerVideoView;
    private ImageButton backImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoPlayerVideoView = findViewById(R.id.videoPlayerVideoView);
        backImageButton = findViewById(R.id.backImageButton);
        backImageButton.setOnClickListener(this);
        Intent intent = getIntent();
        videoPlayerVideoView.setVideoURI(Uri.parse(intent.getStringExtra("videoUrl")));
        videoPlayerVideoView.setMediaController(new MediaController(this));
        videoPlayerVideoView.requestFocus();
        videoPlayerVideoView.start();
    }

    @Override
    public void onClick(View v) {
        VideoActivity.this.finish();
    }
}