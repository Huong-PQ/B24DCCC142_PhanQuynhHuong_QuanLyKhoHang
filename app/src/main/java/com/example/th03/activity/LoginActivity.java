package com.example.th03.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.th03.R;
import com.example.th03.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private TextView tvLoginError;
    private Button btnLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvLoginError = findViewById(R.id.tvLoginError);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            tvLoginError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            tvLoginError.setVisibility(View.VISIBLE);
            return;
        }

        boolean isValid = dbHelper.checkLogin(username, password);
        if (isValid) {
            tvLoginError.setVisibility(View.GONE);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng LoginActivity để nút Back không quay lại
        } else {
            tvLoginError.setText("Sai tên đăng nhập hoặc mật khẩu");
            tvLoginError.setVisibility(View.VISIBLE);
        }
    }
}