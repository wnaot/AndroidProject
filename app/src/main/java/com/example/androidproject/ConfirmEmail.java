    package com.example.androidproject;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;

    import com.example.androidproject.Utils.AndroidUtil;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.Query;
    import com.google.firebase.database.ValueEventListener;

    import java.util.HashMap;
    import java.util.Random;

    public class ConfirmEmail extends AppCompatActivity {
        EditText editTextConfirmCode;
        TextView btnConfirm;
        TextView btnResend;
        FirebaseAuth mAuth;
        DatabaseReference reference;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_confirm_email);

            editTextConfirmCode = findViewById(R.id.textConfirmCode);
            btnConfirm = findViewById(R.id.btnConfirm);
            btnResend = findViewById(R.id.btnResend);
            mAuth = FirebaseAuth.getInstance();
            reference = FirebaseDatabase.getInstance().getReference();

            Intent intent = getIntent();

            // SignUp
            String userName = intent.getStringExtra("USERNAME");
            String emailSignUp = intent.getStringExtra("EMAIL");
            String passWord = intent.getStringExtra("PASSWORD");
            String phone = intent.getStringExtra("PHONE");
            String address = intent.getStringExtra("ADDRESS");

            //Forgot Password
            String emailForgotPassword = intent.getStringExtra("EMAIL_FORGOT_PASSWORD");
            String codeForgotPassword = intent.getStringExtra("CODE");

            btnResend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (emailSignUp != null) {
                        String code = ramdomString();
                        reSendEmail(emailSignUp, "Mã xác nhận", "Code : " + code);
                    }
                    else{
                        Intent intent = new Intent(ConfirmEmail.this, ForgotPassword.class);
                        intent.putExtra("EMAIL", emailForgotPassword);
                        startActivity(intent);
                    }
                }
            });
            if (emailSignUp != null) {
                String code = ramdomString();
                reSendEmail(emailSignUp, "Mã xác nhận", "Code : " + code);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (code.equals(editTextConfirmCode.getText().toString())) {

                            //Nhập mã xác nhận đúng
                            AndroidUtil.showToast(ConfirmEmail.this,"Đăng ký thành công. Bạn hãy bắt đầu đăng nhập và sử dụng!");
                            //Toast.makeText(ConfirmEmail.this, "Đăng ký thành công. Bạn hãy bắt đầu đăng nhập và sử dụng!", Toast.LENGTH_SHORT).show();
                            register(userName, emailSignUp, passWord, phone, address);
                            navigateToSignIn();
                        } else {
                            Toast.makeText(ConfirmEmail.this, "Sai mã xác nhận. Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
            if (emailForgotPassword != null) {
                btnResend.setText("Back");
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assert codeForgotPassword != null;
                        if (codeForgotPassword.equals(editTextConfirmCode.getText().toString())) {
                            Toast.makeText(ConfirmEmail.this, "Mã xác nhận đúng. Chờ password sẽ được gửi đến email của bạn!", Toast.LENGTH_SHORT).show();
                            Query query = reference.child("Users").orderByChild("email").equalTo(emailForgotPassword);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // Duyệt qua từng `DataSnapshot` con trong `snapshot`
                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                            // Lấy giá trị của trường "password"
                                            String password = snap.child("password").getValue(String.class);
                                            // Gửi email chỉ khi tìm thấy mật khẩu
                                            if (password != null) {
                                                // Sử dụng SendEmailTask để gửi email
                                                SendEmailTask sendEmailTask = new SendEmailTask(emailForgotPassword, "FORGOT_PASSWORD", "password: " + password);
                                                sendEmailTask.execute();
                                            }
                                        }
                                    } else {
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            navigateToSignIn();

                        } else {
                            Toast.makeText(ConfirmEmail.this, "Sai mã xác nhận. Vui lòng nhập lại!!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        }

        public void navigateToSignIn() {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }

        public String ramdomString() {
            Random random = new Random();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                int randomNumber = random.nextInt(10);
                stringBuilder.append(randomNumber);
            }
            String randomString = stringBuilder.toString();
            return randomString;
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
                                    userMap.put("userId", userId);
                                    userMap.put("userName", username);
                                    userMap.put("email", email);
                                    userMap.put("phone", phone);
                                    userMap.put("address", address);
                                    userMap.put("password", password);
                                    userMap.put("friendList", "");
                                    userMap.put("blockList", "");
                                    userMap.put("lastActive", "default");
                                    userMap.put("profilePicture", "https://firebasestorage.googleapis.com/v0/b/productappchat.appspot.com/o/images%2Favatar-default.png?alt=media&token=6b103445-3c5c-4f76-8ad6-d62fab194574");
                                    // Thêm thông tin người dùng vào collection "Users"
                                    reference.setValue(userMap);
                                }
                            } else {
                                // Đăng ký người dùng thất bại
                                Toast.makeText(ConfirmEmail.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        public void reSendEmail(String email, String subject, String body) {
            SendEmailTask sendEmailTask = new SendEmailTask(email, subject, body);
            sendEmailTask.execute();
        }

        
    }
