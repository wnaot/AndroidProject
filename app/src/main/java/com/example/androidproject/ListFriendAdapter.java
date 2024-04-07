package com.example.androidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class ListFriendAdapter extends RecyclerView.Adapter<ListFriendAdapter.ViewHolder> {

    private List<User> listUser;


    // data is passed into the constructor
    public void setData(List<User> listUser) {
        this.listUser = listUser;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ListFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listfriend, parent, false);
        return new ListFriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = listUser.get(position);
        if(user == null) {
            return;
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
        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_imgViewLFriend);
            itemName = itemView.findViewById(R.id.item_nameLFriend);

        }
    }
}

