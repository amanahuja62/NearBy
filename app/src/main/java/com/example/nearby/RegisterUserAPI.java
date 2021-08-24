package com.example.nearby;

import com.example.nearby.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RegisterUserAPI {

    @Headers({"Key: key","Password: password"})
    @POST("/api/users/register-user")
    Call<User> createUser(@Body User user);
}
