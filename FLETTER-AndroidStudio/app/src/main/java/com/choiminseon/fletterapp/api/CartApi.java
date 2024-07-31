package com.choiminseon.fletterapp.api;

import com.choiminseon.fletterapp.model.CartCheckRes;
import com.choiminseon.fletterapp.model.CartRequest;
import com.choiminseon.fletterapp.model.CartRes;
import com.choiminseon.fletterapp.model.Res;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApi {


    // 장바구니 조회 API
    @GET("/cart")
    Call<CartRes> getCartList(@Header("Authorization") String token);


    // 장바구니 삭제 API
    @DELETE("/cart/{cartId}")
    Call<Res> deleteCart(@Path("cartId") int cartId,
                         @Header("Authorization") String token);

    // 장바구니 주문 수량 1 증가하는 api
    @PUT("/cartadd/{cartId}")
    Call<Res> addCartQuantity(@Path("cartId") int cartId,
                              @Header("Authorization") String token);

    // 장바구니 주문 수량 1 감소하는 api
    @PUT("/cartsub/{cartId}")
    Call<Res> subCartQuantity(@Path("cartId") int cartId,
                              @Header("Authorization") String token);


    // 장바구니 추가 API
    @POST("/cart")
    Call<Res> addCart(@Header("Authorization") String token,
                      @Body CartRequest cartRequest);

    // 장바구니 상태 변경 API
    @PUT("/cart/{cartId}")
    Call<Res> updateCartStatus(@Header("Authorization") String token,
                               @Path("cartId") int cartId);

    // 장바구니에 상품이 존재하는지 확인하는 API
    @GET("/cartStatus")
    Call<CartCheckRes> checkCartCount(@Header("Authorization") String token);
}
