package com.choiminseon.fletterapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.choiminseon.fletterapp.adapter.ChatAdapter;
import com.choiminseon.fletterapp.api.FlowerApi;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.model.AiRecommend;
import com.choiminseon.fletterapp.model.AiRecommendRes;
import com.choiminseon.fletterapp.model.ChatMessage;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AiRecommendActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> chatMessages;

    String reason;
    String packageType;
    String combination;
    String aiRecommendMsg;
    String recommendedFlowers;
    LottieAnimationView animationView;
    TextView txtMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_recommend);

        recyclerView = findViewById(R.id.recyclerView);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, this::onOptionSelected);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        animationView = findViewById(R.id.animationView);
        txtMsg = findViewById(R.id.txtMsg);

        // 처음 메시지
        showBotMessageWithOptions("안녕하세요 :)\n저는 AI 플로리스트 입니다.\n고객님 저희 FLETTER를 찾아주셔서 감사합니다.\n" +
                        "꽃 조합이 어려우신가요?\n제가 고객님에게 맞게 조합을 추천해드려요!\n\n꽃 조합이 필요하신가요?",
                new String[]{"네, 필요해요!", "아니요, 필요없어요."});
    }

    private void showBotMessage(String message) {
        chatMessages.add(new ChatMessage(message, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void showUserMessage(String message) {
        chatMessages.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void showBotMessageWithOptions(String message, String[] options) {
        chatMessages.add(new ChatMessage(message, false, options));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void onOptionSelected(String option) {
        showUserMessage(option);

        // 다음 챗봇 메시지 로직
        if (option.equals("네, 필요해요!")) {

            showBotMessageWithOptions("추천해드릴게요! 오늘 준비된 꽃으로 조합해드립니다. 꽃을 구매하시는 이유는 무엇일까요?",
                    new String[]{"축하", "성공", "사랑", "위로"});

        } else if (option.equals("아니요, 필요없어요.")) {

            showBotMessageWithOptions("알겠습니다. 꽃 조합 추천이 필요하시면 말씀해주세요!", new String[]{"채팅방 나가기", "다시 꽃 조합 추천 받기"});

        } else if (option.equals("채팅방 나가기")) {

            onBackPressed();

        } else if (option.equals("다시 꽃 조합 추천 받기")) {

            // 화면 지우고 다시 처음부터

        } else if (option.equals("축하") || option.equals("성공") || option.equals("사랑") || option.equals("위로")) {

            reason = option;
            showBotMessageWithOptions("꽃 포장 종류를 선택해주세요.", new String[]{"한송이", "꽃다발", "꽃바구니"});

        } else if (option.equals("한송이")) {

            packageType = option;
            combination = "1개";
            getAiRecommendMsg();

        } else if (option.equals("꽃다발") || option.equals("꽃바구니")) {

            packageType = option;
            showBotMessageWithOptions("몇 개의 꽃으로 조합을 할까요? 최소 1개부터 최대 3개까지 가능합니다.", new String[]{"1개", "2개", "3개"});

        } else if (option.equals("1개")) {

            combination = option;
            getAiRecommendMsg();

        } else if (option.equals("2개")) {

            combination = option;
            getAiRecommendMsg();

        } else if (option.equals("3개")) {

            combination = option;
            getAiRecommendMsg();

        } else if (option.equals("추천받은 꽃 조합으로 주문하기")) {

            Intent intent = new Intent(AiRecommendActivity.this, OrderOptionActivity.class);
            intent.putExtra("recommendedFlowers", recommendedFlowers);
            if (packageType.equals("한송이")) {
                intent.putExtra("packageId", 1);
            } else if (packageType.equals("꽃다발")) {
                intent.putExtra("packageId", 2);
            } else if (packageType.equals("꽃바구니")) {
                intent.putExtra("packageId", 3);
            }
            startActivity(intent);
            finish();

        }

    }

    private void getAiRecommendMsg() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(AiRecommendActivity.this);
        FlowerApi api = retrofit.create(FlowerApi.class);

        animationView.setVisibility(View.VISIBLE);
        txtMsg.setVisibility(View.VISIBLE);


        AiRecommend aiRecommend = new AiRecommend(reason, packageType, combination);

        Call<AiRecommendRes> call = api.recommendedAi(aiRecommend);
        call.enqueue(new Callback<AiRecommendRes>() {
            @Override
            public void onResponse(Call<AiRecommendRes> call, Response<AiRecommendRes> response) {
                animationView.setVisibility(View.GONE);
                txtMsg.setVisibility(View.GONE);
                if (response.isSuccessful()) {

                    AiRecommendRes aiRecommendRes = response.body();
                    aiRecommendMsg = aiRecommendRes.responseMessage;
                    recommendedFlowers = aiRecommendRes.responseServer;

                    showBotMessageWithOptions(aiRecommendMsg + " 추천이 마음에 드시나요?", new String[]{"추천받은 꽃 조합으로 주문하기", "다시 추천 받기", "채팅방 나가기"});
                }
            }

            @Override
            public void onFailure(Call<AiRecommendRes> call, Throwable throwable) {
                animationView.setVisibility(View.GONE);
                txtMsg.setVisibility(View.GONE);
                Toast.makeText(AiRecommendActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}