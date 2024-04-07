package com.example.androidproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    private List<User> listItemUser;
    Context context;
    public void setData(List<User> listItemUser) {
        this.listItemUser = listItemUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User itemUser = listItemUser.get(position);
        if(itemUser == null) {
            return;
        }
        String fullName = itemUser.getUserName();
        String[] parts = fullName.split(" ");
        String firstName = parts[0]; // Lấy phần đầu tiên của tên
        Picasso.get().load(itemUser.getProfilePicture()).into(holder.imgAvatar);
        holder.txtName.setText(firstName);
    }

    @Override
    public int getItemCount() {
        if(listItemUser != null) {
            return listItemUser.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private TextView txtName;

        public ViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.item_imgView1);
            txtName = itemView.findViewById(R.id.TextViewName1);
        }
    }
}
