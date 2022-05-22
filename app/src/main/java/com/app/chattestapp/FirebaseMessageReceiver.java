package com.app.chattestapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        //save token to database
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User currentUser = userSnapshot.getValue(User.class);
                        if (currentUser.getEmail().equals(currentUserEmail)) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("token", token);
                            usersRef.child(userSnapshot.getKey()).updateChildren(updates);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //get current user
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User currentUser = userSnapshot.getValue(User.class);
                        if (currentUser.getEmail().equals(currentUserEmail)) {
                            if (currentUser.isAvailable()) {
                                Map<String, String> data = remoteMessage.getData();
                                showNotification(data.get("title"), data.get("body  "));
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

//        if (remoteMessage.getNotification() != null) {
//            Map<String, String> data = remoteMessage.getData();
//            showNotification(data.get("title"), data.get("message"));
//        }
    }

    private RemoteViews getCustomDesign(String title, String message) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,
                android.R.drawable.stat_notify_chat);
        return remoteViews;
    }

    // Method to display the notifications
    public void showNotification(String title,
                                 String message) {
        // Pass the intent to switch to the MainActivity
        Intent intent = new Intent(this, UsersActivity.class);
        // Assign channel ID
        String channel_id = "notification_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getApplicationContext(), channel_id)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }
}
