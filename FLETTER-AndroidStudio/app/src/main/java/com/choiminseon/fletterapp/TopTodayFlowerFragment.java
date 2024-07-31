package com.choiminseon.fletterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.choiminseon.fletterapp.adapter.TodayFlowerAdapter;
import com.choiminseon.fletterapp.api.FlowerApi;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.Flower;
import com.choiminseon.fletterapp.model.FlowerList;
import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TopTodayFlowerFragment extends Fragment {

    RecyclerView recyclerView;
    TodayFlowerAdapter adapter;
    ArrayList<Flower> flowerArrayList = new ArrayList<>();

    // Lottie Animation View
    LottieAnimationView animationView;
    String token;
    Button btnAiRecommend;

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TopTodayFlowerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopTodayFlowerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopTodayFlowerFragment newInstance(String param1, String param2) {
        TopTodayFlowerFragment fragment = new TopTodayFlowerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_today_flower, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");

        recyclerView = view.findViewById(R.id.recyclerViewFlower);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Lottie Animation View
        animationView = view.findViewById(R.id.animationView);

        btnAiRecommend = view.findViewById(R.id.btnAiRecommend);

        btnAiRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AiRecommendActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getNetworkData();
    }

    public void getNetworkData() {
        // 로딩 애니메이션 시작
        animationView.setVisibility(View.VISIBLE);

        long startTime = System.currentTimeMillis(); // 시작 시간

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        FlowerApi api = retrofit.create(FlowerApi.class);
        Call<FlowerList> call = api.getTodayFlowerList("Bearer " + token);

        call.enqueue(new Callback<FlowerList>() {

            @Override
            public void onResponse(Call<FlowerList> call, Response<FlowerList> response) {
                long endTime = System.currentTimeMillis(); // 종료 시간
                long elapsedTime = endTime - startTime;

                long minimumDisplayTime = 1500; // 최소 1.5초간 표시
                if (elapsedTime < minimumDisplayTime) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animationView.setVisibility(View.GONE);
                        }
                    }, minimumDisplayTime - elapsedTime);
                } else {
                    animationView.setVisibility(View.GONE);
                }

                if (response.isSuccessful()) {
                    FlowerList flowerList = response.body();

                    flowerArrayList.clear();
                    flowerArrayList.addAll(flowerList.items);

                    // 어댑터가 아직 설정되지 않았다면 새로 생성
                    if (adapter == null) {
                        adapter = new TodayFlowerAdapter(getActivity(), flowerArrayList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // 기존 어댑터가 있다면 데이터 변경
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onFailure(Call<FlowerList> call, Throwable throwable) {
                animationView.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
