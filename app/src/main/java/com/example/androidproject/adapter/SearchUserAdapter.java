package com.example.androidproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidproject.model.User;
import com.example.androidproject.R;
import com.example.androidproject.utils.FirebaseUtil;
import com.example.androidproject.utils.UserUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {
    private List<User> listUser;

    public void setData(List<User> listUser) {
        this.listUser = listUser;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SearchUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_add, parent, false);
        return new SearchUserAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = listUser.get(position);
        if(user == null) {
            return;
        }

        Map<String, Boolean> friendList = user.getFriendList();
        boolean isFriend = false;
        for (Map.Entry<String, Boolean> entry : friendList.entrySet()) {
            String friendId = entry.getKey();
            Boolean isFriendValue = entry.getValue();
            if (friendId.equals(FirebaseUtil.currentUserId()) && isFriendValue) {
                isFriend = true;
                break;
            }
        }
        if (isFriend) {
            holder.btnAddUser.setText("Bạn bè");
            holder.btnAddUser.setEnabled(false);
        } else {
            FirebaseUtil.allFriendInvitation().child(user.getUserId()).child(FirebaseUtil.currentUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String status = snapshot.child("status").getValue(String.class);
                        if(status.equals("pending")){
                            holder.btnAddUser.setText("Hủy lời mời");
                            holder.btnAddUser.setEnabled(true);
                            holder.btnAddUser.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseUtil.allFriendInvitation().child(user.getUserId()).child(FirebaseUtil.currentUserId()).removeValue();
                                    holder.btnAddUser.setText("Thêm bạn bè");
                                    notifyDataSetChanged();
                                }
                            });
                        }
                        else{
                            holder.btnAddUser.setText("Thêm bạn bè");
                            holder.btnAddUser.setEnabled(true);
                            FirebaseUtil.addFriendInvitation(FirebaseUtil.currentUserId(), user.getUserId());
                            holder.btnAddUser.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseUtil.allFriendInvitation().child(user.getUserId()).child(FirebaseUtil.currentUserId()).removeValue();
                                    holder.btnAddUser.setText("Hủy lời mời");
                                    sendFriendInvitation(user.getFcmToken());
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    } else {
                        holder.btnAddUser.setText("Thêm bạn bè");
                        holder.btnAddUser.setEnabled(true);
                        holder.btnAddUser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseUtil.addFriendInvitation(FirebaseUtil.currentUserId(), user.getUserId());
                                holder.btnAddUser.setText("Hủy lời mời");
                                sendFriendInvitation(user.getFcmToken());
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        Picasso.get().load(user.getProfilePicture()).into(holder.image);
        holder.itemName.setText(user.getUserName());
    }

    @Override
    public int getItemCount() {
        if(listUser != null) {
            return listUser.size();
        }
        return 0;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView itemName;
        TextView btnAddUser;


        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_imgViewAdd);
            itemName = itemView.findViewById(R.id.item_nameAdd);
            btnAddUser = itemView.findViewById(R.id.btnAddUser);
        }
    }
    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAAikwt8ac:APA91bGG0Bya4SgXQtZ23p6EXIatM5n3cVnc57NYDWjZr6Nul_AXO5rn7cuzeGDFnXKjVfn9M_hGSElOWQ4bfcy1AJrOuh2o9KYdvG53yCoOXidwRXvNd1mHHG3DN77Nf83do6u_GNgz")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    void sendFriendInvitation(String otherToken){
        FirebaseUtil.currentUserDetails().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User currentUser = UserUtil.getUserFromSnapshot(snapshot);
                    try{
                        JSONObject jsonObject  = new JSONObject();

                        JSONObject notificationObj = new JSONObject();
                        notificationObj.put("title",currentUser.getUserName());
                        notificationObj.put("body",currentUser.getUserName() +" đã gửi lời mời kết bạn cho bạn");

                        JSONObject dataObj = new JSONObject();
                        dataObj.put("userId",currentUser.getUserId());

                        jsonObject.put("notification",notificationObj);
                        jsonObject.put("data",dataObj);

                        jsonObject.put("to",otherToken);

                        callApi(jsonObject);
                    }catch (Exception e) {
                    }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
