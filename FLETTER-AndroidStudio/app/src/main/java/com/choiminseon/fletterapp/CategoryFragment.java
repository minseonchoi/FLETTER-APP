package com.choiminseon.fletterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.choiminseon.fletterapp.config.Config;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
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
        View view =  inflater.inflate(R.layout.fragment_category, container, false);

        TextView txtLoginRegister = view.findViewById(R.id.txtLoginRegister);
        TextView txtProfile = view.findViewById(R.id.txtProfile);
        TextView txtWishList = view.findViewById(R.id.txtWishList);
        TextView txtCart = view.findViewById(R.id.txtCart);
        TextView txtOrderHistory = view.findViewById(R.id.txtOrderHistory);
        TextView txtFlower = view.findViewById(R.id.textView);
        TextView txtRecommend = view.findViewById(R.id.txtRecommend);
        TextView txtPredict = view.findViewById(R.id.txtPredict);
        TextView txtInfo = view.findViewById(R.id.txtInfo);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        // 로그인 프래그먼트로 이동
        txtLoginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (token.isEmpty()) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "이미 로그인 되어 있습니다.", Toast.LENGTH_SHORT).show();
                } 
            }
        });


        // 프로필 프래그먼트로 이동
        txtProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Fragment profileFragment = new ProfileFragment();
//                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment, profileFragment);
//                transaction.addToBackStack(null);  // 뒤로 가기 스택에 추가
//                transaction.commit();

                // 바텀내비게이션 상태 업데이트 + 프로필 프래그먼트로 이동
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    mainActivity.switchFragment(profileFragment, "profile");
                    mainActivity.setBottomNavigationViewSelectedItem(R.id.myPageFragment);
                }
            }
        });

        // 위시리스트 프래그먼트로 이동
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


        // 장바구니로 이동
        txtCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });

        // 주문내역으로 이동
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

        // 오늘의 꽃 확인하기로 이동
        txtFlower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    HomeFragment homeFragment = new HomeFragment();
                    mainActivity.switchFragment(homeFragment, "오늘의 꽃");
                    mainActivity.setBottomNavigationViewSelectedItem(R.id.homeFragment);
                }
            }
        });

        // ai꽃 조합 추천으로 이동
        txtRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AiRecommendActivity.class);
                startActivity(intent);
            }
        });
        


        // 꽃 이름 예측하기로 이동
        txtPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PredictActivity.class);
                startActivity(intent);
            }
        });

        // 매장 안내 액티비티로 이동
        txtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InfoActivity.class);
                startActivity(intent);
            }
        });




        return view;
    }
}