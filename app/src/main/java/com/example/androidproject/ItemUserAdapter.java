package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemUserAdapter extends RecyclerView.Adapter<ItemUserAdapter.ViewHolder>{
    private List<ItemUser> listItemUser;
    Context context;
    public void setData(List<ItemUser> listItemUser) {
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
        ItemUser itemUser = listItemUser.get(position);
        if(itemUser == null) {
            return;
        }

        holder.imgAvatar.setImageResource(itemUser.getItemImg());
        holder.txtName.setText(itemUser.getItemName());
        holder.txtChat.setText((itemUser.getItemChat()));
        holder.txtTime.setText(itemUser.getItemTime());

        holder.layoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessageBox.class);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private TextView txtName;
        private TextView txtChat;
        private TextView txtTime;

        private RelativeLayout layoutMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.item_imgView);
            txtName = itemView.findViewById(R.id.item_name);
            txtChat = itemView.findViewById(R.id.item_desc);
            txtTime = itemView.findViewById(R.id.item_time);
            layoutMessage = itemView.findViewById(R.id.layoutMessage);
        }
    }
}
