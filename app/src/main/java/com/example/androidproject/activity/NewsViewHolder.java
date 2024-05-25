package com.example.androidproject.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;

public class NewsViewHolder extends RecyclerView.ViewHolder {

    ImageView imageViewNews;
    TextView textViewTitle, textViewDesc;

    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);

        imageViewNews = (ImageView) itemView.findViewById(R.id.imgNews);
        textViewTitle = (TextView) itemView.findViewById(R.id.tv_title_news);
        textViewDesc = (TextView) itemView.findViewById(R.id.tv_desc_news);
    }
}
