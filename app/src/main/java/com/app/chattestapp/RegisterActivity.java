package com.app.chattestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText emailEditText;
    @BindView(R.id.username)
    EditText usernameEditText;
    @BindView(R.id.password)
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, UsersActivity.class));
        }
    }

    @OnClick(R.id.registerButton)
    public void onRegisterClicked() {
        String email = emailEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "You must enter username and password", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> tokenTask) {
                    String token = tokenTask.getResult();
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "User is created successfully", Toast.LENGTH_SHORT).show();
                                    //add display name for the user
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username).build();
                                    firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //add user to real-time database
                                            User user = new User(email, username, password, token, true);
                                            FirebaseDatabase.getInstance().getReference("users").push()
                                                    .setValue(user).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    startActivity(new Intent(RegisterActivity.this, UsersActivity.class));
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Can't create user, please try again", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Can't create user, please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
    }

    @OnClick(R.id.login)
    public void onLoginClicked() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}