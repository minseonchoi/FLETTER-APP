package com.choiminseon.fletterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.api.UserApi;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.User;
import com.choiminseon.fletterapp.model.UserRes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    EditText editEmail;
    EditText editPassword;
    TextView txtLogin;
    TextView txtRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 기본 액션바 설정
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(R.layout.custom_action_bar);

            // Back button 클릭 리스너 설정
            View customView = actionBar.getCustomView();
            ImageButton backButton = customView.findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> finish());

            // Shopping cart button 클릭 리스너 설정 (예시로 Toast를 표시합니다)
            ImageButton shoppingCartButton = customView.findViewById(R.id.shoppingCartButton);
            shoppingCartButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            });

            // FLETTER 누르면 메인으로 이동
            TextView actionBarTitle = customView.findViewById(R.id.actionBarTitle);
            actionBarTitle.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        }

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        txtLogin = findViewById(R.id.txtLoginRegister);
        txtRegister = findViewById(R.id.txtRegister);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                // 이메일, 패스워드 필수항목 체크
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                User user = new User(email, password);
                Call<UserRes> call = api.login(user);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {

                        if (response.isSuccessful()) {
                            UserRes userRes = response.body();
                            Log.i("TOKEN", userRes.accessToken);
                            SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", userRes.accessToken);
                            editor.commit();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "인터넷 연결이 되어있는지 확인해주세요.", Toast.LENGTH_SHORT).show();
                            Log.i("LOGIN", response.toString());
                        }

                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable throwable) {
                        Toast.makeText(LoginActivity.this, "인터넷 연결이 되어있는지 확인해주세요.", Toast.LENGTH_SHORT).show();
                        Log.i("LOGIN", throwable.toString());
                    }
                });


            }
        });


        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });



    }
}