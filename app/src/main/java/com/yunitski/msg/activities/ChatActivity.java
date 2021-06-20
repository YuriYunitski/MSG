package com.yunitski.msg.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yunitski.msg.R;
import com.yunitski.msg.adapters.MSGAdapter;
import com.yunitski.msg.data.MSGmessage;
import com.yunitski.msg.data.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView messageListView;
    private MSGAdapter adapter;
    private ImageButton sendMessageImageButton, addContentImageButton;
    private EditText messageEditText;
    private String userName;
    FirebaseDatabase database;
    DatabaseReference messagesDatabaseReference;
    ChildEventListener messagesChildEventListener;
    DatabaseReference usersDatabaseReference;
    ChildEventListener usersChildEventListener;
    private static final int RC_IMAGE_PICKER = 123;
    private FirebaseStorage storage;
    private StorageReference chatImagesStorageReference;

    private Toolbar toolbar;

    private String recipientUserId;

    private FirebaseAuth auth;

    List<MSGmessage> gmessages;
    LinearLayout chatItemLinearLayout;

    private String recipientUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");
        chatImagesStorageReference = storage.getReference().child("chat_images");


        Intent intent = getIntent();
        if (intent != null){
            userName = intent.getStringExtra("userName");
            recipientUserId = intent.getStringExtra("recipientUserId");
            recipientUserName = intent.getStringExtra("recipientUserName");
        }
        setTitle(recipientUserName);

        messageListView = findViewById(R.id.mainActivityListView);
        sendMessageImageButton = findViewById(R.id.sendMessageImageButton);
        addContentImageButton = findViewById(R.id.addContentImageButton);
        messageEditText = findViewById(R.id.messageEditText);
        chatItemLinearLayout = findViewById(R.id.chatItemLinearLayout);
        updateUI();

//        messageListView.setStackFromBottom(true);
//        adapter.notifyDataSetChanged();

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendMessageImageButton.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
        sendMessageImageButton.setOnClickListener(this);
        addContentImageButton.setOnClickListener(this);
        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    userName = user.getName();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersDatabaseReference.addChildEventListener(usersChildEventListener);
        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MSGmessage message = snapshot.getValue(MSGmessage.class);
                if (message.getSender().equals(auth.getCurrentUser().getUid())
                        && message.getRecipient().equals(recipientUserId)) {
                    message.setMine(true);
                    message.setName(userName);
                    adapter.add(message);
                } else if (message.getRecipient().equals(auth.getCurrentUser().getUid())
                        && message.getSender().equals(recipientUserId)) {
                    message.setMine(false);
                    adapter.add(message);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        messagesDatabaseReference.addChildEventListener(messagesChildEventListener);
//        messageListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                if (totalItemCount > 0)
//                {
//                    int lastInScreen = firstVisibleItem + visibleItemCount;
//                    if(lastInScreen == totalItemCount)
//                    {
//
//                        //Notify the adapter about the data set change.
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        });
    }

    private void updateUI(){
        gmessages = new ArrayList<>();
        adapter = new MSGAdapter(this, R.layout.message_item, gmessages);
        messageListView.setAdapter(adapter);

        messageListView.setStackFromBottom(true);
        adapter.notifyDataSetChanged();
    }

    private String currentDate(){
        Calendar calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return "" + hour + ":" + minute;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendMessageImageButton){
            MSGmessage gmessage = new MSGmessage();
            gmessage.setText(messageEditText.getText().toString());
            gmessage.setName(userName);
            gmessage.setSender(auth.getCurrentUser().getUid());
            gmessage.setRecipient(recipientUserId);
            gmessage.setImageUrl(null);
            gmessage.setTime(currentDate());
            messagesDatabaseReference.push().setValue(gmessage);
            messageEditText.setText("");
            messageEditText.requestFocus();

        } else if (v.getId() == R.id.addContentImageButton){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_IMAGE_PICKER);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            final StorageReference imageReference = chatImagesStorageReference.child(selectedImageUri.getLastPathSegment());
            UploadTask uploadTask = imageReference.putFile(selectedImageUri);

            uploadTask = imageReference.putFile(selectedImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        MSGmessage gmessage = new MSGmessage();
                        gmessage.setImageUrl(downloadUri.toString());
                        gmessage.setName(userName);
                        gmessage.setSender(auth.getCurrentUser().getUid());
                        gmessage.setRecipient(recipientUserId);
                        gmessage.setTime(currentDate());
                        messagesDatabaseReference.push().setValue(gmessage);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
//        messageListView.setStackFromBottom(true);
//        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
    }
}
