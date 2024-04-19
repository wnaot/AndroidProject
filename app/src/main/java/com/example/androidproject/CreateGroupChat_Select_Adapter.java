package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CreateGroupChat_Select_Adapter extends RecyclerView.Adapter<CreateGroupChat_Select_Adapter.ViewHolder>{
    private List<User> listItemUser;
    Context context;
    private OnItemClickListener listener;

    public CreateGroupChat_Select_Adapter(List<User> listItemUser, Context context) {
        this.listItemUser = listItemUser;
        this.context = context;
    }

    // Constructor để truyền dữ liệu và giao diện lắng nghe sự kiện
    public CreateGroupChat_Select_Adapter(List<User> userList, Context context, OnItemClickListener listener) {
        this.listItemUser = userList;
        this.context = context;
        this.listener = listener;
    }
    public void setData(List<User> listItemUser) {
        this.listItemUser = listItemUser;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groupchat_userselect, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User itemUser = listItemUser.get(position);
        if(itemUser == null) {
            return;
        }
//        if (itemUser.getProfilePicture() != null && !itemUser.getProfilePicture().isEmpty()) {
//            Picasso.get().load(itemUser.getProfilePicture()).into(holder.imgAvatar);
//        } else {
////            Picasso.get().load(itemUser.getProfilePicture()).into(holder.imgAvatar);
//        }
//        Picasso.get().load(itemUser.getProfilePicture()).into(holder.imgAvatar);
        holder.txtName.setText(itemUser.getUserName());

        int index_user = position;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(itemUser);
//                holder.rdb_select.setChecked(true);
            }
        });


    }
    public interface OnItemClickListener {
        void onItemClick(User user);
    }


    @Override
    public int getItemCount() {
        if(listItemUser != null) {
            return listItemUser.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private TextView txtName;

        private RadioButton rdb_select;

        private RelativeLayout layout_creategroupchat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.creategroupchat_userSelect_imgView);
            txtName = itemView.findViewById(R.id.creategroupchat_select_user_name);
            rdb_select = itemView.findViewById(R.id.creategroupchat_radiobtn);
            layout_creategroupchat = itemView.findViewById(R.id.layout_creategroupchat_userselect);
        }
    }
}
