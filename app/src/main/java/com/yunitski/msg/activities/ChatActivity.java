package com.yunitski.msg.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yunitski.msg.R;
import com.yunitski.msg.adapters.MSGAdapter;
import com.yunitski.msg.data.MSGmessage;
import com.yunitski.msg.data.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
    private String recipientUserAvatar;
    private ArrayList<MSGmessage> msGmessageArrayList;

    FloatingActionButton floatingActionButton;
    private TextView userTitleTextView;
    private ImageView profilePhotoImageView;
    private LinearLayout titleLinearLayout;


    SharedPreferences sharedPreferences;
    ArrayList<String> urisList;

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
            recipientUserAvatar = intent.getStringExtra("recipientUserAvatar");
        }
        setTitle("");


        urisList = new ArrayList<>();
        msGmessageArrayList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("isActive", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isActive", true);
        editor.apply();



        messageListView = findViewById(R.id.mainActivityListView);
        sendMessageImageButton = findViewById(R.id.sendMessageImageButton);
        addContentImageButton = findViewById(R.id.addContentImageButton);
        messageEditText = findViewById(R.id.messageEditText);
        chatItemLinearLayout = findViewById(R.id.chatItemLinearLayout);
        floatingActionButton = findViewById(R.id.fabBottom);
        floatingActionButton.setOnClickListener(this);
        userTitleTextView = findViewById(R.id.userTitleTextView);
        profilePhotoImageView = findViewById(R.id.profilePhotoImageView);
        titleLinearLayout = findViewById(R.id.titleLinearLayout);
        titleLinearLayout.setOnClickListener(this);

        gmessages = new ArrayList<>();
        adapter = new MSGAdapter(this, R.layout.message_item, gmessages);
        messageListView.setAdapter(adapter);

        messageListView.setStackFromBottom(true);
        adapter.notifyDataSetChanged();
        adapter.setOnPhotoClickListener(new MSGAdapter.OnPhotoClickListener() {
            @Override
            public void onUserClick(int position) {
                Intent intent = new Intent(ChatActivity.this, PhotoActivity.class);
                intent.putExtra("photoUrl", msGmessageArrayList.get(position).getImageUrl());
                startActivity(intent);
            }
        });

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
                    Picasso.get().load(recipientUserAvatar).into(profilePhotoImageView);
                    userTitleTextView.setText(recipientUserName);

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
                    message.setPusId(snapshot.getKey());
                    msGmessageArrayList.add(message);
                    adapter.add(message);
                    if (message.getImageUrl() != null){
                        urisList.add(message.getImageUrl());
                    }
                } else if (message.getRecipient().equals(auth.getCurrentUser().getUid())
                        && message.getSender().equals(recipientUserId) && !message.isDeleted()) {
                    message.setMine(false);
                    message.setPusId(snapshot.getKey());

                    msGmessageArrayList.add(message);
                    adapter.add(message);
                    if (message.getImageUrl() != null){
                        urisList.add(message.getImageUrl());
                    }
                   }
                if (!message.getSender().equals(auth.getCurrentUser().getUid())
                        && !message.getRecipient().equals(recipientUserId) && message.getRecipient().equals(auth.getCurrentUser().getUid())
                        && message.getSender().equals(recipientUserId) && !message.isDeleted()){

                    sharedPreferences = getSharedPreferences("isActive", Context.MODE_PRIVATE);
                    boolean isA = sharedPreferences.getBoolean("isActive", true);
                    if (isA){
                        FirebaseDatabase  database = FirebaseDatabase.getInstance();
                        DatabaseReference mDatabaseRef = database.getReference();
                        mDatabaseRef.child("messages").child(msGmessageArrayList.get(msGmessageArrayList.size() - 1).getPusId()).child("read").setValue(true);
                    }
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
        messageListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < (totalItemCount - 13)) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        registerForContextMenu(messageListView);
    }



    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = getSharedPreferences("isActive", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isActive", false);
        editor.apply();
    }



    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int pos = adapter.getPositionList();

        FirebaseDatabase  database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = database.getReference();

        if (msGmessageArrayList.get(pos).isMine()){

            mDatabaseRef.child("messages").child(msGmessageArrayList.get(pos).getPusId()).removeValue();

        } else {

            mDatabaseRef.child("messages").child(msGmessageArrayList.get(pos).getPusId()).child("deleted").setValue(true);

        }
        msGmessageArrayList.remove(pos);
        adapter.clear();
        adapter.addAll(msGmessageArrayList);
        adapter.notifyDataSetChanged();

        return super.onContextItemSelected(item);

    }

    private String currentDate(){
        Calendar calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String m = "" + minute;
        if (minute < 10){
            m = "0" + minute;
        }
        return "" + hour + ":" + m;
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
        } else if (v.getId() == R.id.fabBottom){
            messageListView.setSelection(adapter.getCount() - 1);
        } else if (v.getId() == R.id.titleLinearLayout){
            Intent intent = new Intent(ChatActivity.this, RecipientProfileActivity.class);
            intent.putExtra("recipientUserName", recipientUserName);
            intent.putExtra("imageUris", urisList);
            startActivity(intent);
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
                        messageListView.setSelection(adapter.getCount() - 1);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
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
}
