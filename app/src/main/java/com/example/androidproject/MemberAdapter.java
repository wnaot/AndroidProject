package com.example.androidproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.AndroidUtil;
import com.example.androidproject.Utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;


public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private List<User> listItemUser;
    Context context;
    private String groupChatId;

    public MemberAdapter(List<User> listItemUser, Context context,String groupChatId) {
        this.listItemUser = listItemUser;
        this.context = context;
        this.groupChatId = groupChatId;

    }

    public void setData(List<User> listItemUser) {
        this.listItemUser = listItemUser;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_member, parent, false);
        return new MemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User itemUser = listItemUser.get(position);
        if(itemUser == null) {
            return;
        }
        Picasso.get().load(itemUser.getProfilePicture()).into(holder.imgAvatar);
        holder.txtName.setText(itemUser.getUserName());

        holder.layoutList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!itemUser.getUserId().equals(FirebaseUtil.currentUserId())){
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                    bottomSheetDialog.setContentView(R.layout.action_admin);
                    bottomSheetDialog.show();

                    RelativeLayout relativeLayout = bottomSheetDialog.findViewById(R.id.btnRemoveMember);

                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showRemoveMemberConfirmationDialog(itemUser.getUserName(),itemUser.getUserId());
                            bottomSheetDialog.dismiss();

                        }
                    });
                    RelativeLayout addRole = bottomSheetDialog.findViewById(R.id.btnAddRole);
                    addRole.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAddRoleAdminConfirmationDialog(itemUser.getUserName(),itemUser.getUserId());
                            bottomSheetDialog.dismiss();
                        }
                    });
                    return true;
                }
                return false;
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
        private RelativeLayout layoutList;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.item_imgViewLFriend);
            txtName = itemView.findViewById(R.id.item_nameLFriend);
            layoutList = itemView.findViewById(R.id.layoutListFriend);
        }
    }
    private void showAddRoleAdminConfirmationDialog(String name,String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có muốn thêm quyền quản trị viên cho "+name+" không?")
                .setCancelable(true)
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, Object> adminData = new HashMap<>();
                        adminData.put(userId, true);
                        FirebaseUtil.allGroupChat().child(groupChatId).child("admin").updateChildren(adminData).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                AndroidUtil.showToast(context, "Bạn đã thêm quyền quản trị viên cho" + name + " thành công");
                                loadListData();
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

     public void loadListData() {
        List<String> listIdMember = new ArrayList<>();
        List<User> listUserAll = new ArrayList<>();
        FirebaseUtil.allGroupChat().child(groupChatId).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIdMember.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    String memberId = groupSnapshot.getValue(String.class);
                    if (!memberId.equals(FirebaseUtil.currentUserId())) {
                        listIdMember.add(memberId);
                    }
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

                            User user = new User(userID, userName, avatar, status);
                            listUserAll.add(user);
                            latch.countDown();

                            setData(listUserAll);
                            if (latch.getCount() == 0) {
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
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
    
    private void showRemoveMemberConfirmationDialog(String name,String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có xóa "+name+" ra khỏi nhóm")
                .setCancelable(true)
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseUtil.allGroupChat().child(groupChatId).child("members").orderByValue().equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    ds.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                AndroidUtil.showToast(context, "Bạn đã " + name + " ra khỏi nhóm");
                                                loadListData();
                                            }
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
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
    // public void loadListData() {
    //     List<String> listIdMember = new ArrayList<>();
    //     List<User> listUserAll = new ArrayList<>();
    //     FirebaseUtil.allGroupChat().child(groupChatId).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
    //         @Override
    //         public void onDataChange(@NonNull DataSnapshot snapshot) {
    //             listIdMember.clear();
    //             for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
    //                 String memberId = groupSnapshot.getValue(String.class);
    //                 if (!memberId.equals(FirebaseUtil.currentUserId())) {
    //                     listIdMember.add(memberId);
    //                 }
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

    //                         User user = new User(userID, userName, avatar, status);
    //                         listUserAll.add(user);
    //                         latch.countDown();

    //                         setData(listUserAll);
    //                         if (latch.getCount() == 0) {
    //                         }
    //                     }
    //                     @Override
    //                     public void onCancelled(@NonNull DatabaseError error) {
    //                         latch.countDown();
    //                     }
    //                 });
    //             }
    //         }

    //         @Override
    //         public void onCancelled(@NonNull DatabaseError error) {
    //         }
    //     });
    // }
}

