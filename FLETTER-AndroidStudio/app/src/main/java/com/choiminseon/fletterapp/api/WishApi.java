package com.choiminseon.fletterapp.api;



import com.choiminseon.fletterapp.model.Res;
import com.choiminseon.fletterapp.model.WishList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WishApi {


    // 위시리스트 가져오는 api
    @GET("/wish")
    Call<WishList> getWishList(@Header("Authorization") String token);

    // 관심 취소하는 api
    @DELETE("/flower/{flower_id}/like")
    Call<Res> deleteWishList(@Path("flower_id") int flowerId,
                             @Header("Authorization") String token);

    // 관심 등록하는 api
    @POST("/flower/{flowerId}/like")
    Call<Res> addWishList(@Path("flowerId") int flowerId,
                          @Header("Authorization") String token);
}
