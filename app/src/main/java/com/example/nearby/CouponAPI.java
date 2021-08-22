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
    @Headers({"Key: key","Password: password"})
    @POST("/api/coupons/create-coupon")
    Call<Coupon> postCoupon(@Body Coupon coupon);



    @GET("/api/coupons/all-coupons")
    Call<List<Coupon>> getAllCoupons();

    @GET
    Call<List<Coupon>> getSelectedCoupons(@Url String url);

    @PUT("/api/coupons/coupon/{id}")
    Call<Coupon> putCoupon(@Path("id") long id, @Body Coupon coupon);

    @DELETE("/api/coupons/coupon/{id}")
    Call<DeleteResponse> deleteCoupon(@Path("id") long id);

    @GET("/api/coupons/coupon/{id}")
    Call<Coupon> getCouponHavingID(@Path("id") long id);

    @PUT("/api/coupons/update-coupon/{couponId}/{userId}")
    Call<Coupon> updateLikes(@Path("couponId")long couponId, @Path("userId")long userId);







}
