package com.example.androidproject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MemberActivity extends AppCompatActivity {
    ImageView btn_previos;
    TextView btn_add;
    TextView btn_all,btn_admin,count_member;

    RecyclerView recycle_member;

    String groupChatId;

    private List<String> listIdMember;
    private List<User> listUserAll,listAdmin;

    MemberAdapter memberAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_member);

        btn_previos = findViewById(R.id.btn_previos_action);
        btn_add = findViewById(R.id.btn_add_member);
        btn_all = findViewById(R.id.btnAll);
        btn_admin = findViewById(R.id.btnAdmin);
        count_member = findViewById(R.id.count_member);
        recycle_member = findViewById(R.id.recycle_member);

        LinearLayoutManager memberLayoutManager = new LinearLayoutManager(this);
        recycle_member.setLayoutManager(memberLayoutManager);

        listIdMember = new ArrayList<>();
        listUserAll = new ArrayList<>();
        listAdmin = new ArrayList<>();
        Intent intent = getIntent();
        groupChatId = intent.getStringExtra("groupChatId");
        loadListData();
        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listUserAll.clear();
                loadListData();
            }
        });
        btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdmin.clear();
                loadListDataAdmin();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberActivity.this, AddUserInGroup.class);
                intent.putExtra("groupChatId", groupChatId);
                startActivity(intent);
            }
        });
        btn_previos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // public void loadListData() {
    //     FirebaseUtil.allGroupChat().child(groupChatId).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
    //         @Override
    //         public void onDataChange(@NonNull DataSnapshot snapshot) {
    //             listIdMember.clear();
    //             for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
    //                 String memberId = groupSnapshot.getValue(String.class);
    //                 listIdMember.add(memberId);

    //             }

    //             // Khởi tạo CountDownLatch với số lượng request từ Firebase
    //             CountDownLatch latch = new CountDownLatch(listIdMember.size());

    //             for (String id : listIdMember) {
    //                 FirebaseUtil.allUserDatabaseReference().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
    //                     @Override
    //                     public void onDataChange(@NonNull DataSnapshot snapshot) {
    //                         String userID = id;
    //                         String userName = snapshot.child("userName").getValue(String.class);
    //                         String avatar = snapshot.child("profilePicture").getValue(String.class);
    //                         String status = snapshot.child("Status").getValue(String.class);
    //                         if(userID.equals(FirebaseUtil.currentUserId())){
    //                             User user = new User(userID, "Tôi", avatar, status);
    //                             listUserAll.add(user);
    //                         }else{
    //                             User user = new User(userID, userName, avatar, status);
    //                             listUserAll.add(user);
    //                         }

    //                         latch.countDown();

    //                         // Nếu đã hoàn thành tất cả request, cập nhật RecyclerView
    //                         if (latch.getCount() == 0) {
                                
    //                             updateRecyclerView();
    //                         }
    //                     }

    //                     @Override
    //                     public void onCancelled(@NonNull DatabaseError error) {
    //                         // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
    //                         // Cần giảm số lượng request cần hoàn thành ở đây nếu cần
    //                         latch.countDown();
    //                     }
    //                 });
    //             }
    //         }

    //         @Override
    //         public void onCancelled(@NonNull DatabaseError error) {
    //             // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
    //         }
    //     });
    // }

    private void updateRecyclerView() {
        count_member.setText(listUserAll.size()+" Thành viên");
        memberAdapter = new MemberAdapter(listUserAll, MemberActivity.this,groupChatId);
        recycle_member.setAdapter(memberAdapter);
    }

    public void loadListData() {
        FirebaseUtil.allGroupChat().child(groupChatId).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIdMember.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    String memberId = groupSnapshot.getValue(String.class);
                    listIdMember.add(memberId);

                }

                // Khởi tạo CountDownLatch với số lượng request từ Firebase
                CountDownLatch latch = new CountDownLatch(listIdMember.size());

                for (String id : listIdMember) {
                    FirebaseUtil.allUserDatabaseReference().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userID = id;
                            String userName = snapshot.child("userName").getValue(String.class);
                            String avatar = snapshot.child("profilePicture").getValue(String.class);
                            String status = snapshot.child("Status").getValue(String.class);
                            if(userID.equals(FirebaseUtil.currentUserId())){
                                User user = new User(userID, "Tôi", avatar, status);
                                listUserAll.add(user);
                            }else{
                                User user = new User(userID, userName, avatar, status);
                                listUserAll.add(user);
                            }

                            latch.countDown();

                            // Nếu đã hoàn thành tất cả request, cập nhật RecyclerView
                            if (latch.getCount() == 0) {
                                
                                updateRecyclerView();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                            // Cần giảm số lượng request cần hoàn thành ở đây nếu cần
                            latch.countDown();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
            }
        });
    }

    public void loadListDataAdmin() {
        FirebaseUtil.allGroupChat().child(groupChatId).child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIdMember.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    String adminID = groupSnapshot.getKey();
                    listIdMember.add(adminID);
                }

                // Khởi tạo CountDownLatch với số lượng request từ Firebase
                CountDownLatch latch = new CountDownLatch(listIdMember.size());

                for (String id : listIdMember) {
                    FirebaseUtil.allUserDatabaseReference().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userID = id;
                            String userName = snapshot.child("userName").getValue(String.class);
                            String avatar = snapshot.child("profilePicture").getValue(String.class);
                            String status = snapshot.child("Status").getValue(String.class);
                            if(userID.equals(FirebaseUtil.currentUserId())){
                                User user = new User(userID, "Tôi", avatar, status);
                                listAdmin.add(user);
                            }
                            else{
                                User user = new User(userID, userName, avatar, status);
                                listAdmin.add(user);
                            }
                            // Giảm số lượng request cần hoàn thành
                            latch.countDown();

                            // Nếu đã hoàn thành tất cả request, cập nhật RecyclerView
                            if (latch.getCount() == 0) {
                                updateRecyclerViewAdmin();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                            // Cần giảm số lượng request cần hoàn thành ở đây nếu cần
                            latch.countDown();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updateRecyclerViewAdmin() {
        count_member.setText(listAdmin.size()+" Quản trị viên");
        memberAdapter = new MemberAdapter(listAdmin, MemberActivity.this,groupChatId);
        recycle_member.setAdapter(memberAdapter);
    }

}
