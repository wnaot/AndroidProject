package com.example.androidproject.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.model.Chat;
import com.example.androidproject.model.User;
import com.example.androidproject.adapter.HorizontalAdapter;
import com.example.androidproject.adapter.ItemUserAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class SearchMessage extends AppCompatActivity {

    private RecyclerView rcvChat;
    private RecyclerView hRecylerView;
    private SearchView sView;
    private View mView;
    private MainScreen mainActivity;
    private ItemUserAdapter itemUserAdapter;

    private HorizontalAdapter horizontalAdapter;

    private DatabaseReference mDatabase;
    private FirebaseUser mUser;

    private List<Chat> listChat;

    private ArrayList<User> listUserChat;
    private List<User> listFriend;
    private List<String> listIDFriendChat,listIDFriend;
    private TextView NoOnline;
    Chat lastChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);

        listUserChat = new ArrayList<>();
        listChat = new ArrayList<>();


    }
}
