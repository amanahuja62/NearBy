package com.example.nearby;

import com.example.nearby.model.Cart;
import com.example.nearby.model.Coupon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface CartAPI {
    @Headers({"Key: key","Password: password"})
    @GET
    Call<Cart> getUserCart(@Url String url);

    @Headers({"Key: key","Password: password"})
    @PUT("/api/cart/update-cart/{cart_id}")
    Call<Cart> updateUserCart(@Path("cart_id") long cart_id, @Body Cart cart);

    @Headers({"Key: key","Password: password"})
    @GET("/api/cart/cart-coupons/{cartId}")
    Call<ArrayList<Coupon>> getCouponsOfCartHavingID(@Path("cartId") long cartid);

}
