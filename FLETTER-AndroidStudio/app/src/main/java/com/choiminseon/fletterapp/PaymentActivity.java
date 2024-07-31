package com.choiminseon.fletterapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.choiminseon.fletterapp.api.CartApi;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.api.OrderApi;
import com.choiminseon.fletterapp.api.UserApi;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.OrderRequset;
import com.choiminseon.fletterapp.model.OrderRes;
import com.choiminseon.fletterapp.model.ProfileRes;
import com.choiminseon.fletterapp.model.Res;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentActivity extends AppCompatActivity {

    TextView txtUserName;
    TextView txtPhone;
    TextView txtAddress;
    TextView txtReservationMsg;
    TextView txtReservationDate;
    EditText editContent;
    TextView txtTotalPrice;
    Button btnPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

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

        txtUserName = findViewById(R.id.txtUserName);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        txtReservationMsg = findViewById(R.id.txtReservationMsg);
        txtReservationDate = findViewById(R.id.txtReservationDate);
        editContent = findViewById(R.id.editContent);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        btnPayment = findViewById(R.id.btnPayment);


        Retrofit retrofit = NetworkClient.getRetrofitClient(PaymentActivity.this);
        UserApi api = retrofit.create(UserApi.class);

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");

        Call<ProfileRes> call = api.profile("Bearer " + token);
        call.enqueue(new Callback<ProfileRes>() {
            @Override
            public void onResponse(Call<ProfileRes> call, Response<ProfileRes> response) {
                if (response.isSuccessful()) {
                    ProfileRes profileRes = response.body();
                    if (profileRes != null) {
                        ProfileRes.Item profileResItem = profileRes.user.get(0);
                        txtUserName.setText(profileResItem.userName);
                        txtPhone.setText(profileResItem.phone);
                    }
                }

            }

            @Override
            public void onFailure(Call<ProfileRes> call, Throwable throwable) {
                Toast.makeText(PaymentActivity.this, "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        String receiveType = intent.getStringExtra("receiveType");
        String address = intent.getStringExtra("address");
        String reservationDate = intent.getStringExtra("reservationDate");
        String strTotalPrice = intent.getStringExtra("totalPrice");
        String strCartId = intent.getStringExtra("cartId");

        if (receiveType.equals("배송")) {
            txtReservationMsg.setText("배송 예약 시간");
        } else {
            txtReservationMsg.setText("픽업 예약 시간");
        }
        txtAddress.setText(address);
        txtReservationDate.setText(reservationDate);
        txtTotalPrice.setText(strTotalPrice + "원");

        btnPayment.setText(strTotalPrice + "원 결제하기");

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(strCartId, strTotalPrice, address, receiveType, reservationDate, token);
            }
        });
    }

    private void showConfirmationDialog(String strCartId, String strTotalPrice, String address, String receiveType, String reservationDate, String token) {
        new AlertDialog.Builder(PaymentActivity.this)
                .setTitle("결제 확인")
                .setMessage("결제하시겠습니까?\n결제 후 주문 취소 및 변경이 어렵습니다.")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 결제 진행
                        processPayment(strCartId, strTotalPrice, address, receiveType, reservationDate, token);
                    }
                })
                .setNegativeButton("아니오", null)
                .setIcon(R.drawable.logo)
                .show();
    }

    private void processPayment(String strCartId, String strTotalPrice, String address, String receiveType, String reservationDate, String token) {
        // 주문 내역 저장
        int cartId = Integer.parseInt(strCartId);
        int totalPrice = Integer.parseInt(strTotalPrice);
        String comment = editContent.getText().toString().trim();
        OrderRequset orderRequset = new OrderRequset(cartId, totalPrice, "무통장입금", comment, address, "상품 준비중", receiveType, reservationDate);

        Retrofit retrofit = NetworkClient.getRetrofitClient(PaymentActivity.this);
        OrderApi api = retrofit.create(OrderApi.class);
        Call<OrderRes> call = api.addOrder("Bearer " + token, orderRequset);
        call.enqueue(new Callback<OrderRes>() {
            @Override
            public void onResponse(Call<OrderRes> call, Response<OrderRes> response) {
                if (response.isSuccessful()) {
                    OrderRes orderRes = response.body();
                    String orderNumber = orderRes.orderNumber;

                    // 결제 후 장바구니 상태 변경
                    Retrofit retrofit = NetworkClient.getRetrofitClient(PaymentActivity.this);
                    CartApi api = retrofit.create(CartApi.class);
                    Call<Res> call2 = api.updateCartStatus("Bearer " + token, cartId);
                    call2.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(PaymentActivity.this, SuccessPaymentActivity.class);
                                intent.putExtra("orderNumber", orderNumber);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Res> call, Throwable throwable) {
                            Toast.makeText(PaymentActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<OrderRes> call, Throwable throwable) {
                Toast.makeText(PaymentActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
