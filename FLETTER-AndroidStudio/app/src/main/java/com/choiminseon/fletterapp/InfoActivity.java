package com.choiminseon.fletterapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;

public class InfoActivity extends AppCompatActivity {
    TextView txtNav;

    // 로케이션 매니저, 리스너 사용
    LocationManager locationManager;
    LocationListener locationListener;
    // 사용자 위치 멤버변수로 받기
    Double currentLat;
    Double currentLng;
    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

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

        txtNav = findViewById(R.id.txtNav);
        animationView = findViewById(R.id.animationView);

        // 위치 관리자 객체 가져오기
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 길찾기로 이동
        txtNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 위치 권한 확인 및 요청
                if (ActivityCompat.checkSelfPermission(InfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(InfoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(InfoActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            100);
                } else {
                    // 위치 정보 요청
                    startLoadingAnimation();
                    Toast.makeText(InfoActivity.this, "현재 위치를 요청 중입니다. 잠시만 기다려주세요...", Toast.LENGTH_SHORT).show();
                    requestLocationUpdates();
                }
            }
        });
    }

    // 위치 정보 요청 메서드
    private void requestLocationUpdates() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                // 위치가 변경될 때 호출됨
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                Log.i("GPS MAIN", "위도 : " + currentLat + ", 경도 : " + currentLng);

                // 위치를 성공적으로 받아온 후 업데이트 중지
                locationManager.removeUpdates(this);

                // 위치를 받아온 후에 맵을 열어야 함
                stopLoadingAnimation();
                openKakaoMap();
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        Log.i("GPS MAIN", "Location provider available.");
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.i("GPS MAIN", "Location provider out of service.");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.i("GPS MAIN", "Location provider temporarily unavailable.");
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("GPS MAIN", "Provider enabled: " + provider);

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("GPS MAIN", "Provider disabled: " + provider);
                Toast.makeText(InfoActivity.this, "위치 제공자가 비활성화되었습니다. 설정에서 활성화해주세요.", Toast.LENGTH_SHORT).show();
            }
        };

        // 위치 업데이트 요청 (최소 시간 간격: 5초, 최소 거리: 0미터)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // 허용하시겠습니까? 물어보는 것
            ActivityCompat.requestPermissions(InfoActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    private void openKakaoMap() {

        try {
            // 내가 설정한 도착지 좌표, 매장 위치 위도는 37.54130563627117, 경도는 126.67636881196708
            Double endLat = 37.54130563627117;
            Double endLng = 126.67636881196708;

            // URL Scheme
            String url = "kakaomap://route?sp=" + currentLat + "," + currentLng + "&ep=" + endLat + "," + endLng + "&by=PUBLICTRANSIT";

            if (currentLat == 0.0 || currentLng == 0.0) {
                Toast.makeText(InfoActivity.this, "현재 위치를 가져오는 중입니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // 카카오 맵이 설치되어 있지 않은 경우 처리
            Toast.makeText(InfoActivity.this, "카카오 맵이 설치되어 있지 않습니다.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인됨
                // 위치 정보 요청 다시 시작
                startLoadingAnimation();
                Toast.makeText(this, "현재 위치를 요청 중입니다. 잠시만 기다려주세요...", Toast.LENGTH_SHORT).show();
                requestLocationUpdates();
            } else {
                // 사용자가 권한을 거부함
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 위치 업데이트 중지
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void startLoadingAnimation() {
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
    }

    private void stopLoadingAnimation() {
        animationView.setVisibility(View.GONE);
        animationView.cancelAnimation();
    }
}