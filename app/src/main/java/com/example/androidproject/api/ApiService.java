package com.example.androidproject.api;

import com.example.androidproject.model.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    // https://newsdata.io/api/1/news?country=vi&apikey=pub_4170662dd01ae3d32c0994392800bac80eec9

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://newsdata.io/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("api/1/news")
    Call<ApiResponse> callNews(@Query("country") String country, @Query("apikey") String apikey);
}
