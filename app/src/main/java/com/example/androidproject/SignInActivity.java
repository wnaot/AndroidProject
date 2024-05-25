package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.androidproject.activity.ForgotPassword;
import com.example.androidproject.activity.MainScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {
    private boolean passwordVisible = false;
    TextView sign_in_btn;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        EditText editTextUserName = findViewById(R.id.textUsername);
        EditText editText = findViewById(R.id.textPassword);

        auth = FirebaseAuth.getInstance();
        sign_in_btn = findViewById(R.id.sign_in_btn);
        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextUserName.getText().toString().trim();
                String password = editText.getText().toString().trim();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(SignInActivity.this,"All fileds are required",Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(SignInActivity.this, MainScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(SignInActivity.this,"Authentication failed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
        setIcon(editText);

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(editText);
                        return true;
                    }
                }
                return false;
            }
        });

        TextView textForgotPassword = findViewById(R.id.textForgotPassword);

        // Combine the two strings with "Password" clickable
        String combinedText = getString(R.string.forgot_password) + " " + getString(R.string.password) + "?";

        SpannableString spannableString = new SpannableString(combinedText);

        // Set ClickableSpan and color for "Password"
        int startIndex = combinedText.indexOf(getString(R.string.password));
        int endIndex = startIndex + getString(R.string.password).length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //Navigate to forgot password action
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, 0);
        spannableString.setSpan(new android.text.style.ForegroundColorSpan(Color.rgb(12, 46, 223)), startIndex, endIndex, 0);

        // Set the modified SpannableString to the TextView
        textForgotPassword.setText(spannableString);
        textForgotPassword.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        TextView textCreateAccount = findViewById(R.id.textCreateAccount);

        // Combine the two strings with "Account" clickable
        String combinedTextCreateAccount = getString(R.string.create_account) + " " + getString(R.string.account) + "?";

        SpannableString spannableStringAccount = new SpannableString(combinedTextCreateAccount);

        // Set ClickableSpan and color for "Account"
        int startIndexAccount = combinedTextCreateAccount.indexOf(getString(R.string.account));
        int endIndexAccount = startIndexAccount + getString(R.string.account).length();

        ClickableSpan clickableSpanAccount = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                navigateToSignUp();
            }
        };

        spannableStringAccount.setSpan(clickableSpanAccount, startIndexAccount, endIndexAccount, 0);
        spannableStringAccount.setSpan(new android.text.style.ForegroundColorSpan(Color.rgb(12, 46, 223)), startIndexAccount, endIndexAccount, 0);

        // Set the modified SpannableString to the TextView
        textCreateAccount.setText(spannableStringAccount);
        textCreateAccount.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());


        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }
    public void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
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


}