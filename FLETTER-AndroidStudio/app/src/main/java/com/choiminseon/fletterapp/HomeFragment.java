package com.choiminseon.fletterapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    public TabLayout tabLayout;
    private FrameLayout tabContentFrame;
    private Fragment currentFragment;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        tabContentFrame = view.findViewById(R.id.tabContentFrame);

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("오늘의 꽃"));
        tabLayout.addTab(tabLayout.newTab().setText("꽃 예측"));
        tabLayout.addTab(tabLayout.newTab().setText("주문"));
        tabLayout.addTab(tabLayout.newTab().setText("매장 정보"));

        // 기본으로 첫 번째 탭 선택
        loadFragment(new TopTodayFlowerFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new TopTodayFlowerFragment();
                        getChildFragmentManager().beginTransaction().replace(R.id.tabContentFrame, selectedFragment).commit();
                        break;
                    case 1:
                        selectedFragment = new TopPredictFragment();
                        break;
                    case 2:
                        selectedFragment = new TopOrderFragment();
                        break;
                    case 3:
                        selectedFragment = new TopInfoFragment();
                        break;
                }
                if (selectedFragment != null) {
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.tabContentFrame, selectedFragment);
                    transaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        return view;
    }


    public void selectTab(int tabIndex) {
        tabLayout.getTabAt(tabIndex).select();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.tabContentFrame, fragment);
        transaction.commit();
        currentFragment = fragment;
    }

    // TopTodayFlowerFragment를 표시하는 메서드
    public void showTopTodayFlowerFragment() {
        loadFragment(new TopTodayFlowerFragment());
    }

}