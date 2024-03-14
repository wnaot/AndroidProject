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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
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
    FirebaseAuth mAuth;
    DatabaseReference reference;
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

        mAuth = FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = editTextUserName.getText().toString();
                String email = editTextEmail.getText().toString();
                String phone = editTextPhone.getText().toString();
                String address = editTextAddress.getText().toString();
                String passWord = editTextPassWord.getText().toString();
                String confirm = editTextConfirm.getText().toString();

//                if (TextUtils.isEmpty(userName)) {
//                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (userName.matches(".*\\d.*") || !userName.matches("[a-zA-Z\\s]+")) {
//                    Toast.makeText(SignUpActivity.this, "Tên người dùng không được chứa số hoặc ký tự đặc biệt, nhưng có thể chứa khoảng trắng", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(email)) {
//                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập địa chỉ email", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!isValidEmail(email)) {
//                    Toast.makeText(SignUpActivity.this, "Email không hợp lệ abc@gmail.com", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(phone)) {
//                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!isValidPhoneNumber(phone)) {
//                    Toast.makeText(SignUpActivity.this, "Số điện thoại không hợp lệ bắt đầu là 0 và có 10 số 0987655432", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(address)) {
//                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (TextUtils.isEmpty(passWord)) {
//                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                // Nếu cần kiểm tra mật khẩu xác nhận, hãy thêm điều kiện ở đây
//                if (!passWord.equals(confirm)) {
//                    Toast.makeText(SignUpActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                //register(userName,email,phone,address,passWord);
                register("MinhTonf","toan07122019@gmail.com","01223624615Thai","168 ba hat","144 kafas");
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

    public void register(String username, String email, String password, String phone, String address) {
        // Đăng ký người dùng với email và mật khẩu
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Người dùng được đăng ký thành công
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Lấy ID của người dùng
                                String userId = firebaseUser.getUid();
                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                                // Tạo một HashMap chứa thông tin của người dùng
                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("userId",userId);
                                userMap.put("userName", username);
                                userMap.put("email", email);
                                userMap.put("phone", phone);
                                userMap.put("address", address);
                                userMap.put("password", password);
                                userMap.put("lastActive", "default");
                                userMap.put("profilePicture", "default");
                                // Thêm thông tin người dùng vào collection "Users"
                                reference.setValue(userMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    firebaseUser.sendEmailVerification()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        // Email xác nhận đã được gửi thành công
                                                                        Toast.makeText(SignUpActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        // Không thể gửi email xác nhận
                                                                        Toast.makeText(SignUpActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                            });
                                                    if(firebaseUser.isEmailVerified()){
                                                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Đăng ký người dùng thất bại
                            Toast.makeText(SignUpActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

