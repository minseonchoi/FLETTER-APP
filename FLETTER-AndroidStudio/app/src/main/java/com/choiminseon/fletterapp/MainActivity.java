package com.choiminseon.fletterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.choiminseon.fletterapp.adapter.TodayFlowerAdapter;
import com.choiminseon.fletterapp.config.Config;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {


    BottomNavigationView bottomNavigationView;
    Fragment homeFragment;
    Fragment orderHistoryFragment;
    Fragment categoryFragment;
    Fragment wishFragment;
    Fragment myPageFragment;
    String token;

    private Fragment currentFragment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 기본 액션바 설정
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(R.layout.main_custom_action_bar);

            View customView = actionBar.getCustomView();

            // Shopping cart button 클릭 리스너 설정
            ImageButton shoppingCartButton = customView.findViewById(R.id.shoppingCartButton);
            shoppingCartButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            });
        }


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        homeFragment = new HomeFragment();
        orderHistoryFragment = new OrderHistoryFragment();
        categoryFragment = new CategoryFragment();
        wishFragment = new WishFragment();
        myPageFragment = new MyPageFragment();

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");

        if (token.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

//        // 기본 프래그먼트 설정
//        if (savedInstanceState == null) {
//            HomeFragment homeFragment = new HomeFragment();
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment, homeFragment, "홈")
//                    .commit();
//            currentFragment = homeFragment;
//        }

        // Fragment 초기화
        homeFragment = new HomeFragment();
        TopTodayFlowerFragment topTodayFlowerFragment = new TopTodayFlowerFragment();

        // 기본 프래그먼트 설정
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, homeFragment)
                    .commit();
        }

        // 바텀 네비게이션 눌렀을때 프래그먼트 영역 바꾸기
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int itemId = menuItem.getItemId();
                Fragment fragment = null;

                if (itemId == R.id.homeFragment) {
                    fragment = homeFragment;
                } else if (itemId == R.id.orderHistoryFragment) {
                    fragment = orderHistoryFragment;
                } else if (itemId == R.id.categoryFragment) {
                    fragment = categoryFragment;
                } else if (itemId == R.id.wishFragment) {
                    fragment = wishFragment;
                } else if (itemId == R.id.myPageFragment) {
                    fragment = myPageFragment;
                }
                return loadFragment(fragment);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String targetFragment = intent.getStringExtra("targetFragment");
            if ("OrderHistoryFragment".equals(targetFragment)) {
                bottomNavigationView.setSelectedItemId(R.id.orderHistoryFragment);
            }
        }




    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
            return true;
        }else {
            return false;
        }
    }

    public void setBottomNavigationViewSelectedItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }

    public void switchFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (existingFragment != null) {
            transaction.show(existingFragment);
        } else {
            transaction.add(R.id.fragment, fragment, tag);
        }

        currentFragment = fragment;
        transaction.addToBackStack(tag);
        transaction.commit();
    }


}