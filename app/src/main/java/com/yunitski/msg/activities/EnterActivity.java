package com.yunitski.msg.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yunitski.msg.R;
import com.yunitski.msg.data.User;

public class

EnterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText;
    private TextView registrationTextView, enterTextView;
    private Button applyButton;
    public static final String TAG = "EnterActivity";
    private boolean isLogInModeActive;
    private boolean isEnterSuccess;
    SharedPreferences sharedPreferences;
    public static final String FILE_IS_ENTER = "isEntered";
    FirebaseDatabase database;
    DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child("users");
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        registrationTextView = findViewById(R.id.registrationTextView);
        enterTextView = findViewById(R.id.enterTextView);
        applyButton = findViewById(R.id.applyButton);
        applyButton.setOnClickListener(this);
        registrationTextView.setOnClickListener(this);
        enterTextView.setOnClickListener(this);
        registrationTextView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_white));
        enterTextView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_gray));
        isLogInModeActive = false;
        isEnterSuccess = false;
        if (auth.getCurrentUser() != null){
            startActivity(new Intent(EnterActivity.this, UserListActivity.class));
            EnterActivity.this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.applyButton){
            logInSignUpUser(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
        } else if (v.getId() == R.id.registrationTextView){
                registrationTextView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_white));
                enterTextView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_gray));
                applyButton.setText("??????????????????????");
                confirmPasswordEditText.setVisibility(View.VISIBLE);
                isLogInModeActive = false;
        } else if (v.getId() == R.id.enterTextView){

            registrationTextView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_gray));
            enterTextView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_white));
            applyButton.setText("????????");
            confirmPasswordEditText.setVisibility(View.GONE);
            isLogInModeActive = true;
        }

    }

    private void logInSignUpUser(String email, String password) {

        if (isLogInModeActive){
            if (passwordEditText.getText().toString().trim().length() < 7){
                Toast.makeText(this, "?????????? ???????????? ???????????? 7", Toast.LENGTH_SHORT).show();
            } else if (emailEditText.getText().toString().trim().equals("")){
                Toast.makeText(this, "?????????????? Email", Toast.LENGTH_SHORT).show();
            } else {

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    //updateUI(user);
                                    Intent intent = new Intent(EnterActivity.this, UserListActivity.class);
                                    intent.putExtra("userName", nameEditText.getText().toString().trim());
                                    startActivity(intent);
                                    EnterActivity.this.finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(EnterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            }
        } else {
            if (!passwordEditText.getText().toString().trim().equals(confirmPasswordEditText.getText().toString().trim())){
                Toast.makeText(this, "???????????? ???? ??????????????????", Toast.LENGTH_SHORT).show();
            } else if (passwordEditText.getText().toString().trim().length() < 7){
                Toast.makeText(this, "?????????? ???????????? ???????????? 7", Toast.LENGTH_SHORT).show();
            } else if (emailEditText.getText().toString().trim().equals("")){
                Toast.makeText(this, "?????????????? Email", Toast.LENGTH_SHORT).show();
            } else {

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    createUser(user);
                                    //updateUI(user);
                                    Intent intent = new Intent(EnterActivity.this, UserListActivity.class);
                                    intent.putExtra("userName", nameEditText.getText().toString().trim());
                                    startActivity(intent);
                                    EnterActivity.this.finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(EnterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            }
        }
    }

    private void createUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(nameEditText.getText().toString().trim());
        user.setPassword(passwordEditText.getText().toString().trim());
        user.setProfilePhotoUrl("https://firebasestorage.googleapis.com/v0/b/mcgmessenger-26c67.appspot.com/o/profile_images%2Fperson_image.png?alt=media&token=132f8b24-741d-461c-9cb8-d7cc48d8c5fa");

        usersDatabaseReference.push().setValue(user);
    }
}