package com.yunitski.msg.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.yunitski.msg.data.MSGmessage;
import com.yunitski.msg.data.User;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference profileImagesStorageReference;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListener;
    private FirebaseDatabase database;
    private String userName;
    private int avatarMockupResource;

    private ImageView profileImageView;
    private TextView userNameTextView;
    private ImageButton editUserNameImageButton;
    private static final int RC_IMAGE_PICKER = 1238;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        usersDatabaseReference = database.getReference().child("users");
        profileImagesStorageReference = storage.getReference().child("profile_images");

        profileImageView = findViewById(R.id.profileImageView);
        userNameTextView = findViewById(R.id.nameTextView);
        editUserNameImageButton = findViewById(R.id.editNameImageButton);
        profileImageView.setOnClickListener(this);

        if (usersChildEventListener == null) {
            usersChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User user = snapshot.getValue(User.class);
                    if (user.getId().equals(auth.getCurrentUser().getUid())) {
                        userNameTextView.setText(user.getName());
//                        Glide.with(profileImageView).load(user.getAvatarMockupResource()).into(profileImageView);
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
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_IMAGE_PICKER);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK){
//            Uri selectedImageUri = data.getData();
//            final StorageReference imageReference = profileImagesStorageReference.child(selectedImageUri.getLastPathSegment());
//            UploadTask uploadTask = imageReference.putFile(selectedImageUri);
//
//            uploadTask = imageReference.putFile(selectedImageUri);
//
//            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    // Continue with the task to get the download URL
//                    return imageReference.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()) {
//                        Uri downloadUri = task.getResult();
//                        usersChildEventListener = new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                                User user = snapshot.getValue(User.class);
//                                if (user.getId().equals(auth.getCurrentUser().getUid())){
//                                    userNameTextView.setText(user.getName());
//                                    user.setProfilePhotoUrl(downloadUri.toString());
//                                    Glide.with(profileImageView.getContext()).load(user.getProfilePhotoUrl()).into(profileImageView);
//
//                                }
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        };
//                        usersDatabaseReference.addChildEventListener(usersChildEventListener);
////                        MSGmessage gmessage = new MSGmessage();
////                        gmessage.setImageUrl(downloadUri.toString());
////                        gmessage.setName(userName);
////                        gmessage.setSender(auth.getCurrentUser().getUid());
////                        gmessage.setRecipient(recipientUserId);
////                        gmessage.setTime(currentDate());
////                        messagesDatabaseReference.push().setValue(gmessage);
////                        messageListView.setSelection(adapter.getCount() - 1);
//                    } else {
//                        // Handle failures
//                        // ...
//                    }
//                }
//            });
//        }
//    }
}