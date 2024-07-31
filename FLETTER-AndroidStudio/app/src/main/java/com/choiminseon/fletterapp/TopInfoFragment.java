package com.choiminseon.fletterapp;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

public class TopInfoFragment extends Fragment {
    private TextView txtNav;
    private LottieAnimationView animationView;

    // 로케이션 매니저, 리스너 사용
    private LocationManager locationManager;
    private LocationListener locationListener;
    // 사용자 위치 멤버변수로 받기
    private Double currentLat;
    private Double currentLng;

    public TopInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Do something with arguments if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_info, container, false);

        txtNav = view.findViewById(R.id.txtNav);
        animationView = view.findViewById(R.id.animationView);  // LottieAnimationView 추가

        // 위치 관리자 객체 가져오기
        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

        // 길찾기로 이동
        txtNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 위치 권한 확인 및 요청
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                } else {
                    // 위치 정보 요청
                    startLoadingAnimation();
                    Toast.makeText(getActivity(), "현재 위치를 요청 중입니다. 잠시만 기다려주세요...", Toast.LENGTH_SHORT).show();
                    requestLocationUpdates();
                }
            }
        });

        return view;
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
                Toast.makeText(getContext(), "위치 제공자가 비활성화되었습니다. 설정에서 활성화해주세요.", Toast.LENGTH_SHORT).show();
            }
        };

        // 위치 업데이트 요청 (최소 시간 간격: 5초, 최소 거리: 0미터)
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 허용하시겠습니까? 물어보는 것
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    private void openKakaoMap() {
        try {
            // 내가 설정한 도착지 좌표, 매장 위치 위도는 37.54130563627117, 경도는 126.67636881196708
            Double endLat = 37.54130563627117;
            Double endLng = 126.67636881196708;

            // URL Scheme
            String url = "kakaomap://route?sp=" + currentLat + "," + currentLng + "&ep=" + endLat + "," + endLng + "&by=PUBLICTRANSIT";

            if (currentLat == 0.0 || currentLng == 0.0) {
                Toast.makeText(getContext(), "현재 위치를 가져오는 중입니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // 카카오 맵이 설치되어 있지 않은 경우 처리
            Toast.makeText(getContext(), "카카오 맵이 설치되어 있지 않습니다.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), "현재 위치를 요청 중입니다. 잠시만 기다려주세요...", Toast.LENGTH_SHORT).show();
                requestLocationUpdates();
            } else {
                // 사용자가 권한을 거부함
                Toast.makeText(getContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 프래그먼트 뷰가 파괴될 때 위치 업데이트 중지
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
