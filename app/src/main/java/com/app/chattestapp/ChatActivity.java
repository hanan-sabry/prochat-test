package com.app.chattestapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.messageArea)
    EditText messageAreaEditText;
    @BindView(R.id.layout1)
    LinearLayout layout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    private User chatWithUser;
    private String currentUserName;
    DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
    private DatabaseReference user2Msg;
    private DatabaseReference currentUserMsgRef;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    ;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        messagesRef.removeValue();
        chatWithUser = getIntent().getParcelableExtra("USER2");
        currentUser = getIntent().getParcelableExtra("USER1");
        currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        currentUserMsgRef = messagesRef.child(currentUserName + "_" + chatWithUser.getUsername());
        user2Msg = messagesRef.child(chatWithUser.getUsername() + "_" + currentUserName);

        setTitle("Chat with: " + chatWithUser.getUsername());

//        setAlert();
//        sendNotification();
//        WorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(AttachListenerWorker.class).build();
//        WorkManager.getInstance(this).enqueue(uploadWorkRequest);

        currentUserMsgRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage msg = snapshot.getValue(ChatMessage.class);
                if (msg.getUser().equals(currentUserName)) {
                    addMessageBox("You:-\n" + msg.getText(), msg.getTime(), 1);
                    if (!chatWithUser.isAvailable()) {
                        addMessageBox(chatWithUser.getUsername() + " can't receive your messages now", msg.getTime(), 2);
                    }
                } else {
                    //if user is available
                    if (currentUser.isAvailable()) {
                        addMessageBox(chatWithUser.getUsername() + ":-\n" + msg.getText(), msg.getTime(), 2);
                    } else {
//                        addMessageBox("user isn't available now", msg.getTime(), 2);
//                        final Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                currentUserMsgRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
//                                            ChatMessage chatMessage = msgSnapshot.getValue(ChatMessage.class);
////                                            if (!chatMessage.getUser().equals(currentUser)) {
//                                                if (chatMessage.getTime() < Calendar.getInstance().getTimeInMillis()) {
//                                                    addMessageBox(chatWithUser.getUsername() + ":-\n" + chatMessage.getText(), chatMessage.getTime(), 2);
//                                                }
////                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                            }
//
//                        }, 30000);
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
        });

    }

    private void sendNotification() {
        // This registration token comes from the client FCM SDKs.
        String registrationToken = "YOUR_REGISTRATION_TOKEN";

// See documentation on defining a message payload.
//        Message message = Message.builder()
//                .putData("score", "850")
//                .putData("time", "2:45")
//                .setToken(registrationToken)
//                .build();

// Send a message to the device corresponding to the provided
// registration token.
//        String response = FirebaseMessaging.getInstance().send(message);
// Response is a message ID string.
//        System.out.println("Successfully sent message: " + response);

    }

    private void setAlert() {
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (10 * 1000), pendingIntent);
        Toast.makeText(this, "Alarm set in " + 10 + " seconds", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.sendButton)
    public void onSendButtonClicked() {
        String message = messageAreaEditText.getText().toString();
        if (!message.isEmpty()) {
            ChatMessage msg = new ChatMessage(message, currentUserName);
            currentUserMsgRef.push().setValue(msg);
            user2Msg.push().setValue(msg);
            messageAreaEditText.setText("");
        }
    }

    public void addMessageBox(String message, long time, int type) {
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);

        TextView timeView = new TextView(ChatActivity.this);
        timeView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", time));
        timeView.setTextSize(8);
        timeView.setLayoutParams(lp);

        if (type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        } else {
            textView.setBackgroundResource(R.drawable.rounded_corner3);
        }

        layout.addView(textView);
        layout.addView(timeView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}