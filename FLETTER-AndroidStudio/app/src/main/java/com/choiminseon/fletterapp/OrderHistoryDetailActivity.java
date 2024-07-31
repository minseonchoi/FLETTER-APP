package com.choiminseon.fletterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.api.OrderApi;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.OrderHistoryDetail;
import com.choiminseon.fletterapp.model.OrderHistoryDetailList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderHistoryDetailActivity extends AppCompatActivity {

    TextView txtStatus;
    TextView txtPackageType;
    TextView txtAddFlower;
    TextView txtCreatedAt;
    TextView txtOrderNumber;
    TextView txtReceive;
    TextView txtUserName;
    TextView txtPhone;
    TextView txtAddress;
    TextView txtReservationDate;
    TextView txtContent;
    TextView txtPaymentMethod;
    TextView txtTotalPrice;
    TextView txtReservationMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_detail);

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

        txtStatus = findViewById(R.id.txtStatus);
        txtPackageType = findViewById(R.id.txtPackageType);
        txtAddFlower = findViewById(R.id.txtAddFlower);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        txtOrderNumber = findViewById(R.id.txtOrderNumber);
        txtReceive = findViewById(R.id.txtReceive);
        txtUserName = findViewById(R.id.txtUserName);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        txtReservationDate = findViewById(R.id.txtReservationDate);
        txtContent = findViewById(R.id.txtContent);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtReservationMsg = findViewById(R.id.txtReservationMsg);

        Intent intent = getIntent();
        int orderId = intent.getIntExtra("orderId", 0);

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");



        Retrofit retrofit = NetworkClient.getRetrofitClient(OrderHistoryDetailActivity.this);
        OrderApi api = retrofit.create(OrderApi.class);

        Call<OrderHistoryDetailList> call = api.getOrderHistoryDetail(orderId, "Bearer " + token);
        call.enqueue(new Callback<OrderHistoryDetailList>() {
            @Override
            public void onResponse(Call<OrderHistoryDetailList> call, Response<OrderHistoryDetailList> response) {
                if (response.isSuccessful()) {
                    OrderHistoryDetailList orderHistoryDetailList = response.body();
                    OrderHistoryDetail orderHistoryDetail = orderHistoryDetailList.orderInfo.get(0);

                    if (orderHistoryDetail.receive.equals("픽업")) {
                        txtReservationMsg.setText("픽업 예약 시간");
                    } else {
                        txtReservationMsg.setText("배송 예약 시간");
                    }

                    txtStatus.setText(orderHistoryDetail.status);
                    txtPackageType.setText(orderHistoryDetail.packagingType);
                    txtAddFlower.setText(orderHistoryDetail.orderFlower);
                    txtCreatedAt.setText(orderHistoryDetail.createdAt);
                    txtOrderNumber.setText(orderHistoryDetail.orderNumber);
                    txtReceive.setText(orderHistoryDetail.receive);
                    txtUserName.setText(orderHistoryDetail.userName);
                    txtPhone.setText(orderHistoryDetail.phone);
                    txtAddress.setText(orderHistoryDetail.address);
                    txtReservationDate.setText(orderHistoryDetail.reservationDate);
                    txtContent.setText(orderHistoryDetail.comment);
                    txtPaymentMethod.setText(orderHistoryDetail.paymentMethod);
                    txtTotalPrice.setText(orderHistoryDetail.totalPrice + "원");

                }
            }

            @Override
            public void onFailure(Call<OrderHistoryDetailList> call, Throwable throwable) {
                Toast.makeText(OrderHistoryDetailActivity.this, "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });

    }
}