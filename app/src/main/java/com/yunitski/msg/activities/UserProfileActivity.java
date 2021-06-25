package com.yunitski.msg.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.yunitski.msg.data.MSGmessage;
import com.yunitski.msg.data.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference profileImagesStorageReference;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListener;
    private FirebaseDatabase database;

    private ImageView profileImageView;
    private TextView userNameTextView;
    private ImageButton editUserNameImageButton, backImageButton;
    private static final int RC_IMAGE_PICKER = 1238;

    private String pushId;

    private Button deleteAccountButton;
    private String userEmail, userPassword;


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
        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        backImageButton = findViewById(R.id.backImageButton);
        backImageButton.setOnClickListener(this);
        profileImageView.setOnClickListener(this);
        editUserNameImageButton.setOnClickListener(this);
        deleteAccountButton.setOnClickListener(this);

        updUI();

    }

    private void updUI(){
            usersChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User user = snapshot.getValue(User.class);
                    if (user.getId().equals(auth.getCurrentUser().getUid())) {
                        userNameTextView.setText(user.getName());
                        pushId = snapshot.getKey();
                        userEmail = user.getEmail();
                        userPassword = user.getPassword();
                        Glide.with(profileImageView).load(user.getProfilePhotoUrl()).into(profileImageView);
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

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.profileImageView){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_IMAGE_PICKER);
        } else if (v.getId() == R.id.editNameImageButton){
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Изменить имя пользователя");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.name_update_dialog, null);
            builder.setView(view);
            EditText nameUpdateEditText = view.findViewById(R.id.nameUpdateEditText);
            nameUpdateEditText.setText(userNameTextView.getText().toString());
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    FirebaseDatabase  database = FirebaseDatabase.getInstance();
                    DatabaseReference mDatabaseRef = database.getReference();

                    mDatabaseRef.child("users").child(pushId).child("name").setValue(nameUpdateEditText.getText().toString());
                    userNameTextView.setText(nameUpdateEditText.getText().toString());
                }
            });
            builder.setNegativeButton("отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        } else if (v.getId() == R.id.deleteAccountButton){
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Удалить Аккаунт?");
            builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(userEmail, userPassword);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                    }
                                }
                            });
                        }
                    });

                    FirebaseDatabase  database = FirebaseDatabase.getInstance();
                    DatabaseReference mDatabaseRef = database.getReference();

                    mDatabaseRef.child("users").child(pushId).removeValue();
//
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(UserProfileActivity.this, EnterActivity.class));
                    UserProfileActivity.this.finish();
                }
            });
            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        } else if (v.getId() == R.id.backImageButton){
            UserProfileActivity.this.finish();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            final StorageReference imageReference = profileImagesStorageReference.child(selectedImageUri.getLastPathSegment());
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

                        FirebaseDatabase  database = FirebaseDatabase.getInstance();
                        DatabaseReference mDatabaseRef = database.getReference();

                        assert downloadUri != null;
                        mDatabaseRef.child("users").child(pushId).child("profilePhotoUrl").setValue(downloadUri.toString());
                        Picasso.get().load(downloadUri.toString()).into(profileImageView);


                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }
}