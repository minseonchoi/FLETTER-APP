package com.choiminseon.fletterapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choiminseon.fletterapp.adapter.CartAdapter;
import com.choiminseon.fletterapp.api.CartApi;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.Cart;
import com.choiminseon.fletterapp.model.CartRes;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartActivity extends AppCompatActivity {

    NestedScrollView nestedScrollView;
    CheckBox checkBoxDelivery;
    CheckBox checkBoxPickUp;
    Button btnDay;
    Button btnTime;
    EditText editAddress;
    Button btnOrder;

    String token;

    RecyclerView recyclerView;
    ArrayList<Cart> cartArrayList = new ArrayList<>();
    CartAdapter adapter;
    TextView txtAddress;

    int totalPrice;
    String selectedDate;
    int cartId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");


        // isCartEmpty 메서드를 호출하여 결과를 처리합니다.
        isCartEmpty(new CartEmptyCallback() {
            @Override
            public void onResult(boolean isEmpty) {
                if (isEmpty) {
                    showEmptyCartLayout();
                } else {
                    showCartItemsLayout();
                }
            }
        });



    }





    private void showCartItemsLayout() {
        setContentView(R.layout.cart_items);

        nestedScrollView = findViewById(R.id.nestedScrollView);
        checkBoxDelivery = findViewById(R.id.checkBoxDelivery);
        checkBoxPickUp = findViewById(R.id.checkBoxPickUp);
        txtAddress = findViewById(R.id.txtAddress);
        btnDay = findViewById(R.id.btnDay);
        btnTime = findViewById(R.id.btnTime);
        btnOrder = findViewById(R.id.btnOrder);
        editAddress = findViewById(R.id.editAddress);
        recyclerView = findViewById(R.id.recyclerViewFlower);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtAddress.setVisibility(View.GONE);
        editAddress.setVisibility(View.GONE);

        // DatePickerDialog 설정
        btnDay.setOnClickListener(v -> {
            if (checkBoxDelivery.isChecked()) {
                showDatePickerDialog(7); // 현재 날짜로부터 일주일 후까지 선택 불가능
            } else if (checkBoxPickUp.isChecked()) {
                showDatePickerDialog(1); // 현재 날짜로부터 하루 후까지 선택 불가능
            }
        });

        // TimePickerDialog 설정
        btnTime.setOnClickListener(v -> showTimePickerDialog());

        // api 호출해서 장바구니 리스트 불러오기
        Retrofit retrofit = NetworkClient.getRetrofitClient(CartActivity.this);
        CartApi api = retrofit.create(CartApi.class);
        Call<CartRes> call = api.getCartList("Bearer " + token);
        call.enqueue(new Callback<CartRes>() {
            @Override
            public void onResponse(Call<CartRes> call, Response<CartRes> response) {

                if (response.isSuccessful()) {
                    CartRes cartRes = response.body();
                    cartArrayList.addAll(cartRes.cartList);
                    Log.i("CART DATA", cartArrayList.toString());
                    adapter = new CartAdapter(CartActivity.this, cartArrayList);
                    recyclerView.setAdapter(adapter);

                    cartId = cartRes.cartList.get(0).cartId;

                    Log.e("totalPrice","가격 : "+totalPrice);

                    // checkBoxDelivery가 기본으로 체크되었을 때 초기화
                    checkBoxDelivery.setChecked(true);
                    checkBoxPickUp.setChecked(false);
                    editAddress.setVisibility(View.VISIBLE);
                    txtAddress.setVisibility(View.VISIBLE);
                    btnOrder.setVisibility(View.VISIBLE);
                    btnOrder.setText(totalPrice + "원 ⦁ 배송 주문하기");

                    // 어댑터에 리스너 설정
                    adapter.setOnPriceChangeListener(totalPrice -> {
                        CartActivity.this.totalPrice = totalPrice;
                        btnOrder.setText(totalPrice + "원 ⦁ 배송 주문하기");
                        Log.e("totalPrice", "가격 : " + totalPrice);
                    });

                    // 초기 총 가격 계산 및 버튼 텍스트 설정
                    adapter.notifyDataSetChanged();  // 어댑터의 데이터를 다시 로드하여 총 가격을 계산합니다.
                }

            }

            @Override
            public void onFailure(Call<CartRes> call, Throwable throwable) {
                Toast.makeText(CartActivity.this, "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });


        // checkBox 하나만 선택하게 하기
        // 배송체크박스 눌렀을때
        checkBoxDelivery.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxPickUp.setChecked(false); // 다른체크박스 해제
                editAddress.setVisibility(View.VISIBLE); // EditText를 표시
                txtAddress.setVisibility(View.VISIBLE);
                scrollToView(editAddress);
                btnOrder.setVisibility(View.VISIBLE);
                btnOrder.setText(totalPrice + "원 ⦁ 배송 주문하기");
            } else if (!checkBoxPickUp.isChecked()) {
                editAddress.setVisibility(View.GONE); // 둘 다 체크 해제 시 editAddress 숨김
                txtAddress.setVisibility(View.GONE);
                btnOrder.setVisibility(View.GONE);
            }
        });

        // 픽업체크박스 눌렀을때
        checkBoxPickUp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxDelivery.setChecked(false);
                editAddress.setVisibility(View.GONE);
                txtAddress.setVisibility(View.GONE);
                btnOrder.setVisibility(View.VISIBLE);
                btnOrder.setText(totalPrice + "원 ⦁ 픽업 주문하기");
            } else if (!checkBoxDelivery.isChecked()) {
                editAddress.setVisibility(View.GONE);
                txtAddress.setVisibility(View.GONE);
                btnOrder.setVisibility(View.GONE);
            }
        });


        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이름, 연락처, 주소
                // 배송 예약 시간
                // 총 결제금액
                // 을 넘겨줘야됨

                String receiveType = checkBoxDelivery.isChecked() ? "배송" : "픽업"; // 수령방식
                String address = editAddress.getText().toString().trim();
                String reservationDate = btnDay.getText().toString() + " " + btnTime.getText().toString();

                if (reservationDate.contains("날짜")) {
                    Toast.makeText(CartActivity.this, "예약 날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (reservationDate.contains("시간")) {
                    Toast.makeText(CartActivity.this, "예약 시간을 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkBoxDelivery.equals("배송") && address.isEmpty()) {
                    Toast.makeText(CartActivity.this, "주소를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                intent.putExtra("receiveType", receiveType);
                intent.putExtra("address", address);
                intent.putExtra("reservationDate", reservationDate);
                intent.putExtra("totalPrice", totalPrice + "");
                intent.putExtra("cartId", cartId + "");
                startActivity(intent);


            }
        });



    }




    private void showEmptyCartLayout() {
        setContentView(R.layout.empty_cart);

    }



    // api 호출하여 장바구니 데이터를 가져오고 비어있는지 여부 판단
    private void isCartEmpty(CartEmptyCallback callback) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(CartActivity.this);
        CartApi api = retrofit.create(CartApi.class);
        Call<CartRes> call = api.getCartList("Bearer " + token);
        call.enqueue(new Callback<CartRes>() {
            @Override
            public void onResponse(Call<CartRes> call, Response<CartRes> response) {
                if (response.isSuccessful()) {
                    CartRes cartRes = response.body();
                    boolean isEmpty = cartRes.cartList.isEmpty();
                    callback.onResult(isEmpty);
                } else {
                    callback.onResult(true); // 실패한 경우 비어있다고 가정
                }
            }

            @Override
            public void onFailure(Call<CartRes> call, Throwable t) {
                Toast.makeText(CartActivity.this, "네트워크 에러", Toast.LENGTH_SHORT).show();
                callback.onResult(true); // 실패한 경우 비어있다고 가정
            }
        });
    }

    interface CartEmptyCallback {
        void onResult(boolean isEmpty);
    }




    // 액션바에 화살표를 표시하는 코드
    @Override
    public boolean onSupportNavigateUp() {
        // 다른작업이 있으면 여기에 코드 작성한다.

        finish();
        return true;
    }

    private void scrollToView(View view) {
        nestedScrollView.post(() -> {
            nestedScrollView.smoothScrollTo(0, view.getBottom());
        });
    }

//    // 총 가격을 계산하는 메서드
//    private int calculateTotalPrice() {
//        int totalPrice = 0;
//        for (Cart cart : cartArrayList) {
//            totalPrice += (cart.packagePrice + cart.sizePrice + cart.orderFlowerPrice) * cart.quantity;
//        }
//        return totalPrice;
//    }

    // btnDay클릭시 달력 띄우기
    private void showDatePickerDialog(int daysToDisable) {
        // 현재 날짜를 기본값으로 설정
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // 선택한 날짜로 버튼 텍스트 설정
                    selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    btnDay.setText(selectedDate);
                },
                year, month, day
        );

        // 현재 날짜로부터 지정된 일수(daysToDisable) 후까지는 선택 불가능하게 설정
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_YEAR, daysToDisable);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }


    private void showTimePickerDialog() {
        // 현재 시간을 기본값으로 설정
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // 선택한 시간으로 버튼 텍스트 설정
                    if (isValidTime(selectedHour, selectedMinute)) {
                        String selectedTime = selectedHour + ":" + String.format("%02d", selectedMinute);
                        btnTime.setText(selectedTime);
                    } else {
                        Toast.makeText(CartActivity.this, "09:00 ~ 19:30 시간만 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                },
                hour, minute, true // true로 설정하면 24시간 형식을 사용합니다.
        );

        timePickerDialog.show();
    }

    private boolean isValidTime(int hour, int minute) {
        // 09:00 ~ 19:30 시간만 유효
        if (hour < 9 || (hour == 19 && minute > 30) || hour > 19) {
            return false;
        }
        return true;

    }


}