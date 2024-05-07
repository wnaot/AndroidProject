package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Model.User;
import com.squareup.picasso.Picasso;


public class ListFriendAdapter extends RecyclerView.Adapter<ListFriendAdapter.ViewHolder> {

    private List<User> listUser;
    private Context context;

    public ListFriendAdapter() {
    }

    public ListFriendAdapter(List<User> listUser, Context context) {
        this.listUser = listUser;
        this.context = context;

    }

    @NonNull
    @Override
    public ListFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listfriend, parent, false);
        return new ListFriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int index_user = position;

        User user = listUser.get(position);
        if (user.getStatus().equals("online")) {
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        }
        else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }
        Picasso.get().load(user.getProfilePicture()).into(holder.image);
        holder.itemName.setText(user.getUserName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(), listUser.get(index_user).getUserId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), MessageBox.class);
                intent.putExtra("FriendID", listUser.get(index_user).getUserId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView itemName;
        ImageView img_on;
        ImageView img_off;
        ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.item_imgViewLFriend);
            itemName = itemView.findViewById(R.id.item_nameLFriend);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }
}

