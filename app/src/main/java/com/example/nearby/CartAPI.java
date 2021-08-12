package com.example.nearby;

import com.example.nearby.model.Cart;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface CartAPI {
    @GET
    Call<Cart> getUserCart(@Url String url);

    @PUT("/api/cart/update-cart/{cart_id}")
    Call<Cart> updateUserCart(@Path("cart_id") long cart_id, @Body Cart cart);

}
