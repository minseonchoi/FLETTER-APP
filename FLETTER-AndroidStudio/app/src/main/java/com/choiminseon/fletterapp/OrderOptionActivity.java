package com.choiminseon.fletterapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.choiminseon.fletterapp.adapter.ImagePagerAdapter;
import com.choiminseon.fletterapp.adapter.OrderOptionFlowerAdapter;
import com.choiminseon.fletterapp.adapter.OrderOptionSizeAdapter;
import com.choiminseon.fletterapp.api.CartApi;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.api.OrderApi;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.CartCheckRes;
import com.choiminseon.fletterapp.model.CartRequest;
import com.choiminseon.fletterapp.model.OrderOptionFlower;
import com.choiminseon.fletterapp.model.OrderOptionList;
import com.choiminseon.fletterapp.model.OrderOptionPackage;
import com.choiminseon.fletterapp.model.OrderOptionSize;
import com.choiminseon.fletterapp.model.Res;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderOptionActivity extends AppCompatActivity{

    ViewPager2 viewPager2;
    ImagePagerAdapter imagePagerAdapter;
    ArrayList<OrderOptionPackage> orderOptionPackageArrayList = new ArrayList<>();

    TextView txtPackageType;
    TextView txtPackagePrice;
    Button btnRecommend;
    TextView textView15;
    DotsIndicator dotsIndicator;
    Button btnTotalPrice;

    ArrayList<OrderOptionSize> orderOptionSizeArrayList = new ArrayList<>();
    OrderOptionSizeAdapter orderOptionSizeAdapter;
    RecyclerView recyclerViewSize;

    ArrayList<OrderOptionFlower> orderOptionFlowerArrayList = new ArrayList<>();
    OrderOptionFlowerAdapter orderOptionFlowerAdapter;
    RecyclerView recyclerViewFlower;

    LottieAnimationView loadingAnimationView;
    String token;
    String recommendedFlowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_option);

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

        viewPager2 = findViewById(R.id.viewPager2);
        txtPackagePrice = findViewById(R.id.txtPackagePrice);
        txtPackageType = findViewById(R.id.txtPackageType);
        textView15 = findViewById(R.id.textView15);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        btnRecommend = findViewById(R.id.btnRecommend);
        btnTotalPrice = findViewById(R.id.btnTotalPrice);

        recyclerViewSize = findViewById(R.id.recyclerViewSize);
        recyclerViewSize.setHasFixedSize(true);
        recyclerViewSize.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewFlower = findViewById(R.id.recyclerViewFlower);
        recyclerViewFlower.setHasFixedSize(true);
        recyclerViewFlower.setLayoutManager(new LinearLayoutManager(this));

        loadingAnimationView = findViewById(R.id.loadingAnimationView);

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");

        Intent intent = getIntent();
        int packageId = intent.getIntExtra("packageId", 0);
        Log.i("PACKAGEID", String.valueOf(packageId));

        // packageId에 따라 각각의 API 호출을 수행합니다.
        switch (packageId) {
            case 1:
                showLoadingAnimation(); // 로딩 애니메이션 보이기
                getOneFlower();
                break;
            case 2:
                showLoadingAnimation(); // 로딩 애니메이션 보이기
                getBunchFlower();
                break;
            case 3:
                showLoadingAnimation(); // 로딩 애니메이션 보이기
                getBasketFlower();
                break;
            case 4:
                showLoadingAnimation(); // 로딩 애니메이션 보이기
                getWreathFlower();
                break;
            default:
                Toast.makeText(this, "잘못된 packageId입니다.", Toast.LENGTH_SHORT).show();
                break;
        }

        recommendedFlowers = intent.getStringExtra("recommendedFlowers");

        btnRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recommendedFlowers == null) {
                    showPopupWindow(v, "AI 플로리스트에게 추천을 받지 않으셨습니다.");
                } else {
                    showPopupWindow(v, "AI 플로리스트에게 추천받은 꽃은 " + recommendedFlowers + " 입니다.");
                }
            }
        });


        btnTotalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int selectedSizeId;

                if (packageId == 1) {
                    selectedSizeId = 1;
                } else {
                    selectedSizeId = orderOptionSizeAdapter.getSelectedSizeId(); // sizeId 준비
                }



                if (selectedSizeId == -1) {
                    Toast.makeText(OrderOptionActivity.this, "사이즈를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<Integer> selectedFlowerId = orderOptionFlowerAdapter.getSelectedFlowerIds();
                if (selectedFlowerId.isEmpty()) {
                    Toast.makeText(OrderOptionActivity.this, "꽃을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Retrofit retrofit = NetworkClient.getRetrofitClient(OrderOptionActivity.this);
                CartApi api = retrofit.create(CartApi.class);

                Call<CartCheckRes> call = api.checkCartCount("Bearer " + token);

                call.enqueue(new Callback<CartCheckRes>() {
                    @Override
                    public void onResponse(Call<CartCheckRes> call, Response<CartCheckRes> response) {
                        if (response.isSuccessful()) {

                            CartCheckRes cartCheckRes = response.body();

                            // 장바구니가 비어있다면 장바구니에 추가
                            if (cartCheckRes.count == 0) {
                                Retrofit retrofit = NetworkClient.getRetrofitClient(OrderOptionActivity.this);
                                CartApi api = retrofit.create(CartApi.class);

                                CartRequest cartRequest = new CartRequest(packageId, selectedSizeId, 1, selectedFlowerId);

                                Call<Res> call2 = api.addCart("Bearer " + token, cartRequest);
                                call2.enqueue(new Callback<Res>() {
                                    @Override
                                    public void onResponse(Call<Res> call2, Response<Res> response) {
                                        if (response.isSuccessful()) {
                                            Intent intent = new Intent(OrderOptionActivity.this, CartActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Res> call2, Throwable throwable) {
                                        Toast.makeText(OrderOptionActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                // 장바구니가 비어있지 않다면 기존 장바구니를 삭제하고 새롭게 추가
                                // 또는 장바구니 추가 취소
                            } else {
                                showConfirmationDialog(cartCheckRes.cartId, packageId, selectedSizeId, selectedFlowerId);

                            }


                        }


                    }

                    @Override
                    public void onFailure(Call<CartCheckRes> call, Throwable throwable) {
                        Toast.makeText(OrderOptionActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }



    private void showConfirmationDialog(int cartId, int packageId, int selectedSizeId, ArrayList<Integer> selectedFlowerId) {
        new AlertDialog.Builder(OrderOptionActivity.this)
                .setTitle("장바구니 추가")
                .setMessage("장바구니에는 1개의 상품만 담을 수 있습니다.\n선택하신 메뉴를 장바구니에 담을 경우 이전에 담은 메뉴가 삭제됩니다.")
                .setPositiveButton("담기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 이전 장바구니 삭제하고 새로운 장바구니 추가
                        // 먼저 기존 장바구니 삭제
                        Retrofit retrofit = NetworkClient.getRetrofitClient(OrderOptionActivity.this);
                        CartApi api = retrofit.create(CartApi.class);

                        Call<Res> call = api.deleteCart(cartId, "Bearer " + token);
                        call.enqueue(new Callback<Res>() {
                            @Override
                            public void onResponse(Call<Res> call, Response<Res> response) {
                                if (response.isSuccessful()) {

                                    // 삭제 성공했으면 현재 상품 장바구니에 추가
                                    CartRequest cartRequest = new CartRequest(packageId, selectedSizeId, 1, selectedFlowerId);
                                    Call<Res> call2 = api.addCart("Bearer " + token, cartRequest);
                                    call2.enqueue(new Callback<Res>() {
                                        @Override
                                        public void onResponse(Call<Res> call, Response<Res> response) {
                                            if (response.isSuccessful()) {
                                                Intent intent = new Intent(OrderOptionActivity.this, CartActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Res> call, Throwable throwable) {
                                            Toast.makeText(OrderOptionActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onFailure(Call<Res> call, Throwable throwable) {
                                Toast.makeText(OrderOptionActivity.this, "네트워크 에러", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void showPopupWindow(View anchorView, String popupText) {
        // 팝업 윈도우의 레이아웃을 인플레이트합니다.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_info, null);

        // 팝업 윈도우를 생성합니다.
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // 텍스트뷰를 찾아서 텍스트를 설정합니다.
        TextView txtPopupInfo = popupView.findViewById(R.id.txtPopupInfo);
        txtPopupInfo.setText(popupText);

        // 외부 클릭을 감지하기 위한 설정
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 투명 배경 설정


        // 팝업 윈도우를 표시합니다.
        popupWindow.showAsDropDown(anchorView, -450, -30); // anchorView 바로 아래에 팝업을 표시합니다.

        // 팝업 외부를 클릭하면 팝업이 닫히도록 설정합니다.
        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });
    }


    private void showLoadingAnimation() {
        loadingAnimationView.setVisibility(View.VISIBLE);
        loadingAnimationView.setSpeed(0.1f);
        loadingAnimationView.playAnimation();
    }

    private void hideLoadingAnimation() {
        loadingAnimationView.setVisibility(View.GONE);
        loadingAnimationView.cancelAnimation();
    }

    private void getOneFlower() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(OrderOptionActivity.this);
        OrderApi api = retrofit.create(OrderApi.class);
        Call<OrderOptionList> call = api.getOrderOptionOneFlower("Bearer " + token);
        call.enqueue(new Callback<OrderOptionList>() {
            @Override
            public void onResponse(Call<OrderOptionList> call, Response<OrderOptionList> response) {
                hideLoadingAnimation(); // API 호출 완료 후 로딩 애니메이션 숨기기

                if (response.isSuccessful()) {
                    OrderOptionList orderOptionList = response.body();
                    orderOptionPackageArrayList.addAll(orderOptionList.packageInfo);
                    setupViewPager();

                    if (!orderOptionPackageArrayList.isEmpty()) {
                        OrderOptionPackage orderOptionPackage = orderOptionPackageArrayList.get(0);
                        txtPackageType.setText(orderOptionPackage.packagingType);
                        txtPackagePrice.setText(orderOptionPackage.packagePrice + " 원");
                    }

                    textView15.setVisibility(View.GONE);
                    recyclerViewSize.setVisibility(View.GONE);

                    orderOptionFlowerArrayList.addAll(orderOptionList.flowers);
                    // maxSelectableFlowers 값을 1로 설정
                    orderOptionFlowerAdapter = new OrderOptionFlowerAdapter(OrderOptionActivity.this, orderOptionFlowerArrayList, 1, OrderOptionActivity.this::updateTotalPrice);
                    recyclerViewFlower.setAdapter(orderOptionFlowerAdapter);

                    // ai에게 추천받아 주문옵션 액티비티로 이동할 경우
                    if (recommendedFlowers != "") {
                        orderOptionFlowerAdapter.setRecommendedFlowers(recommendedFlowers);
                        updateTotalPrice();
                    }


                } else {
                    Toast.makeText(OrderOptionActivity.this, "API 호출 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderOptionList> call, Throwable t) {
                hideLoadingAnimation(); // API 호출 실패 시 로딩 애니메이션 숨기기
                Toast.makeText(OrderOptionActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBunchFlower() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(OrderOptionActivity.this);
        OrderApi api = retrofit.create(OrderApi.class);
        Call<OrderOptionList> call = api.getOrderOptionBunchFlower("Bearer " + token);
        call.enqueue(new Callback<OrderOptionList>() {
            @Override
            public void onResponse(Call<OrderOptionList> call, Response<OrderOptionList> response) {
                hideLoadingAnimation(); // API 호출 완료 후 로딩 애니메이션 숨기기

                if (response.isSuccessful()) {
                    OrderOptionList orderOptionList = response.body();
                    orderOptionPackageArrayList.addAll(orderOptionList.packageInfo);
                    setupViewPager();

                    if (!orderOptionPackageArrayList.isEmpty()) {
                        OrderOptionPackage orderOptionPackage = orderOptionPackageArrayList.get(0);
                        txtPackageType.setText(orderOptionPackage.packagingType);
                        txtPackagePrice.setText(orderOptionPackage.packagePrice + " 원");
                    }

                    orderOptionSizeArrayList.addAll(orderOptionList.size);
                    orderOptionSizeAdapter = new OrderOptionSizeAdapter(OrderOptionActivity.this, orderOptionSizeArrayList, OrderOptionActivity.this::updateTotalPrice);
                    recyclerViewSize.setAdapter(orderOptionSizeAdapter);

                    orderOptionFlowerArrayList.addAll(orderOptionList.flowers);
                    // maxSelectableFlowers 값을 3으로 설정
                    orderOptionFlowerAdapter = new OrderOptionFlowerAdapter(OrderOptionActivity.this, orderOptionFlowerArrayList, 3, OrderOptionActivity.this::updateTotalPrice);
                    recyclerViewFlower.setAdapter(orderOptionFlowerAdapter);

                    // ai에게 추천받아 주문옵션 액티비티로 이동할 경우
                    if (recommendedFlowers != "") {
                        orderOptionFlowerAdapter.setRecommendedFlowers(recommendedFlowers);
                        updateTotalPrice();
                    }

                } else {
                    Toast.makeText(OrderOptionActivity.this, "API 호출 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderOptionList> call, Throwable t) {
                hideLoadingAnimation(); // API 호출 실패 시 로딩 애니메이션 숨기기
                Toast.makeText(OrderOptionActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBasketFlower() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(OrderOptionActivity.this);
        OrderApi api = retrofit.create(OrderApi.class);
        Call<OrderOptionList> call = api.getOrderOptionBasketFlower("Bearer " + token);
        call.enqueue(new Callback<OrderOptionList>() {
            @Override
            public void onResponse(Call<OrderOptionList> call, Response<OrderOptionList> response) {
                hideLoadingAnimation(); // API 호출 완료 후 로딩 애니메이션 숨기기

                if (response.isSuccessful()) {
                    OrderOptionList orderOptionList = response.body();
                    orderOptionPackageArrayList.addAll(orderOptionList.packageInfo);
                    setupViewPager();

                    if (!orderOptionPackageArrayList.isEmpty()) {
                        OrderOptionPackage orderOptionPackage = orderOptionPackageArrayList.get(0);
                        txtPackageType.setText(orderOptionPackage.packagingType);
                        txtPackagePrice.setText(orderOptionPackage.packagePrice + " 원");
                    }

                    orderOptionSizeArrayList.addAll(orderOptionList.size);
                    orderOptionSizeAdapter = new OrderOptionSizeAdapter(OrderOptionActivity.this, orderOptionSizeArrayList, OrderOptionActivity.this::updateTotalPrice);
                    recyclerViewSize.setAdapter(orderOptionSizeAdapter);

                    orderOptionFlowerArrayList.addAll(orderOptionList.flowers);
                    orderOptionFlowerAdapter = new OrderOptionFlowerAdapter(OrderOptionActivity.this, orderOptionFlowerArrayList, 3, OrderOptionActivity.this::updateTotalPrice);
                    recyclerViewFlower.setAdapter(orderOptionFlowerAdapter);

                    // ai에게 추천받아 주문옵션 액티비티로 이동할 경우
                    if (recommendedFlowers != "") {
                        orderOptionFlowerAdapter.setRecommendedFlowers(recommendedFlowers);
                        updateTotalPrice();
                    }

                } else {
                    Toast.makeText(OrderOptionActivity.this, "API 호출 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderOptionList> call, Throwable t) {
                hideLoadingAnimation(); // API 호출 실패 시 로딩 애니메이션 숨기기
                Toast.makeText(OrderOptionActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWreathFlower() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(OrderOptionActivity.this);
        OrderApi api = retrofit.create(OrderApi.class);
        Call<OrderOptionList> call = api.getOrderOptionWreathFlower("Bearer " + token);
        call.enqueue(new Callback<OrderOptionList>() {
            @Override
            public void onResponse(Call<OrderOptionList> call, Response<OrderOptionList> response) {
                hideLoadingAnimation(); // API 호출 완료 후 로딩 애니메이션 숨기기

                if (response.isSuccessful()) {
                    OrderOptionList orderOptionList = response.body();
                    orderOptionPackageArrayList.addAll(orderOptionList.packageInfo);
                    setupViewPager();

                    if (!orderOptionPackageArrayList.isEmpty()) {
                        OrderOptionPackage orderOptionPackage = orderOptionPackageArrayList.get(0);
                        txtPackageType.setText(orderOptionPackage.packagingType);
                        txtPackagePrice.setText(orderOptionPackage.packagePrice + " 원");
                    }

                    orderOptionSizeArrayList.addAll(orderOptionList.size);
                    orderOptionSizeAdapter = new OrderOptionSizeAdapter(OrderOptionActivity.this, orderOptionSizeArrayList, OrderOptionActivity.this::updateTotalPrice);
                    recyclerViewSize.setAdapter(orderOptionSizeAdapter);

                    orderOptionFlowerArrayList.addAll(orderOptionList.flowers);
                    orderOptionFlowerAdapter = new OrderOptionFlowerAdapter(OrderOptionActivity.this, orderOptionFlowerArrayList, 3, OrderOptionActivity.this::updateTotalPrice);
                    recyclerViewFlower.setAdapter(orderOptionFlowerAdapter);

                } else {
                    Toast.makeText(OrderOptionActivity.this, "API 호출 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderOptionList> call, Throwable t) {
                hideLoadingAnimation(); // API 호출 실패 시 로딩 애니메이션 숨기기
                Toast.makeText(OrderOptionActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewPager() {
        ArrayList<String> imageUrls = new ArrayList<>();
        for (OrderOptionPackage orderPackage : orderOptionPackageArrayList) {
            imageUrls.add(orderPackage.firstPackagePhotoUrl);
            imageUrls.add(orderPackage.secondPackagePhotoUrl);
            imageUrls.add(orderPackage.thridPackagePhotoUrl);
            imageUrls.add(orderPackage.forthPackagePhotoUrl);
        }
        imagePagerAdapter = new ImagePagerAdapter(OrderOptionActivity.this, imageUrls);
        viewPager2.setAdapter(imagePagerAdapter);
        dotsIndicator.setViewPager2(viewPager2);
    }

    private void updateTotalPrice() {
        int totalPrice = 0;

        totalPrice += Integer.parseInt(txtPackagePrice.getText().toString().replace("원", "").trim());

        for (OrderOptionSize size : orderOptionSizeArrayList) {
            if (size.isSelected) {
                totalPrice += size.sizePrice;
            }
        }

        for (OrderOptionFlower flower : orderOptionFlowerArrayList) {
            if (flower.isSelected) {
                totalPrice += flower.flowerPrice;
            }
        }

        btnTotalPrice.setText(totalPrice + "원 장바구니 담기");
    }

}
