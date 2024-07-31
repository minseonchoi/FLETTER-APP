package com.choiminseon.fletterapp.api;

import com.choiminseon.fletterapp.model.OrderHistoryDetailList;
import com.choiminseon.fletterapp.model.OrderHistoryList;
import com.choiminseon.fletterapp.model.OrderOptionList;
import com.choiminseon.fletterapp.model.OrderRequset;
import com.choiminseon.fletterapp.model.OrderRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderApi {

    // 주문정보 가져오는 API
    @GET("/order/list")
    Call<OrderHistoryList> getOrderHistory(@Header("Authorization") String token);



    // 주문 상세정보 가져오는 API
    @GET("/order/{orderId}")
    Call<OrderHistoryDetailList> getOrderHistoryDetail(@Path("orderId") int orderId,
                                                       @Header("Authorization") String token);


    // 한송이 옵션 가져오는 api
    @GET("/order/oneflower")
    Call<OrderOptionList> getOrderOptionOneFlower(@Header("Authorization") String token);

    // 꽃다발 옵션 가져오는 api
    @GET("/order/bunchflower")
    Call<OrderOptionList> getOrderOptionBunchFlower(@Header("Authorization") String token);

    // 꽃바구니 옵션 가져오는 api
    @GET("/order/basketflower")
    Call<OrderOptionList> getOrderOptionBasketFlower(@Header("Authorization") String token);

    // 화환 옵션 가져오는 api
    @GET("/order/wreathflower")
    Call<OrderOptionList> getOrderOptionWreathFlower(@Header("Authorization") String token);

    // 주문하는 API
    @POST("/orderList")
    Call<OrderRes> addOrder(@Header("Authorization") String token,
                            @Body OrderRequset orderRequset);


}
