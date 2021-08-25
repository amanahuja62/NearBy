package com.example.nearby;

import com.example.nearby.model.Coupon;
import com.example.nearby.model.DeleteResponse;
import com.example.nearby.model.LoginData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface CouponAPI {
    @Headers({"key: password"} )
    @POST("/api/coupons/create-coupon")
    Call<Coupon> postCoupon(@Body Coupon coupon);


    @Headers({"key: password"} )
    @GET("/api/coupons/all-coupons")
    Call<List<Coupon>> getAllCoupons();

    @Headers({"key: password"} )
    @GET
    Call<List<Coupon>> getSelectedCoupons(@Url String url);

    @Headers({"key: password"} )
    @PUT("/api/coupons/coupon/{id}")
    Call<Coupon> putCoupon(@Path("id") long id, @Body Coupon coupon);

    @Headers({"key: password"} )
    @DELETE("/api/coupons/coupon/{id}")
    Call<DeleteResponse> deleteCoupon(@Path("id") long id);

    @Headers({"key: password"} )
    @GET("/api/coupons/coupon/{id}")
    Call<Coupon> getCouponHavingID(@Path("id") long id);

    @Headers({"key: password"} )
    @PUT("/api/coupons/update-coupon/{couponId}/{userId}")
    Call<Coupon> updateLikes(@Path("couponId")long couponId, @Path("userId")long userId);


}
