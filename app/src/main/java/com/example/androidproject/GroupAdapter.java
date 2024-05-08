package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.Chat;
import com.example.androidproject.Model.GroupChat;
import com.example.androidproject.Model.MessageGroup;
import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.FirebaseUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>{
    private List<GroupChat> listGroupChat;
    Context context;



    public GroupAdapter(List<GroupChat> listGroupChat, Context context) {
        this.listGroupChat = listGroupChat;
        this.context = context;

    }

    public void setData(List<GroupChat> listGroupChat) {
        this.listGroupChat = listGroupChat;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groups, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupChat itemGroupChat = listGroupChat.get(position);
        if(itemGroupChat == null) {
            return;
        }
        List<MessageGroup> messageGroups = itemGroupChat.getMessageGroups();

        // Kiểm tra và sử dụng danh sách messageGroups
        if (messageGroups != null && !messageGroups.isEmpty()) {
            MessageGroup lastMessageGroup = messageGroups.get(messageGroups.size() - 1);
            String senderID = lastMessageGroup.getSenderID();
            String messageText = lastMessageGroup.getMessageText();
            String time = lastMessageGroup.getTime();

            if(messageText.equals(FirebaseUtil.currentUserId())){
                String newMess = senderID;
                if(newMess.length() > 25) {
                    newMess  = newMess.substring(0,15) + "...";
                }
                holder.txtChat.setText("Bạn: "+newMess);
            }
            else{
                FirebaseUtil.allUserDatabaseReference().child(messageText).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("userName").getValue(String.class);
                        String newMess = senderID;
                        if(newMess.length() > 25) {
                            newMess  = newMess.substring(0,15) + "...";
                        }
                        holder.txtChat.setText(name+": " + newMess);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            String[] parts = time.split(" ");
            String dateLastMess = parts[0];
            String timeLastMess = parts[1];
            holder.txtTime.setText(dateLastMess);
            holder.txtTimeMess.setText(timeLastMess);
        } else {
            holder.txtChat.setText("");
            holder.txtTime.setText("");
            holder.txtTimeMess.setText("");
        }

        holder.txtName.setText(itemGroupChat.getName());
        Picasso.get().load(itemGroupChat.getGroupchatPicture()).into(holder.imgAvatar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessageBoxGroups.class);
                intent.putExtra("groupChatId",itemGroupChat.getGroupChatId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listGroupChat != null) {
            return listGroupChat.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private TextView txtName;
        private TextView txtChat;
        private TextView txtTime;

        private TextView txtTimeMess;

        private RelativeLayout layoutMessage;
        private ImageView img_on;
        private ImageView img_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.item_imgView);
            txtName = itemView.findViewById(R.id.item_name_groups);
            txtChat = itemView.findViewById(R.id.item_desc);
            txtTime = itemView.findViewById(R.id.item_time);
            txtTimeMess = itemView.findViewById(R.id.time_mess);
            layoutMessage = itemView.findViewById(R.id.layoutMessage);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }
}
