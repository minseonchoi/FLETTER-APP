package com.choiminseon.fletterapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.choiminseon.fletterapp.adapter.OrderHistoryAdapter;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.api.OrderApi;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.OrderHistory;
import com.choiminseon.fletterapp.model.OrderHistoryList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderHistoryFragment extends Fragment {

    String token;
    RecyclerView recyclerView;
    OrderHistoryAdapter adapter;
    ArrayList<OrderHistory> orderHistoryArrayList = new ArrayList<>();
    TextView textViewOrder;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderHistoryFragment newInstance(String param1, String param2) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");

        textViewOrder = view.findViewById(R.id.textViewOrder);
        recyclerView = view.findViewById(R.id.recyclerViewFlower);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getNetworkData();

        return view;
    }


    public void getNetworkData() {

        orderHistoryArrayList.clear();

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        OrderApi api = retrofit.create(OrderApi.class);
        Call<OrderHistoryList> call = api.getOrderHistory("Bearer " + token);

        call.enqueue(new Callback<OrderHistoryList>() {
            @Override
            public void onResponse(Call<OrderHistoryList> call, Response<OrderHistoryList> response) {
                if (response.isSuccessful()) {
                    OrderHistoryList orderHistoryList = response.body();

                    orderHistoryArrayList.addAll(orderHistoryList.orderList);

                    adapter = new OrderHistoryAdapter(getActivity(), orderHistoryArrayList);
                    recyclerView.setAdapter(adapter);

                    // Check if the list is empty
                    if (orderHistoryArrayList.isEmpty()) {
                        textViewOrder.setText("주문내역이 비어있습니다.");
                    } else {
                        textViewOrder.setText("주문 내역 확인");
                    }
                }

            }

            @Override
            public void onFailure(Call<OrderHistoryList> call, Throwable throwable) {
                Toast.makeText(getActivity(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });

    }

}