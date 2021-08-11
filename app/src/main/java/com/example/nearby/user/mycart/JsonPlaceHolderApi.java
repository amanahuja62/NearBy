package com.example.nearby.user.mycart;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {
    @GET("api/coupons/coupon/6")
    Call<Post> getPosts();
    // Call<List<Post>> getPosts();
}
