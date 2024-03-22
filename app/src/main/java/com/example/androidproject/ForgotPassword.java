package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class ForgotPassword extends AppCompatActivity {
    EditText editTextEmail;
    TextView btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        editTextEmail = findViewById(R.id.textForgotPassword);
        btnConfirm = findViewById(R.id.btnConfirm);

        Intent intent = getIntent();
        // SignUp
        String email = intent.getStringExtra("EMAIL");
        if(email != null){
            editTextEmail.setText(email);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                usersRef.orderByChild("email").equalTo(editTextEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                                String srtRamdom = ramdomString();
                                SendEmailTask sendEmailTask = new SendEmailTask(editTextEmail.getText().toString(), "Code forgot password","Authentication code forgot password.\n "+ srtRamdom);
                                sendEmailTask.execute();
                                navigateToConfirmEmail(editTextEmail.getText().toString(),srtRamdom);

                        } else {
                            // Email không tồn tại trong cơ sở dữ liệu
                            Toast.makeText(ForgotPassword.this, "Email không tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xảy ra lỗi khi truy vấn cơ sở dữ liệu
                        Log.e("SignInActivity", "Error: " + databaseError.getMessage());
                    }
                });
            }
        });
    }
    public String ramdomString(){
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int randomNumber = random.nextInt(10);
            stringBuilder.append(randomNumber);
        }
        String randomString = stringBuilder.toString();
        return randomString;
    }
    public void navigateToConfirmEmail(String email,String code) {
        Intent intent = new Intent(this, ConfirmEmail.class);
        intent.putExtra("EMAIL_FORGOT_PASSWORD", email);
        intent.putExtra("CODE", code);
        startActivity(intent);
    }

}
