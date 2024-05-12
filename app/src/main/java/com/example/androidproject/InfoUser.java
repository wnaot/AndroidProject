package com.example.androidproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.Model.User;
import com.example.androidproject.Utils.FirebaseUtil;
import com.example.androidproject.Utils.UserUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InfoUser extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    ShapeableImageView shapeableImageView,imageView;
    private TextView userName;
    ProgressDialog progressDialog;
    ImageView btnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);

        shapeableImageView = findViewById(R.id.imageCamera);
        imageView = findViewById(R.id.imageView);
        userName = findViewById(R.id.user_name);
        btnBack = findViewById(R.id.btn_previos_action);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(InfoUser.this,MainScreen.class);
//                startActivity(intent);
                finish();
            }
        });
        loadData();
        shapeableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(InfoUser.this);
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
    // private void uploadImageToFirebaseStorage(Uri uri) {
    //     progressDialog = new ProgressDialog(this);
    //     progressDialog.setTitle("Uploading File.....");
    //     progressDialog.show();

    //     // Tham chiếu tới Firebase Storage
    //     StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    //     // Tạo tham chiếu tới ảnh trong Firebase Storage (có thể đặt tên ngẫu nhiên)
    //     StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());
    //     try {
    //         // Giảm kích thước của ảnh trước khi tải lên
    //         Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri), 1024, 1024);

    //         // Chuyển đổi Bitmap thành dữ liệu byte
    //         ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //         bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
    //         byte[] imageData = baos.toByteArray();

    //         // Upload ảnh từ dữ liệu byte đã được giảm kích thước
    //         imageRef.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
    //             @Override
    //             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
    //                 // Lấy URL của ảnh đã tải lên
    //                 imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
    //                     @Override
    //                     public void onSuccess(Uri uri) {
    //                         // URL của ảnh đã tải lên
    //                         String imageUrl = uri.toString();

    //                         // Lưu URL của ảnh vào Firebase Realtime Database
    //                         updateUserProfilePicture(FirebaseUtil.currentUserId(),imageUrl);

    //                         Toast.makeText(InfoUser.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
    //                         loadData();
    //                     }
    //                 });
    //                 progressDialog.dismiss();

    //                 // Hiển thị ảnh đã tải lên trên ImageView
    //                 imageView.setImageBitmap(bitmap);
    //             }
    //         }).addOnFailureListener(new OnFailureListener() {
    //             @Override
    //             public void onFailure(@NonNull Exception e) {
    //                 Toast.makeText(InfoUser.this, "Upload Failed!!", Toast.LENGTH_SHORT).show();
    //                 progressDialog.dismiss();
    //             }
    //         });
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    // Phương thức để lưu URL của ảnh vào Firebase Realtime Database
    private void updateUserProfilePicture(String userId, String newProfilePictureUrl) {
        // Tham chiếu tới Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        // Tạo một HashMap để chứa thông tin cần cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("profilePicture", newProfilePictureUrl);

        // Cập nhật chỉ mục profilePicture cho người dùng
        databaseRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Xử lý khi cập nhật thành công
                Toast.makeText(InfoUser.this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show();
                loadData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý khi cập nhật thất bại
                Toast.makeText(InfoUser.this, "Failed to update profile picture!", Toast.LENGTH_SHORT).show();
            }
        });
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

                            Toast.makeText(InfoUser.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                            loadData();
                        }
                    });
                    progressDialog.dismiss();

                    // Hiển thị ảnh đã tải lên trên ImageView
                    imageView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(InfoUser.this, "Upload Failed!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức để giảm kích thước của ảnh
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
        FirebaseUtil.currentUserDetails().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = UserUtil.getUserFromSnapshot(snapshot);
                    Picasso.get().load(user.getProfilePicture()).into(imageView);
                    userName.setText(user.getUserName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
