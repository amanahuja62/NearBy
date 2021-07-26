package com.example.nearby;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterUserAPI {

    @POST("/api/users/register-user")
    Call<User> createUser(@Body User user);
}
