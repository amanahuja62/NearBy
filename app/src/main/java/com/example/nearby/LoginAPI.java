package com.example.nearby;

import com.example.nearby.model.LoginData;
import com.example.nearby.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginAPI {
    @POST("/api/users/login-user")
    Call<User> postUserData(@Body LoginData loginData);

    @POST("/api/users/login-admin")
    Call<LoginData> postAdminData(@Body LoginData loginData);
}
