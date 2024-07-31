package com.choiminseon.fletterapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SuccessPaymentActivity extends AppCompatActivity {

    TextView txtOrderNumber;
    Button btnOrderHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_payment);

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

        txtOrderNumber = findViewById(R.id.txtOrderNumber);
        btnOrderHistory = findViewById(R.id.btnOrderHistroy);

        Intent intent = getIntent();
        String orderNumber = intent.getStringExtra("orderNumber");

        txtOrderNumber.setText("(주문번호 : " + orderNumber + ")");

        btnOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessPaymentActivity.this, MainActivity.class);
                intent.putExtra("targetFragment", "OrderHistoryFragment");
                startActivity(intent);
                finish();
            }
        });

    }
}