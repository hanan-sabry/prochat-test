package com.app.chattestapp;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String id;
    private String email;
    private String username;
    private String password;
    private boolean available;
    private boolean available_limit;
    private long available_from;
    private long available_to;

    public User() {
    }

    public User(String email, String username, String password, boolean available) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.available = available;
    }


    protected User(Parcel in) {
        id = in.readString();
        email = in.readString();
        username = in.readString();
        password = in.readString();
        available = in.readByte() != 0;
        available_limit = in.readByte() != 0;
        available_from = in.readLong();
        available_to = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable_limit() {
        return available_limit;
    }

    public void setAvailable_limit(boolean available_limit) {
        this.available_limit = available_limit;
    }

    public long getAvailable_from() {
        return available_from;
    }

    public void setAvailable_from(long available_from) {
        this.available_from = available_from;
    }

    public long getAvailable_to() {
        return available_to;
    }

    public void setAvailable_to(long available_to) {
        this.available_to = available_to;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeByte((byte) (available_limit ? 1 : 0));
        dest.writeLong(available_from);
        dest.writeLong(available_to);
    }

}
