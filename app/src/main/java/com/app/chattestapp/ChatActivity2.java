package com.app.chattestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity2 extends AppCompatActivity implements MessagesAdapter.UpdateChatMessageCallback {

    @BindView(R.id.messageArea)
    EditText messageAreaEditText;
    @BindView(R.id.messagesRecyclerView)
    RecyclerView messagesRecyclerView;

    private User currentUser;
    private User chatWithUser;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference chatsRef = firebaseDatabase.getReference("chats");
    private String chatNameUser1;
    private String chatNameUser2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        ButterKnife.bind(this);

        currentUser = getIntent().getParcelableExtra("USER1");
        chatWithUser = getIntent().getParcelableExtra("USER2");

        List<ChatMessage2> chatMessages = new ArrayList<>();
        MessagesAdapter messagesAdapter = new MessagesAdapter(chatMessages, this, currentUser, chatWithUser);
        messagesRecyclerView.setAdapter(messagesAdapter);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //create chat node for the two users
        setupChatInFirebase();
        //retrieve messages
        chatsRef.child(chatNameUser1).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage2 chatMessage = snapshot.getValue(ChatMessage2.class);
                chatMessages.add(chatMessage);
                messagesAdapter.notifyDataSetChanged();
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

    private void setupChatInFirebase() {
        //between current user and selected user
        Chat chat = new Chat();
        chat.setUser1(currentUser.getId());
        chat.setUser2(chatWithUser.getId());
        chatNameUser1 = "chat_" + currentUser.getUsername() + "_" + chatWithUser.getUsername();
        chatNameUser2 = "chat_" + chatWithUser.getUsername() + "_" + currentUser.getUsername();
        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(chatNameUser1)) {
                    chatsRef.child(chatNameUser1).setValue(chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @OnClick(R.id.sendButton)
    public void onSendClicked() {
        String message = messageAreaEditText.getText().toString();
        //create object with the message
        ChatMessage2 chatMsg = new ChatMessage2(message, currentUser.getUsername(), chatWithUser.getUsername());
        //add the message to the realtime database
        String msgId = chatsRef.child(chatNameUser1).child("messages").push().getKey();
        chatMsg.setId(msgId);
        chatsRef.child(chatNameUser1).child("messages").child(msgId).setValue(chatMsg);
        chatsRef.child(chatNameUser2).child("messages").child(msgId).setValue(chatMsg);
        messageAreaEditText.setText("");
    }

    @Override
    public void updateChatMessage(ChatMessage2 chatMessage) {
        chatsRef.child(chatNameUser1).child("messages").child(chatMessage.getId()).setValue(chatMessage);
        chatsRef.child(chatNameUser2).child("messages").child(chatMessage.getId()).setValue(chatMessage);
    }
}