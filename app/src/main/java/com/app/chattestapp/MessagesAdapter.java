package com.app.chattestapp;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<ChatMessage2> messagesList;
    private UpdateChatMessageCallback updateChatMessageCallback;
    private User currentUser;
    private User chatUser;

    public MessagesAdapter(List<ChatMessage2> messagesList, UpdateChatMessageCallback updateChatMessageCallback, User currentUser, User chatUser) {
        this.messagesList = messagesList;
        this.updateChatMessageCallback = updateChatMessageCallback;
        this.currentUser = currentUser;
        this.chatUser = chatUser;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage2 chatMessage2 = messagesList.get(position);
        //check if the sender is the current user
        //show all messages
        holder.fromTextView.setText(chatMessage2.getFrom());
        holder.msgTextView.setText(chatMessage2.getMessage());
        holder.timeTextView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", chatMessage2.getTime()));

        if (currentUser.getUsername().equals(chatMessage2.getFrom())) {
            holder.messageLayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_corner2));
        } else {
            holder.messageLayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_corner3));
        }
    }

    private void updateChatMessage(ChatMessage2 chatMessage) {
        updateChatMessageCallback.updateChatMessage(chatMessage);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fromTextView)
        TextView fromTextView;
        @BindView(R.id.msgTextView)
        TextView msgTextView;
        @BindView(R.id.timeTextView)
        TextView timeTextView;
        @BindView(R.id.messageLayout)
        LinearLayout messageLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    interface UpdateChatMessageCallback {
        void updateChatMessage(ChatMessage2 chatMessage);
    }
}
