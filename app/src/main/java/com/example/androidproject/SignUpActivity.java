package com.example.androidproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Properties;
import java.util.Random;

import javax.mail.*;
import javax.mail.internet.*;
import com.example.androidproject.SendEmailTask;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private EditText editTextPassWord;
    private EditText editTextConfirm;
    private TextView btnSignUp;
    private boolean passwordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextUserName = findViewById(R.id.textUsername);
        editTextEmail = findViewById(R.id.textEmail);
        editTextPhone = findViewById(R.id.textPhoneNumber);
        editTextAddress = findViewById(R.id.textAddress);
        editTextPassWord = findViewById(R.id.textPassword);
        editTextConfirm = findViewById(R.id.textConfirm);
        btnSignUp = findViewById(R.id.sign_up_btn);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = editTextUserName.getText().toString();
                String email = editTextEmail.getText().toString();
                String phone = editTextPhone.getText().toString();
                String address = editTextAddress.getText().toString();
                String passWord = editTextPassWord.getText().toString();
                String confirm = editTextConfirm.getText().toString();

                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userName.matches(".*\\d.*") || !userName.matches("[a-zA-Z\\s]+")) {
                    Toast.makeText(SignUpActivity.this, "Tên người dùng không được chứa số hoặc ký tự đặc biệt, nhưng có thể chứa khoảng trắng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập địa chỉ email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidEmail(email)) {
                    Toast.makeText(SignUpActivity.this, "Email không hợp lệ abc@gmail.com", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidPhoneNumber(phone)) {
                    Toast.makeText(SignUpActivity.this, "Số điện thoại không hợp lệ bắt đầu là 0 và có 10 số 0987655432", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(passWord)) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Nếu cần kiểm tra mật khẩu xác nhận, hãy thêm điều kiện ở đây
                if (!passWord.equals(confirm)) {
                    Toast.makeText(SignUpActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("users");
                usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Email đã được sử dụng.", Toast.LENGTH_SHORT).show();
                        } else {
                            usersRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Toast.makeText(getApplicationContext(), "Số điện thoại đã được sử dụng.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Random random = new Random();
                                        StringBuilder stringBuilder = new StringBuilder();
                                        for (int i = 0; i < 6; i++) {
                                            // Sinh số ngẫu nhiên từ 0 đến 9 và thêm vào chuỗi
                                            int randomNumber = random.nextInt(10);
                                            stringBuilder.append(randomNumber);
                                        }
                                        String randomString = stringBuilder.toString();
                                        SendEmailTask sendEmailTask = new SendEmailTask(editTextEmail.getText().toString(), "Test Subject","Congratulations on successfully registering.\n "+randomString);
                                        sendEmailTask.execute();
                                        navigateToConfirmEmail(randomString,userName,email,phone,address,passWord,confirm);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    return;
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        return;
                    }
                });
            }
        });
        setIcon(editTextPassWord);
        editTextPassWord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextPassWord.getRight() - editTextPassWord.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(editTextPassWord);
                        return true;
                    }
                }
                return false;
            }
        });

        // Find your TextView by its ID
        TextView textViewSignUp = findViewById(R.id.textViewAlreadyMember);

        // Combine the two strings with "Sign in" clickable
        String combinedText = getString(R.string.already_member) + " " + getString(R.string.sign_in);

        SpannableString spannableString = new SpannableString(combinedText);

        // Set ClickableSpan and color for "Sign in"
        int startIndex = combinedText.indexOf(getString(R.string.sign_in));
        int endIndex = startIndex + getString(R.string.sign_in).length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                navigateToSignIn();
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, 0);
        spannableString.setSpan(new android.text.style.ForegroundColorSpan(Color.rgb(12,46,223)), startIndex, endIndex, 0);

        // Set the modified SpannableString to the TextView
        textViewSignUp.setText(spannableString);
        textViewSignUp.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

    }

    public void navigateToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void setIcon(EditText editText) {
        Drawable icon = getResources().getDrawable(passwordVisible ? R.drawable.visible : R.drawable.hidden);
        int width = 60;
        int height = 60;
        icon.setBounds(0, 0, width, height);
        editText.setCompoundDrawablesRelative(null, null, icon, null);
    }

    private void togglePasswordVisibility(EditText editText) {
        int selection = editText.getSelectionEnd(); // Keep track of cursor position

        if (!passwordVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        passwordVisible = !passwordVisible;
        setIcon(editText);

        // Restore cursor position
        editText.setSelection(selection);
    }
    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private boolean isValidPhoneNumber(CharSequence target) {
        String phoneNumberPattern = "^0\\d{9}$";
        return (!TextUtils.isEmpty(target) && target.toString().matches(phoneNumberPattern));
    }
    public void navigateToConfirmEmail(String emailCode,String userName,String email,String phone,String address,String passWord,String confirm) {
        Intent intent = new Intent(this, ConfirmEmail.class);
        intent.putExtra("USERNAME",userName);
        intent.putExtra("EMAILSIGNUP",email);
        intent.putExtra("PHONE",phone);
        intent.putExtra("ADDRESS",address);
        intent.putExtra("PASSWORD",passWord);
        intent.putExtra("CONFIRM",confirm);
        intent.putExtra("EMAIL_CODE", emailCode);
        startActivity(intent);
    }
}

