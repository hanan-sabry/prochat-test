package com.app.chattestapp;

import java.util.Date;

public class ChatMessage2 {

    private String message;
    private String from;
    private String to;
    private boolean received;
    private long time;
    private String id;

    public ChatMessage2() {
    }

    public ChatMessage2(String message, String from, String to) {
        this.message = message;
        this.from = from;
        this.to = to;
        this.time = new Date().getTime();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
