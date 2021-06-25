package com.yunitski.msg.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunitski.msg.R;
import com.yunitski.msg.adapters.ImageAdapter;
import com.yunitski.msg.data.ImagesInProfile;

import java.util.ArrayList;
import java.util.Collections;

public class RecipientProfileActivity extends AppCompatActivity {

    TextView userNameRecipientProfileTextView;
    ArrayList<String> urisList;
    RecyclerView imagesRecyclerView;
    ArrayList<ImagesInProfile> imagesInProfileArrayList;
    ImageAdapter adapter;
    RecyclerView r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_profile);
        r = findViewById(R.id.imagesRecyclerView);
        userNameRecipientProfileTextView = findViewById(R.id.userNameRecipientProfileTextView);
        Intent intent = getIntent();
        urisList = intent.getStringArrayListExtra("imageUris");
        userNameRecipientProfileTextView.setText(intent.getStringExtra("recipientUserName"));
        imagesInProfileArrayList = new ArrayList<>();
        Collections.reverse(urisList);
        for (int i = 0; i<urisList.size(); i++){
            imagesInProfileArrayList.add(new ImagesInProfile(urisList.get(i)));
//            Toast.makeText(getApplicationContext(), "" + urisList.get(i), Toast.LENGTH_SHORT).show();
            Log.d("imagesLog", "img: " + urisList.get(i));
        }
//        ImagesInProfile imagesInProfile = new ImagesInProfile("https://firebasestorage.googleapis.com/v0/b/mcgmessenger-26c67.appspot.com/o/chat_images%2Fimage%3A4899?alt=media&token=db19972a-b5d7-4280-bf2a-fe65dc0a11d9", "https://firebasestorage.googleapis.com/v0/b/mcgmessenger-26c67.appspot.com/o/chat_images%2Fimage%3A4899?alt=media&token=db19972a-b5d7-4280-bf2a-fe65dc0a11d9");
//
//        imagesInProfileArrayList.add(imagesInProfile);

        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);

//        r.addItemDecoration(new DividerItemDecoration(
//                r.getContext(), DividerItemDecoration.HORIZONTAL));
        r.setHasFixedSize(true);
        r.setLayoutManager(layoutManager);
        adapter = new ImageAdapter( imagesInProfileArrayList);
        r.setAdapter(adapter);

        adapter.setOnImageClickListener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int position) {
                Intent intent = new Intent(RecipientProfileActivity.this, PhotoActivity.class);
                intent.putExtra("photoUrl", imagesInProfileArrayList.get(position).getImageUrl());
                startActivity(intent);
            }
        });
    }
}