package com.yunitski.msg.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yunitski.msg.R;

public class RecipientProfileActivity extends AppCompatActivity {

    TextView userNameRecipientProfileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_profile);
        userNameRecipientProfileTextView = findViewById(R.id.userNameRecipientProfileTextView);
        Intent intent = getIntent();
        userNameRecipientProfileTextView.setText(intent.getStringExtra("recipientUserName"));
    }
}