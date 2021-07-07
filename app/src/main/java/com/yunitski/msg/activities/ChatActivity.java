package com.yunitski.msg.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import static com.yunitski.msg.activities.UserListActivity.CHANNEL_ID;
import static com.yunitski.msg.activities.UserListActivity.NOTIFICATION_ID;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView messageListView;
    private MSGAdapter adapter;
    private ImageButton sendMessageImageButton;
    private EditText messageEditText;
    private String userName;
    FirebaseDatabase database;
    DatabaseReference messagesDatabaseReference;
    ChildEventListener messagesChildEventListener;
    DatabaseReference usersDatabaseReference;
    ChildEventListener usersChildEventListener;
    private static final int RC_IMAGE_PICKER = 123;
    private static final int RC_VIDEO_PICKER = 128;
    private static final int RC_FILE_PICKER = 1283;
    private static final int RC_AUDIO_PICKER = 109;
    private StorageReference chatImagesStorageReference, chatVideosStorageReference, chatAudioStorageReference, chatFileStorageReference;

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

    ArrayList<String> urisList, videoUrisList, audioUrisList, audioNameList, fileUrisList, fileNameList;

    AlertDialog.Builder builder;
    AlertDialog dialog;

    private MediaPlayer mediaPlayer;
    private boolean isPlay;
    public final static String ACTIVITY_INFO_FILE = "activityFile";
    public final static String ACTIVITY_KEY = "activityKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");
        chatImagesStorageReference = storage.getReference().child("chat_images");
        chatVideosStorageReference = storage.getReference().child("chat_videos");
        chatAudioStorageReference = storage.getReference().child("chat_audio");
        chatFileStorageReference = storage.getReference().child("chat_files");

        ActivityCompat.requestPermissions(ChatActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        Intent intent = getIntent();
        if (intent != null){
            userName = intent.getStringExtra("userName");
            recipientUserId = intent.getStringExtra("recipientUserId");
            recipientUserName = intent.getStringExtra("recipientUserName");
            recipientUserAvatar = intent.getStringExtra("recipientUserAvatar");
        }
        setTitle("");


        urisList = new ArrayList<>();
        videoUrisList = new ArrayList<>();
        audioUrisList = new ArrayList<>();
        audioNameList = new ArrayList<>();
        fileUrisList = new ArrayList<>();
        fileNameList = new ArrayList<>();
        msGmessageArrayList = new ArrayList<>();
        messageListView = findViewById(R.id.mainActivityListView);
        sendMessageImageButton = findViewById(R.id.sendMessageImageButton);
        ImageButton addContentImageButton = findViewById(R.id.addContentImageButton);
        messageEditText = findViewById(R.id.messageEditText);
        chatItemLinearLayout = findViewById(R.id.chatItemLinearLayout);
        floatingActionButton = findViewById(R.id.fabBottom);
        floatingActionButton.setOnClickListener(this);
        userTitleTextView = findViewById(R.id.userTitleTextView);
        profilePhotoImageView = findViewById(R.id.profilePhotoImageView);
        LinearLayout titleLinearLayout = findViewById(R.id.titleLinearLayout);
        titleLinearLayout.setOnClickListener(this);

        gmessages = new ArrayList<>();
        adapter = new MSGAdapter(this, R.layout.message_item, gmessages);
        messageListView.setAdapter(adapter);
        isPlay = false;

        messageListView.setStackFromBottom(true);
        adapter.notifyDataSetChanged();
        adapter.setOnPhotoClickListener(position -> {
            if (msGmessageArrayList.get(position).getImageUrl() != null && msGmessageArrayList.get(position).getAudioUrl() == null) {
                Intent intent12 = new Intent(ChatActivity.this, PhotoActivity.class);
                intent12.putExtra("photoUrl", msGmessageArrayList.get(position).getImageUrl());
                startActivity(intent12);
            } else if (msGmessageArrayList.get(position).getVideoUrl() != null  && msGmessageArrayList.get(position).getAudioUrl() == null){
                Intent intent1 = new Intent(ChatActivity.this, VideoActivity.class);
                intent1.putExtra("videoUrl", msGmessageArrayList.get(position).getVideoUrl());
                startActivity(intent1);
            } else if (msGmessageArrayList.get(position).getFileUrl() != null){
                Intent intent13 = new Intent(Intent.ACTION_VIEW);
                intent13.setDataAndType(Uri.parse(msGmessageArrayList.get(position).getFileUrl()), "application/*");
                intent13.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent13);
            }
        });
        adapter.setOnAudioClickListener((position, view) -> {
                            if (!isPlay){
                                mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setDataSource(ChatActivity.this, Uri.parse(msGmessageArrayList.get(position).getAudioUrl()));
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
                assert user != null;
                if (user.getId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MSGmessage message = snapshot.getValue(MSGmessage.class);
                assert message != null;
                if (message.getSender().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                        && message.getRecipient().equals(recipientUserId)) {
                    message.setMine(true);
                    message.setName(userName);
                    message.setPusId(snapshot.getKey());
                    msGmessageArrayList.add(message);
                    adapter.add(message);
                    if (message.getImageUrl() != null){
                        urisList.add(message.getImageUrl());
                    } else if (message.getVideoUrl() != null){
                        videoUrisList.add(message.getVideoUrl());
                    } else if (message.getAudioUrl() != null){
                        audioUrisList.add(message.getAudioUrl());
                        audioNameList.add(message.getAudioName());
                    } else if (message.getFileUrl() != null){
                        fileUrisList.add(message.getFileUrl());
                        fileNameList.add(message.getFileName());
                    }
                } else if (message.getRecipient().equals(auth.getCurrentUser().getUid())
                        && message.getSender().equals(recipientUserId) && !message.isDeleted()) {
                    message.setMine(false);
                    message.setPusId(snapshot.getKey());

                    msGmessageArrayList.add(message);
                    adapter.add(message);
                    if (message.getImageUrl() != null) {
                        urisList.add(message.getImageUrl());
                    } else if (message.getVideoUrl() != null) {
                        videoUrisList.add(message.getVideoUrl());
                    } else if (message.getAudioUrl() != null) {
                        audioUrisList.add(message.getAudioUrl());
                        audioNameList.add(message.getAudioName());
                    } else if (message.getFileUrl() != null){
                        fileUrisList.add(message.getFileUrl());
                        fileNameList.add(message.getFileName());
                    }
                    if (!message.getSender().equals(auth.getCurrentUser().getUid())
                            && !message.getRecipient().equals(recipientUserId) && message.getRecipient().equals(auth.getCurrentUser().getUid())
                            && message.getSender().equals(recipientUserId) && !message.isDeleted()) {
//                        sharedPreferences = getSharedPreferences("isActive", Context.MODE_PRIVATE);
                        SharedPreferences sh = getSharedPreferences(ACTIVITY_INFO_FILE, Context.MODE_PRIVATE);
                        boolean isA = sh.getBoolean(ACTIVITY_KEY, true);
                        if (isA) {

                            if (!message.isRead()) {
                                MediaPlayer mp = MediaPlayer.create(ChatActivity.this, R.raw.unsure_566);
                                mp.start();
                            }
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference mDatabaseRef = database.getReference();
                            mDatabaseRef.child("messages").child(message.getPusId()).child("read").setValue(true);
                        } else {

                            createNotification();
                            addNotification(message.getText(), message.getName());
                        }
                    }

//                    if (!message.getSender().equals(auth.getCurrentUser().getUid())
//                            && !message.getRecipient().equals(recipientUserId) && message.getRecipient().equals(auth.getCurrentUser().getUid())
//                            && message.getSender().equals(recipientUserId) && !message.isDeleted()) {
//                        if (act){
//                            if (!message.isRead()) {
//                                MediaPlayer mp = MediaPlayer.create(ChatActivity.this, R.raw.unsure_566);
//                                mp.start();
//                            }
//                            FirebaseDatabase database = FirebaseDatabase.getInstance();
//                            DatabaseReference mDatabaseRef = database.getReference();
//                            mDatabaseRef.child("messages").child(message.getPusId()).child("read").setValue(true);
//                        } else {
//
////                            createNotification();
////                            addNotification(message.getText(), message.getName());
//                        }
//
//                        sharedPreferences = getSharedPreferences("isActive", Context.MODE_PRIVATE);
//                        boolean isA = sharedPreferences.getBoolean("isActive", true);
//                        if (isA) {
//
//
//                        }
//                    }
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences ss = getSharedPreferences(ACTIVITY_INFO_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ss.edit();
        editor.putBoolean(ACTIVITY_KEY, true);
        editor.apply();
        cancelNotification(getApplicationContext(), NOTIFICATION_ID);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences ss = getSharedPreferences(ACTIVITY_INFO_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ss.edit();
        editor.putBoolean(ACTIVITY_KEY, false);
        editor.apply();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
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
            gmessage.setSender(Objects.requireNonNull(auth.getCurrentUser()).getUid());
            gmessage.setRecipient(recipientUserId);
            gmessage.setImageUrl(null);
            gmessage.setTime(currentDate());
            messagesDatabaseReference.push().setValue(gmessage);
            messageEditText.setText("");
            messageEditText.requestFocus();
            MediaPlayer mp = MediaPlayer.create(ChatActivity.this, R.raw.intuition_561);
            mp.start();

        } else if (v.getId() == R.id.addContentImageButton){

            builder = new AlertDialog.Builder(ChatActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Медиа");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.choose_media_type_dialog, null);
            builder.setView(view);
            ImageButton photo = view.findViewById(R.id.photoImageButton);
            ImageButton video = view.findViewById(R.id.videoImageButton);
            ImageButton file = view.findViewById(R.id.fileImageButton);
            ImageButton audio = view.findViewById(R.id.audioImageButton);
            photo.setOnClickListener(v1 -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*"});
                startActivityForResult(intent, RC_IMAGE_PICKER);

            });
            video.setOnClickListener(v12 -> {

//                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.setType("video/*");
//                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"video/*"});

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, RC_VIDEO_PICKER);
            });
            file.setOnClickListener(v13 -> {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"application/*"});
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("application/*");
//                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, RC_FILE_PICKER);
            });
            audio.setOnClickListener(v14 -> {

//                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.setType("audio/*");
//                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"audio/*"});
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, RC_AUDIO_PICKER);
            });
            builder.setNegativeButton("отмена", (dialog, which) -> {

            });
            dialog = builder.create();
            dialog.show();
        } else if (v.getId() == R.id.fabBottom){
            messageListView.setSelection(adapter.getCount() - 1);
        } else if (v.getId() == R.id.titleLinearLayout){
            Intent intent = new Intent(ChatActivity.this, RecipientProfileActivity.class);
            intent.putExtra("recipientUserName", recipientUserName);
            intent.putExtra("imageUris", urisList);
            intent.putExtra("videosUris", videoUrisList);
            intent.putExtra("audioUris", audioUrisList);
            intent.putExtra("audioName", audioNameList);
            intent.putExtra("fileUris", fileUrisList);
            intent.putExtra("fileName", fileNameList);
            intent.putExtra("recipientUserAvatar", recipientUserAvatar);
            startActivity(intent);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK){
            assert data != null;
            Uri selectedImageUri = data.getData();
            final StorageReference imageReference = chatImagesStorageReference.child(selectedImageUri.getLastPathSegment());
            UploadTask uploadTask = imageReference.putFile(selectedImageUri);

            uploadTask = imageReference.putFile(selectedImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
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
                        assert downloadUri != null;
                        gmessage.setImageUrl(downloadUri.toString());
                        gmessage.setVideoUrl(null);
                        gmessage.setAudioUrl(null);
                        gmessage.setFileUrl(null);
                        gmessage.setName(userName);
                        gmessage.setSender(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                        gmessage.setRecipient(recipientUserId);
                        gmessage.setTime(currentDate());

                        messagesDatabaseReference.push().setValue(gmessage);
                        messageListView.setSelection(adapter.getCount() - 1);
                        MediaPlayer mp = MediaPlayer.create(ChatActivity.this, R.raw.intuition_561);
                        mp.start();
                    }  // Handle failures
                    // ...

                }
            });
            dialog.dismiss();
        } else if (requestCode == RC_VIDEO_PICKER && resultCode == RESULT_OK){
            assert data != null;
            Uri selectedVideoUri = data.getData();
            final StorageReference imageReference = chatVideosStorageReference.child(selectedVideoUri.getLastPathSegment());
            UploadTask uploadTask = imageReference.putFile(selectedVideoUri);

            uploadTask = imageReference.putFile(selectedVideoUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
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
                        assert downloadUri != null;
                        gmessage.setVideoUrl(downloadUri.toString());
                        gmessage.setImageUrl(null);
                        gmessage.setAudioUrl(null);
                        gmessage.setFileUrl(null);
                        gmessage.setName(userName);
                        gmessage.setSender(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                        gmessage.setRecipient(recipientUserId);
                        gmessage.setTime(currentDate());
                        messagesDatabaseReference.push().setValue(gmessage);
                        messageListView.setSelection(adapter.getCount() - 1);
                        MediaPlayer mp = MediaPlayer.create(ChatActivity.this, R.raw.intuition_561);
                        mp.start();
                    }  // Handle failures
                    // ...

                }
            });
            dialog.dismiss();
        }  else if (requestCode == RC_AUDIO_PICKER && resultCode == RESULT_OK){
            assert data != null;
            Uri selectedAudioUri = data.getData();
            String name = queryName(getContentResolver(), selectedAudioUri);
            final StorageReference imageReference = chatAudioStorageReference.child(selectedAudioUri.getLastPathSegment());
            UploadTask uploadTask = imageReference.putFile(selectedAudioUri);

            uploadTask = imageReference.putFile(selectedAudioUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
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
                        assert downloadUri != null;
                        gmessage.setAudioUrl(downloadUri.toString());
                        gmessage.setAudioName(name);
                        gmessage.setFileUrl(null);
                        gmessage.setVideoUrl(null);
                        gmessage.setImageUrl(null);
                        gmessage.setName(userName);
                        gmessage.setSender(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                        gmessage.setRecipient(recipientUserId);
                        gmessage.setTime(currentDate());
                        messagesDatabaseReference.push().setValue(gmessage);
                        messageListView.setSelection(adapter.getCount() - 1);
                        MediaPlayer mp = MediaPlayer.create(ChatActivity.this, R.raw.intuition_561);
                        mp.start();
//                        Toast.makeText(getApplicationContext(), "" + queryName(getContentResolver(), selectedAudioUri), Toast.LENGTH_SHORT).show();
                    }  // Handle failures
                    // ...

                }
            });
            dialog.dismiss();
        }  else if (requestCode == RC_FILE_PICKER && resultCode == RESULT_OK){
            assert data != null;
            Uri selectedFileUri = data.getData();
            String name = queryName(getContentResolver(), selectedFileUri);
            final StorageReference imageReference = chatFileStorageReference.child(selectedFileUri.getLastPathSegment());
            UploadTask uploadTask = imageReference.putFile(selectedFileUri);

            uploadTask = imageReference.putFile(selectedFileUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
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
                        assert downloadUri != null;
                        gmessage.setFileUrl(downloadUri.toString());
                        gmessage.setFileName(name);
                        gmessage.setAudioUrl(null);
                        gmessage.setVideoUrl(null);
                        gmessage.setImageUrl(null);
                        gmessage.setName(userName);
                        gmessage.setSender(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                        gmessage.setRecipient(recipientUserId);
                        gmessage.setTime(currentDate());
                        messagesDatabaseReference.push().setValue(gmessage);
                        messageListView.setSelection(adapter.getCount() - 1);
                        MediaPlayer mp = MediaPlayer.create(ChatActivity.this, R.raw.intuition_561);
                        mp.start();
//                        Toast.makeText(getApplicationContext(), "" + queryName(getContentResolver(), selectedAudioUri), Toast.LENGTH_SHORT).show();
                    }  // Handle failures
                    // ...

                }
            });
            dialog.dismiss();
        }
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotification(){
        CharSequence name = "New Message";
        String desc = "Message";
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(desc);
        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void addNotification(String message, String userName){
//
//        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(userName)
                .setContentText(message)
                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(notifyId);
    }
}
