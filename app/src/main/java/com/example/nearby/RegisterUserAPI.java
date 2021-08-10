package com.example.nearby;

import com.example.nearby.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterUserAPI {

    @POST("/api/users/register-user")
    Call<User> createUser(@Body User user);
}
