package com.app.chattestapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationRequest {

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAfE-zVi4:APA91bHmnwYWkEhbs0aeiSK_RH7rMwRWwiXbtt9aGy9Wo9auiOavYkLw-jRkgqQbfa2YJ_KsEyjnPJYP8bt2aWsNZaDvXivW-kO-h90eCZkVTGV44OmQyfz04I5XPvr3BKrSzYcYKJFq";
    final private String contentType = "application/json";
    static final String TAG = "NOTIFICATION TAG";

    private Context context;

    public NotificationRequest(Context context) {
        this.context = context;
    }

    public void sendNotificationRequest(String username, String message, String toToken) {
        String to = toToken; //topic must match with what the receiver subscribed to

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", "new message from: " + username);
            notificationBody.put("body", message);
            notification.put("to", to);
            notification.put("priority", "high");
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }
}
