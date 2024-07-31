package com.choiminseon.fletterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.api.UserApi;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.ProfileRes;
import com.choiminseon.fletterapp.model.Res;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPageFragment extends Fragment {

    TextView txtUserName;
    TextView txtWishList;
    TextView txtCart;
    TextView txtOrderHistory;
    TextView txtLogout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPageFragment newInstance(String param1, String param2) {
        MyPageFragment fragment = new MyPageFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        txtUserName = view.findViewById(R.id.txtUserName);
        txtWishList = view.findViewById(R.id.txtWishList);
        txtCart = view.findViewById(R.id.txtCart);
        txtOrderHistory = view.findViewById(R.id.txtOrderHistory);
        txtLogout = view.findViewById(R.id.txtLogout);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        txtUserName.setText(username);

        String token = sp.getString("token", "");


        Button btnProfile = view.findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment profileFragment = new ProfileFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, profileFragment);
                transaction.addToBackStack(null);  // 뒤로 가기 스택에 추가
                transaction.commit();
            }
        });

        txtWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    WishFragment wishFragment = new WishFragment();
                    mainActivity.switchFragment(wishFragment, "위시리스트");
                    mainActivity.setBottomNavigationViewSelectedItem(R.id.wishFragment);
                }
            }
        });

        txtCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });

        txtOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    OrderHistoryFragment orderHistoryFragment = new OrderHistoryFragment();
                    mainActivity.switchFragment(orderHistoryFragment, "주문내역");
                    mainActivity.setBottomNavigationViewSelectedItem(R.id.orderHistoryFragment);
                }
            }
        });


        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
                UserApi api = retrofit.create(UserApi.class);
                Call<Res> call = api.logout("Bearer " + token);
                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        if (response.isSuccessful()) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", "");
                            editor.commit();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable throwable) {
                        Toast.makeText(getActivity(), "네트워크 오류", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}