package com.example.androidproject.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;

import java.util.List;

//public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
//    private List<String> messageList;
//    public MessageAdapter(List<String> messageList) {
//        this.messageList = messageList;
//    }
//
//    @NonNull
//    @Override
//    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent,false);
//        return new MessageViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
//        String message = messageList.get(position);
//        holder.textMessage.setText(message);
//    }
//
//    @Override
//    public int getItemCount() {
//        return messageList.size();
//    }
//
//    public static class MessageViewHolder extends RecyclerView.ViewHolder {
//        TextView textMessage;
//
//        public MessageViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textMessage = itemView.findViewById(R.id.text_message);
//        }
//    }
//}
