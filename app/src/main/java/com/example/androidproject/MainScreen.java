package com.example.androidproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainScreen extends AppCompatActivity {
    private BottomNavigationView mbottomNavigationView;
    private ViewPager2 mviewPager;
    private ViewPager2 widgetMenu;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();


    DatabaseReference usersRef;
    private List<User> userList;
    List<User> friendList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);

        setContentView(R.layout.main_screen);

        ImageView imgView_menu = (ImageView) findViewById(R.id.btn_menu);
        ImageView imgView_edit = (ImageView) findViewById(R.id.btn_edit);
        ImageView imgView_search = (ImageView) findViewById(R.id.btn_search);
        mbottomNavigationView = findViewById(R.id.menu_bar);
        mviewPager = findViewById(R.id.view_pager);
        widgetMenu = findViewById(R.id.widgetMenu);

        fragmentArrayList.add(new ChatFragment());
        fragmentArrayList.add(new CallFragment());
        fragmentArrayList.add(ContactFragment.newInstance(userId));
        fragmentArrayList.add(new NewFragment());

        //new LoadFriendListTask().execute(userId);

        imgView_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, MenuMainScreen.class);
                startActivity(intent);
            }
        });

        imgView_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainScreen.this, "Edit", Toast.LENGTH_SHORT).show();
            }
        });
        imgView_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, SearchUser.class);
                startActivity(intent);
            }
        });
        mbottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_chats) {
//                    Toast.makeText(MainActivity.this, "Chats", Toast.LENGTH_SHORT).show();
                    mviewPager.setCurrentItem(0);
                }
                if (id == R.id.action_calls) {
//                    Toast.makeText(MainActivity.this, "Calls", Toast.LENGTH_SHORT).show();
                    mviewPager.setCurrentItem(1);
                }
                if (id == R.id.action_contacts) {
//                    Toast.makeText(MainActivity.this, "Contacts", Toast.LENGTH_SHORT).show();
                    mviewPager.setCurrentItem(2);
                }
                if (id == R.id.action_news) {
//                    Toast.makeText(MainActivity.this, "News", Toast.LENGTH_SHORT).show();
                    mviewPager.setCurrentItem(3);
                }
                return true;
            }
        });
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, fragmentArrayList);
        mviewPager.setAdapter(viewPagerAdapter);
        mviewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mbottomNavigationView.setSelectedItemId(R.id.action_chats);
                        break;
                    case 1:
                        mbottomNavigationView.setSelectedItemId(R.id.action_calls);
                        break;
                    case 2:
                        mbottomNavigationView.setSelectedItemId(R.id.action_contacts);
                        break;
                    case 3:
                        mbottomNavigationView.setSelectedItemId(R.id.action_news);
                        break;
                }

                super.onPageSelected(position);
            }
        });
        getToken();
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                Log.i("My token: ",token);
            }
        });
    }

}
