package com.example.androidproject.activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
//

import com.example.androidproject.R;
import com.example.androidproject.SplashActivity;
import com.example.androidproject.model.FriendInvitation;
import com.example.androidproject.model.User;
import com.example.androidproject.utils.FirebaseUtil;
import com.example.androidproject.utils.UserUtil;
import com.example.androidproject.adapter.ViewPagerAdapter;
import com.example.androidproject.fragment.CallFragment;
import com.example.androidproject.fragment.ChatFragment;
import com.example.androidproject.fragment.ContactFragment;
import com.example.androidproject.fragment.NewFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zegocloud.uikit.components.audiovideo.ZegoAvatarViewProvider;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.config.DurationUpdateListener;
import com.zegocloud.uikit.prebuilt.call.config.ZegoCallDurationConfig;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener;
import com.zegocloud.uikit.prebuilt.call.event.ZegoCallEndReason;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.internal.IncomingCallButtonListener;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallType;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallUser;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoInvitationCallListener;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView mbottomNavigationView;
    private ViewPager2 mviewPager;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ImageView imageView;
    private TextView textViewName, textViewEmail,countFriendInvitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);


        ImageView imgView_menu = (ImageView) findViewById(R.id.btn_menu);

        ImageView imgView_search = (ImageView) findViewById(R.id.btn_search);

        countFriendInvitation = findViewById(R.id.img_on);
        mbottomNavigationView = findViewById(R.id.menu_bar);
        mviewPager = findViewById(R.id.view_pager);

        fragmentArrayList.add(new ChatFragment());
        fragmentArrayList.add(new CallFragment());
        fragmentArrayList.add(new ContactFragment());
        fragmentArrayList.add(new NewFragment());


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        imageView = headerView.findViewById(R.id.nav_image);
        textViewName = headerView.findViewById(R.id.nav_name);
        textViewEmail = headerView.findViewById(R.id.nav_email);

        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        loadDataNavigation();

        initZego();


        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        String message = "We need your consent for the following permissions in order to use the offline call function properly";
                        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                    }
                }).request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                         @NonNull List<String> deniedList) {
                    }
                });

        listenForFriendInvitations();
        imgView_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view))) {
                    drawerLayout.closeDrawer(findViewById(R.id.nav_view));
                } else {
                    drawerLayout.openDrawer(findViewById(R.id.nav_view));
                }
            }
        });

        System.out.println("Id User" + FirebaseUtil.currentUserId());
        getToken();


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
    }

    public void initZego(){
        if(FirebaseUtil.currentUserId().isEmpty()){
            return;
        } else {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseUtil.currentUserId());

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userName = snapshot.child("userName").getValue(String.class);
                        String avatarUrl = snapshot.child("profilePicture").getValue(String.class);
                        startService(FirebaseUtil.currentUserId(),userName, avatarUrl);
                    } else {

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error){
                    Log.w("TAG", "Failed to read value.", error.toException());
                }
            });
        }
    }

    public void startService(String userID, String userName, String avatarUrl){
        Application application = getApplication(); // Android's application context
        long appID = 242915274;   // yourAppID
        String appSign = "e6f8ddc838dbef59456ef0fc412816fe41dc7aceea9de39606273e282a924238";  // yourAppSign

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        callInvitationConfig.provider = new ZegoUIKitPrebuiltCallConfigProvider() {
            @Override
            public ZegoUIKitPrebuiltCallConfig requireConfig(ZegoCallInvitationData invitationData) {
                ZegoUIKitPrebuiltCallConfig config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
                boolean isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.getValue();
                boolean isGroupCall = invitationData.invitees.size() > 1;
                if (isVideoCall && isGroupCall) {
                    config = ZegoUIKitPrebuiltCallConfig.groupVideoCall();
                } else if (!isVideoCall && isGroupCall) {
                    config = ZegoUIKitPrebuiltCallConfig.groupVoiceCall();
                } else if (!isVideoCall) {
                    config = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();
                } else {
                    config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
                }
                config.durationConfig = new ZegoCallDurationConfig();
                config.durationConfig.isVisible = true;
                config.durationConfig.durationUpdateListener = new DurationUpdateListener() {
                    @Override
                    public void onDurationUpdate(long seconds) {
                        com.zego.ve.Log.d(TAG,"onDurationUpdate() called with: second = ["+ seconds+ "]");
                    }
                };
                config.avatarViewProvider = new ZegoAvatarViewProvider(){
                    @Override
                    public View onUserIDUpdated(ViewGroup parent, ZegoUIKitUser zegoUIKitUser) {
                        ImageView imageView = new ImageView(parent.getContext());
                        if (!TextUtils.isEmpty(avatarUrl)) {
                            RequestOptions requestOptions = new RequestOptions().circleCrop();
                            Glide.with(parent.getContext()).load(avatarUrl).apply(requestOptions).into(imageView);
                        }
                        return imageView;
                    }
                };

                return config;
            }
        };


        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();

        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";

        callInvitationConfig.innerText.incomingCallPageDeclineButton = "Decline";
        callInvitationConfig.innerText.incomingCallPageAcceptButton = "Accept";

        ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);

        ZegoUIKitPrebuiltCallService.events.callEvents.setCallEndListener(new CallEndListener() {
            @Override
            public void onCallEnd(ZegoCallEndReason callEndReason, String jsonObject) {
                System.out.println("onCallEnd() called with: callEndReason = [" + callEndReason + "], jsonObject = [" + jsonObject
                        + "]");
            }
        });

        ZegoUIKitPrebuiltCallService.events.invitationEvents.setIncomingCallButtonListener(new IncomingCallButtonListener() {
            @Override
            public void onIncomingCallDeclineButtonPressed() {
                System.out.println("Cuộc gọi đến bị từ chối");
            }

            @Override
            public void onIncomingCallAcceptButtonPressed() {
            }
        });

        ZegoUIKitPrebuiltCallService.events.invitationEvents.setInvitationListener(new ZegoInvitationCallListener() {
            @Override
            public void onIncomingCallReceived(String callID, ZegoCallUser caller, ZegoCallType callType, List<ZegoCallUser> callees) {
            }
            @Override
            public void onIncomingCallCanceled(String callID, ZegoCallUser caller) {
                System.out.println("Nhá máy" + caller.getName());
            }
            @Override
            public void onIncomingCallTimeout(String callID, ZegoCallUser caller) {
                System.out.println("Cuộc gọi bị từ chối");
            }
            @Override
            public void onOutgoingCallAccepted(String callID, ZegoCallUser callee) {
            }

            @Override
            public void onOutgoingCallRejectedCauseBusy(String callID, ZegoCallUser callee) {
                System.out.println("Cuộc gọi bị huỷ");
            }

            @Override
            public void onOutgoingCallDeclined(String callID, ZegoCallUser callee) {
                System.out.println("Cuộc gọi bị từ chối");
            }

            @Override
            public void onOutgoingCallTimeout(String callID, List<ZegoCallUser> callee) {
                System.out.println("Cuộc gọi bị nhỡ");
            }
        });
        System.out.println("Da tao ket noi");
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().child("fcmToken").setValue(token);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_chat) {
            mviewPager.setCurrentItem(0);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if (id == R.id.nav_group_add) {
            Intent i = new Intent(MainScreen.this, CreateGroupChat.class);
            startActivity(i);
        }

        if (id == R.id.nav_setting) {
            Intent intent = new Intent(MainScreen.this, InfoUser.class);
            startActivity(intent);
        }
        if (id == R.id.nav_logout) {
            showLogoutConfirmationDialog();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setCancelable(true)
                .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(MainScreen.this, SplashActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadDataNavigation() {
        FirebaseUtil.currentUserDetails().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = UserUtil.getUserFromSnapshot(snapshot);
                    Picasso.get().load(user.getProfilePicture()).into(imageView);
                    textViewName.setText(user.getUserName());
                    textViewEmail.setText(user.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void activityStatus(String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Status", status);
            reference.updateChildren(hashMap);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityStatus("offline");
    }

    private void listenForFriendInvitations() {
        List<FriendInvitation> listDetaiFriendInvitation = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("friendInvitations").child(FirebaseUtil.currentUserId());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listDetaiFriendInvitation.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String friendInvitationId = childSnapshot.getKey();
                    FirebaseUtil.currentFriendInvitation().child(friendInvitationId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String receiverId = snapshot.child("receiverId").getValue(String.class);
                                String resendId = snapshot.child("senderId").getValue(String.class);
                                String status = snapshot.child("status").getValue(String.class);
                                if (status.equals("pending")) {
                                    FriendInvitation fI = new FriendInvitation(resendId, receiverId, status, 10);
                                    listDetaiFriendInvitation.add(fI);

                                }
                                int invitationCount = listDetaiFriendInvitation.size();
                                if(invitationCount==0){
                                    countFriendInvitation.setVisibility(View.GONE);
                                }
                                String invitationCountString = String.valueOf(invitationCount);
                                countFriendInvitation.setText(invitationCountString);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý lỗi nếu có
                            Log.e("FriendInvitationId", "Error: " + error.getMessage());
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
