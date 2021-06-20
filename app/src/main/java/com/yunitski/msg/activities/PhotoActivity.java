package com.yunitski.msg.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yunitski.msg.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoActivity extends AppCompatActivity {

    private ImageView photoImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photoImageView = findViewById(R.id.photoImageView);
        Intent intent = getIntent();
        String photoUrl = intent.getStringExtra("photoUrl");
        Glide.with(photoImageView.getContext()).load(photoUrl).into(photoImageView);

        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photoImageView);
        photoViewAttacher.update();
    }
}