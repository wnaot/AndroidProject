package com.example.androidproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private RecyclerView rcvChat;
    private View mView;
    private MainScreen mainActivity;
    private ItemUserAdapter itemUserAdapter;


    public ChatFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.chat_fragment, container, false);
        mainActivity = (MainScreen) getActivity();
        rcvChat = mView.findViewById(R.id.recycle_chat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
        rcvChat.setLayoutManager(linearLayoutManager);

        itemUserAdapter = new ItemUserAdapter();
        itemUserAdapter.setData(getListItemUser());

        rcvChat.setAdapter(itemUserAdapter);
        return mView;
    }

    private List<ItemUser> getListItemUser() {
        List<ItemUser> list = new ArrayList<>();
        list.add(new ItemUser(R.drawable.dog, "Minh Toan", "Đi ăn không?", "09/03"));
        list.add(new ItemUser(R.drawable.linda_nguyen, "Linda Nguyen", "Have good day!", "09/03"));
        list.add(new ItemUser(R.drawable.le_minh, "Le Minh", "Đúng rồi bạn", "09/03"));
        list.add(new ItemUser(R.drawable.tram_nguyen, "Nguyen Tram", "Chiều nay đi được không?", "08/03"));
        return list;
    }
}
