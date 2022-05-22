package com.app.chattestapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class AvailableAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Available Alarm is set", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification("Alarm!", "You can receive your message now..");
        notificationHelper.getManager().notify(1, nb.build());

        //get current user
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getEmail().equals(email)) {
                        Map<String, Object> availableMap = new HashMap<>();
                        availableMap.put("available", true);
                        FirebaseDatabase.getInstance().getReference("users").child(userSnapshot.getKey())
                                .updateChildren(availableMap).addOnCompleteListener(task -> {
                            //                                cancelAlarm(context);
                            //check if there is not received messages, and send notifications with them
                            FirebaseDatabase.getInstance().getReference("chats").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot chatsSnapshot : snapshot.getChildren()) {
                                        if (chatsSnapshot.getKey().contains(user.getUsername())) {
                                            for (DataSnapshot userChatSnapshot : chatsSnapshot.getChildren()) {
                                                for (DataSnapshot messageSnapshot : userChatSnapshot.getChildren()){
                                                    ChatMessage2 chatMessage = messageSnapshot.getValue(ChatMessage2.class);
                                                    if (!chatMessage.isReceived()) {
                                                        NotificationHelper notificationHelper = new NotificationHelper(context);
                                                        NotificationCompat.Builder nb = notificationHelper.getChannelNotification
                                                                ("You have new messages", "check them now..");
                                                        notificationHelper.getManager().notify(1, nb.build());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AvailableAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }
}
