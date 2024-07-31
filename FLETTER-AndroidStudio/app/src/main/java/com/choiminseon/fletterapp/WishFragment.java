package com.choiminseon.fletterapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.choiminseon.fletterapp.adapter.WishAdapter;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.api.WishApi;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.Wish;
import com.choiminseon.fletterapp.model.WishList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WishFragment extends Fragment {

    RecyclerView recyclerView;
    WishAdapter adapter;
    ArrayList<Wish> wishArrayList = new ArrayList<>();
    String token;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WishFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WishFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WishFragment newInstance(String param1, String param2) {
        WishFragment fragment = new WishFragment();
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
        View view = inflater.inflate(R.layout.fragment_wish, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");

        recyclerView = view.findViewById(R.id.recyclerViewFlower);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));





        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getNetworkData();
    }

    public void getNetworkData() {

        wishArrayList.clear();

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        WishApi api = retrofit.create(WishApi.class);
        Call<WishList> call = api.getWishList("Bearer " + token);

        call.enqueue(new Callback<WishList>() {
            @Override
            public void onResponse(Call<WishList> call, Response<WishList> response) {

                if (response.isSuccessful()) {

                    WishList wishList = response.body();

                    // 비어 있는 ArrayList에 받아온 리스트를 담아준다.
                    wishArrayList.addAll(wishList.items);
                    Log.i("WISH DATA", wishArrayList.toString());

                    // 데이터가 준비 완료 되었으니,
                    // 어댑터 만들어서, 리사이클러뷰에 적용한다.
                    adapter = new WishAdapter(getActivity(), wishArrayList);
                    recyclerView.setAdapter(adapter);


                } else {
                    Toast.makeText(getActivity(), "네트워크 에러", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WishList> call, Throwable throwable) {
                Toast.makeText(getActivity(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });

    }




}