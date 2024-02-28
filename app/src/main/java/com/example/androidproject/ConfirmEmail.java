package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfirmEmail extends AppCompatActivity {
    EditText editTextConfirmCode;
    Button btnConfirm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);
        editTextConfirmCode = findViewById(R.id.textConfirmCode);
        btnConfirm = findViewById(R.id.btnConfirm);
        Intent intent = getIntent();

        // SignUp
        String emailCode = intent.getStringExtra("EMAIL_CODE");
        String userName = intent.getStringExtra("USERNAME");
        String emailSignIn = intent.getStringExtra("EMAILSIGNUP");
        String phone = intent.getStringExtra("PHONE");
        String address = intent.getStringExtra("ADDRESS");
        String passWord = intent.getStringExtra("PASSWORD");
        String confirm = intent.getStringExtra("CONFIRM");


        //Forgot Password
        String emailCodePassword = intent.getStringExtra("EMAIL_CODE_PASSWORD");
        String email = intent.getStringExtra("EMAIL");

        if(emailCode!= null){
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(emailCode.equals(editTextConfirmCode.getText().toString())){
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String userId = currentUser != null ? currentUser.getUid() : "";
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference usersRef = database.getReference("users");
                        User user = new User(userName, emailSignIn, phone, address, passWord, confirm);
                        //User user = new User("tranMinhToan", "abc@gmail.com", "0987654321", "67C LTB", "300802", "300802");
                        usersRef.child(userId).setValue(user);
                    }
                    else{
                        Toast.makeText(ConfirmEmail.this, "Sai mã xác nhận.", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(ConfirmEmail.this, "Nhập mã xác nhận thành công", Toast.LENGTH_SHORT).show();
                    navigateToSignIn();
                }
            });
        }
        if(emailCodePassword !=null){
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(emailCodePassword.equals(editTextConfirmCode.getText().toString())){
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        String storedPassword = userSnapshot.child("passWord").getValue(String.class);
                                        SendEmailTask sendEmailTask = new SendEmailTask(email, "Password","Password: "+ storedPassword);
                                        sendEmailTask.execute();
                                        Toast.makeText(ConfirmEmail.this, "Nhập mã xác nhận thành công. Mật khẩu sẽ được gửi vào email của bạn", Toast.LENGTH_SHORT).show();
                                        navigateToSignIn();
                                    }
                                } else {
                                    // Email không tồn tại trong cơ sở dữ liệu
                                    Toast.makeText(ConfirmEmail.this, "Email không tồn tại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Xảy ra lỗi khi truy vấn cơ sở dữ liệu
                                Log.e("SignInActivity", "Error: " + databaseError.getMessage());
                            }
                        });
                    }else{
                        Toast.makeText(ConfirmEmail.this, "Sai mã xác nhận", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void navigateToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}
