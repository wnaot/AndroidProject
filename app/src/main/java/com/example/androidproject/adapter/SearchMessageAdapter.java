package com.example.androidproject.adapter;

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

import com.example.androidproject.activity.MessageBox;
import com.example.androidproject.model.User;
import com.example.androidproject.R;
import com.example.androidproject.utils.FirebaseUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchMessageAdapter extends RecyclerView.Adapter<SearchMessageAdapter.ViewHolder>{
    private List<User> listItemUser;

    Context context;


    public SearchMessageAdapter(List<User> listItemUser, Context context) {
        this.listItemUser = listItemUser;
        this.context = context;

    }

    public void setData(List<User> listItemUser) {
        this.listItemUser = listItemUser;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User itemUser = listItemUser.get(position);
        if(itemUser == null) {
            return;
        }
        
        Picasso.get().load(itemUser.getProfilePicture()).into(holder.imgAvatar);
        holder.txtName.setText(itemUser.getUserName());

        int index_user = position;
        String messChat = listItemUser.get(position).getChat().getMessageText();
        String newMess = messChat;
        if(messChat.length() > 25) {
            newMess  = messChat.substring(0,15) + "...";
        }
        if(itemUser.getChat().getSenderID().equals(FirebaseUtil.currentUserId())){
            holder.txtChat.setText("Bạn: "+newMess);
        }
        else{
            holder.txtChat.setText(newMess);
        }
        String dateTimeMess = listItemUser.get(position).getChat().getTime();

        String[] parts = dateTimeMess.split(" ");
        String dateLastMess = parts[0];
        String timeLastMess = parts[1];
        holder.txtTime.setText(dateLastMess);
        holder.txtTimeMess.setText(timeLastMess);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessageBox.class);
                intent.putExtra("FriendID", listItemUser.get(index_user).getUserId());
                v.getContext().startActivity(intent);
            }
        });
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private TextView txtName;
        private TextView txtChat;
        private TextView txtTime;

        private TextView txtTimeMess;

        private RelativeLayout layoutMessage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.item_imgView);
            txtName = itemView.findViewById(R.id.item_name);
            txtChat = itemView.findViewById(R.id.item_desc);
            txtTime = itemView.findViewById(R.id.item_time);
            txtTimeMess = itemView.findViewById(R.id.time_mess);
            layoutMessage = itemView.findViewById(R.id.layoutMessage);
        }
    }

    @Override
    public int getItemCount() {
        if(listItemUser != null) {
            return listItemUser.size();
        }
        return 0;
    }
}
