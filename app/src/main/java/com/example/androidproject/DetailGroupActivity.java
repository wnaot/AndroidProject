package com.example.androidproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.Model.GroupChat;
import com.example.androidproject.Model.MessageGroup;
import com.example.androidproject.Utils.AndroidUtil;
import com.example.androidproject.Utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DetailGroupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView btn_back,btn_more;
    private TextView txtName,btnMemberGroup,btnRemoveGroup;
    ShapeableImageView image_Avatar;
    private LinearLayout btnActionCall,btnActionVideoCall,btnActionAddUser, btnActionTogNotification;

    String groupChatId;

    ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deltail_group);



        btn_back = findViewById(R.id.btn_previos_action);
        image_Avatar = findViewById(R.id.imageViewGroup);
        btn_more = findViewById(R.id.btn_more);
        txtName = findViewById(R.id.user_name);
        btnActionAddUser = findViewById(R.id.btn_action_seeProfile);
        btnMemberGroup = findViewById(R.id.btn_members_group);
        btnRemoveGroup = findViewById(R.id.btn_action_exit);

        Intent intent = getIntent();
        groupChatId = intent.getStringExtra("groupChatId");

        loadData();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnActionAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailGroupActivity.this, AddUserInGroup.class);
                intent.putExtra("groupChatId", groupChatId);
                startActivity(intent);
            }
        });

        btnMemberGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailGroupActivity.this,MemberActivity.class);
                intent.putExtra("groupChatId", groupChatId);
                startActivity(intent);
            }
        });
        btnRemoveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveGroupConfirmationDialog();
            }
        });

        image_Avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailGroupActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_menu);
                bottomSheetDialog.show();

                RelativeLayout relativeLayout = bottomSheetDialog.findViewById(R.id.btnRlayoutCamera);
                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);
                    }
                });

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Lấy Uri của ảnh được chọn
            Uri uri = data.getData();

            // Upload ảnh lên Firebase Storage
            uploadImageToFirebaseStorage(uri);

        }
    }
    private void uploadImageToFirebaseStorage(Uri uri) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File.....");
        progressDialog.show();

        // Tham chiếu tới Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // Tạo tham chiếu tới ảnh trong Firebase Storage (có thể đặt tên ngẫu nhiên)
        StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());
        try {
            // Giảm kích thước của ảnh trước khi tải lên
            Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri), 1024, 1024);

            // Chuyển đổi Bitmap thành dữ liệu byte
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageData = baos.toByteArray();

            // Upload ảnh từ dữ liệu byte đã được giảm kích thước
            imageRef.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Lấy URL của ảnh đã tải lên
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // URL của ảnh đã tải lên
                            String imageUrl = uri.toString();

                            // Lưu URL của ảnh vào Firebase Realtime Database
                            updateUserProfilePicture(FirebaseUtil.currentUserId(),imageUrl);

                            Toast.makeText(DetailGroupActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                            loadData();
                        }
                    });
                    progressDialog.dismiss();

                    // Hiển thị ảnh đã tải lên trên ImageView
                    image_Avatar.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailGroupActivity.this, "Upload Failed!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức để lưu URL của ảnh vào Firebase Realtime Database
    private void updateUserProfilePicture(String userId, String newProfilePictureUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("groupchatPicture", newProfilePictureUrl);
        // Tham chiếu tới Firebase Realtime Database
        FirebaseUtil.allGroupChat().child(groupChatId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Xử lý khi cập nhật thành công
                Toast.makeText(DetailGroupActivity.this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show();
                loadData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý khi cập nhật thất bại
                Toast.makeText(DetailGroupActivity.this, "Failed to update profile picture!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxWidth, int maxHeight) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = originalWidth;
        int resizedHeight = originalHeight;

        // Nếu kích thước vượt quá giới hạn, thì thu nhỏ ảnh xuống kích thước tối đa được chỉ định
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            float aspectRatio = originalWidth / (float) originalHeight;
            if (aspectRatio > 1) {
                resizedWidth = maxWidth;
                resizedHeight = Math.round(maxWidth / aspectRatio);
            } else {
                resizedHeight = maxHeight;
                resizedWidth = Math.round(maxHeight * aspectRatio);
            }
        }

        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, true);
    }

    public void loadData(){
        FirebaseUtil.allGroupChat().child(groupChatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String groupPicture = snapshot.child("groupchatPicture").getValue(String.class);
                String name = snapshot.child("name").getValue(String.class);

                txtName.setText(name);
                Picasso.get().load(groupPicture).into(image_Avatar);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void addRole(){
        FirebaseUtil.allGroupChat().child(groupChatId).child("members").
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstMemberId = null; // Biến để lưu giữ giá trị memberId đầu tiên
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    firstMemberId = groupSnapshot.getValue(String.class);
                    break;
                }
                Map < String, Object > adminData = new HashMap<>();
                adminData.put(firstMemberId, true);
                FirebaseUtil.allGroupChat().child(groupChatId).child("admin").updateChildren(adminData);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void checkCountAdmin(){
        List<String> listIdAdmin  = new ArrayList<>();
        FirebaseUtil.allGroupChat().child(groupChatId).child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIdAdmin.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    String adminID = groupSnapshot.getKey();
                    listIdAdmin.add(adminID);
                }
                if(listIdAdmin.size() == 1){
                    if(listIdAdmin.contains(FirebaseUtil.currentUserId())){
                        addRole();
                        removeRole();
                    }
                }
                else{
                    removeRole();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });
    }
    public void removeRole(){
        FirebaseUtil.allGroupChat().child(groupChatId).child("admin").child(FirebaseUtil.currentUserId()).removeValue();
    }
    public void removeGroup(){
        checkCountAdmin();
        FirebaseUtil.allGroupChat().child(groupChatId).child("members").orderByValue().equalTo(FirebaseUtil.currentUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                checkCountAdmin();
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
            }
        });
    }
    private void showRemoveGroupConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailGroupActivity.this);
        builder.setMessage("Bạn chắc chắn muốn rời nhóm")
                .setCancelable(true)
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeGroup();
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
}
