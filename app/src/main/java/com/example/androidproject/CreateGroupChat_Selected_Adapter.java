package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CreateGroupChat_Selected_Adapter extends RecyclerView.Adapter<CreateGroupChat_Selected_Adapter.ViewHolder>{
    private List<User> listItemUser;
    Context context;

    public CreateGroupChat_Selected_Adapter(List<User> listItemUser, Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groupchat_userselected, parent, false);
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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), CreateGroupChat.class);
                intent.putExtra("UserID", listItemUser.get(index_user).getUserId());
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

        private ImageView btn_delete;

        private RelativeLayout layout_creategroupchat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = (ImageView) itemView.findViewById(R.id.creategroupchat_userselected_imgView);
            txtName = itemView.findViewById(R.id.creategroupchat_userselected_name);
            btn_delete = (ImageView) itemView.findViewById(R.id.creategroupchat_userselected_btnDelete);
            layout_creategroupchat = itemView.findViewById(R.id.layout_creategroupchat_userselected);
        }
    }
}
