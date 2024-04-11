package com.example.androidproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.FriendInvitation;
import com.example.androidproject.Utils.FirebaseUtil;
import com.example.androidproject.Utils.UserUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FriendInvitationAdapter extends RecyclerView.Adapter<FriendInvitationAdapter.ViewHolder> {

    private List<FriendInvitation> friendInvitationsList;
    public void setData(List<FriendInvitation> friendInvitationsList) {
        this.friendInvitationsList = friendInvitationsList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FriendInvitationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invitation, parent, false);
        return new FriendInvitationAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull FriendInvitationAdapter.ViewHolder holder, int position) {
        FriendInvitation item = friendInvitationsList.get(position);
        if(item == null) {
            return;
        }
        String senderId = item.getSenderId();
        if (senderId != null && !senderId.isEmpty()) {
            FirebaseUtil.allUserDatabaseReference().child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = UserUtil.getUserFromSnapshot(snapshot);
                        if (user != null) {
                            Picasso.get().load(user.getProfilePicture()).into(holder.imgAvatar);
                            holder.txtName.setText(user.getUserName());
                        }
                    } else {
                        holder.txtName.setText("Unknown User");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu có
                    Log.e("FriendInvitationAdapter", "Error: " + error.getMessage());
                }
            });
        }
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> friendData = new HashMap<>();
                friendData.put(item.getSenderId(), true);
                FirebaseUtil.acceptFriendInvitation().updateChildren(friendData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        notifyDataSetChanged();
                    }
                });
                Map<String, Object> friendOtherData = new HashMap<>();
                friendOtherData.put(FirebaseUtil.currentUserId(),true);
                FirebaseUtil.acceptOtherFriendInvitation(item.getSenderId()).updateChildren(friendOtherData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        notifyDataSetChanged();
                    }
                });

                Map<String, Object> updateStatus = new HashMap<>();
                updateStatus.put("status", "accepted");
                FirebaseUtil.currentFriendInvitation().child(item.getSenderId()).updateChildren(updateStatus, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        notifyDataSetChanged();
                    }
                });

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.currentFriendInvitation().child(item.getSenderId()).removeValue();
            }
        });
    }
    @Override
    public int getItemCount() {
        if (friendInvitationsList == null) {
            return 0;
        }
        return friendInvitationsList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private TextView txtName;
        private TextView btnAccept,btnDelete;
        private TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.item_imgViewAdd);
            txtName = itemView.findViewById(R.id.item_nameAdd);
            btnAccept = itemView.findViewById(R.id.btnAddUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
            time = itemView.findViewById(R.id.item_time);
        }
    }
}
