package com.yunitski.msg.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

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
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Toolbar toolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");
        chatImagesStorageReference = storage.getReference().child("chat_images");



        Intent intent = getIntent();
        if (intent != null){
            userName = intent.getStringExtra("userName");
        } else {
            userName = "Default User";
        }

        messageListView = findViewById(R.id.mainActivityListView);
        sendMessageImageButton = findViewById(R.id.sendMessageImageButton);
        addContentImageButton = findViewById(R.id.addContentImageButton);
        messageEditText = findViewById(R.id.messageEditText);
        List<MSGmessage> gmessages = new ArrayList<>();
        adapter = new MSGAdapter(this, R.layout.message_item, gmessages);
        messageListView.setAdapter(adapter);
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
                MSGmessage gmessage = snapshot.getValue(MSGmessage.class);
                adapter.add(gmessage);
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendMessageImageButton){
            MSGmessage gmessage = new MSGmessage();
            gmessage.setText(messageEditText.getText().toString());
            gmessage.setName(userName);
            gmessage.setImageUrl(null);

            messagesDatabaseReference.push().setValue(gmessage);
            messageEditText.setText("");
        } else if (v.getId() == R.id.addContentImageButton){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_IMAGE_PICKER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, EnterActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            StorageReference imageReference = chatImagesStorageReference.child(selectedImageUri.getLastPathSegment());
            UploadTask uploadTask = imageReference.putFile(selectedImageUri);
        }
    }
}
