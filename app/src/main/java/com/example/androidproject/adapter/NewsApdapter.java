package com.example.androidproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.model.Article;
import com.example.androidproject.activity.NewsViewHolder;
import com.example.androidproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsApdapter extends RecyclerView.Adapter<NewsViewHolder> {

    Context context;
    List<Article> articleList;

    String url;

    public NewsApdapter(Context context, List<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.textViewTitle.setText(articleList.get(position).getTitle());

        if(articleList.get(position).getDescription() == null) {
            holder.textViewDesc.setText("No description available");
        }
        else {
            holder.textViewDesc.setText(articleList.get(position).getDescription());
        }


        Picasso.get().load(articleList.get(position).getImage_url()).into(holder.imageViewNews);



        int index_news = position;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = articleList.get(index_news).getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
