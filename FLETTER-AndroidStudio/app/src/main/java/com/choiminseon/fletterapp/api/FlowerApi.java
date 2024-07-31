package com.choiminseon.fletterapp.api;

import com.choiminseon.fletterapp.model.AiRecommend;
import com.choiminseon.fletterapp.model.AiRecommendRes;
import com.choiminseon.fletterapp.model.FlowerList;
import com.choiminseon.fletterapp.model.PackageTypeList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FlowerApi {

    @GET("/flowers")
    Call<FlowerList> getTodayFlowerList(@Header("Authorization") String token);

    @GET("/order/package")
    Call<PackageTypeList> getPackageList();

    @POST("/flowers/ai")
    Call<AiRecommendRes> recommendedAi(@Body AiRecommend aiRecommend);
}
