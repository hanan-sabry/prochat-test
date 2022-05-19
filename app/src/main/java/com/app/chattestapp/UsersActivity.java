package com.app.chattestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersActivity extends AppCompatActivity {

    @BindView(R.id.usersList)
    ListView usersListView;
    @BindView(R.id.noUsersText)
    TextView noUsersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);

        setTitle("Welcome, " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("users");
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> usersUsername = new ArrayList<>();
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (!user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        usersUsername.add(user.getUsername());
                        users.add(user);
                    } else {
                        users.add(0, user);
                    }
                }
                if (usersUsername.isEmpty()) {
                    noUsersText.setText("No users found!");
                }
                ArrayAdapter<String> usersAdapter = new ArrayAdapter<String>(UsersActivity.this, android.R.layout.simple_list_item_1, usersUsername);
                usersListView.setAdapter(usersAdapter);
                usersListView.setOnItemClickListener((parent, view, position, id) -> {
                    User selectedUser = users.get(position+1);
                    Intent intent = new Intent(UsersActivity.this, ChatActivity.class);
                    intent.putExtra("USER2", selectedUser);
                    intent.putExtra("USER1", users.get(0));
                    startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UsersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        return true;
    }
}