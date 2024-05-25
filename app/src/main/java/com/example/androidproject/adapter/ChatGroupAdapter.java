package com.example.androidproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.model.MessageGroup;
import com.example.androidproject.R;
import com.example.androidproject.utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.MessageViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<MessageGroup> listGroupChat;

    public void setData(List<MessageGroup> listGroupChat) {
        this.listGroupChat = listGroupChat;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_left, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageGroup messageGroup = listGroupChat.get(position);

        holder.messageText.setText(messageGroup.getMessageText());

        if (getItemViewType(position) == VIEW_TYPE_RECEIVED) {
            FirebaseUtil.allUserDatabaseReference().child(messageGroup.getSenderID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String receiverAvatarURL = snapshot.child("profilePicture").getValue(String.class);
                    if (receiverAvatarURL != null && !receiverAvatarURL.isEmpty()) {
                        // Sử dụng Picasso để tải hình ảnh và đặt nó vào ImageView
                        Picasso.get().load(receiverAvatarURL).into(holder.avatarImage);
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu cần
                }
            });
        } else {
            // Nếu là người gửi, bạn có thể thực hiện các xử lý khác ở đây, hoặc để trống nếu không cần
        }
    }

    @Override
    public int getItemCount() {
        return listGroupChat == null ? 0 : listGroupChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageGroup messageGroup = listGroupChat.get(position);
        if (messageGroup.getSenderID().equals(FirebaseUtil.currentUserId())) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView avatarImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            avatarImage = itemView.findViewById(R.id.avatarImage);
        }
    }
}
