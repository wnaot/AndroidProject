package com.example.androidproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.model.ApiResponse;
import com.example.androidproject.model.Article;
import com.example.androidproject.adapter.NewsApdapter;
import com.example.androidproject.R;
import com.example.androidproject.api.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewFragment extends Fragment {

    RecyclerView recyclerView;

    List<Article> articleList;

    NewsApdapter newsApdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewNews);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        articleList = new ArrayList<>();

        ApiService.apiService.callNews("vi", "pub_4170662dd01ae3d32c0994392800bac80eec9").enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse apiResponse =response.body();
                if(apiResponse != null && "success".equals(apiResponse.getStatus())) {
                    articleList.addAll(apiResponse.getResults());
                    recyclerView.setAdapter(new NewsApdapter(getContext(), articleList));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Call API news failed", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
