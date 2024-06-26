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
import com.squareup.picasso.Picasso;

import java.util.List;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    private List<User> listItemUser;
    Context context;
    public HorizontalAdapter(List<User> listItemUser, Context context) {
        this.listItemUser = listItemUser;
        this.context = context;
    }
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

        holder.txtName.setText(itemUser.getUserName());
        Picasso.get().load(itemUser.getProfilePicture()).into(holder.imgAvatar);

        holder.layoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessageBox.class);
                intent.putExtra("FriendID",itemUser.getUserId());
                v.getContext().startActivity(intent);
            }
        });
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

        private RelativeLayout layoutMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.item_imgView1);
            txtName = itemView.findViewById(R.id.TextViewName1);
            layoutMessage  = itemView.findViewById(R.id.layoutMessage);
        }
    }
}
