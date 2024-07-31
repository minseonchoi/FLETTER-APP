package com.choiminseon.fletterapp.api;

import com.choiminseon.fletterapp.model.ProfileRes;
import com.choiminseon.fletterapp.model.Res;
import com.choiminseon.fletterapp.model.User;
import com.choiminseon.fletterapp.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {

    // 로그인 API
    @POST("/user/login")
    Call<UserRes> login(@Body User user);

    // 회원 정보 가져오는 API
    @GET("/profile")
    Call<ProfileRes> profile(@Header("Authorization") String token);

    // 로그아웃 API
    @DELETE("/user/logout")
    Call<Res> logout(@Header("Authorization") String token);
}
