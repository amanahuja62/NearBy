package com.example.nearby;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginAPI {
    @POST("/api/users/login-user")
    Call<LoginData> postUserData(@Body LoginData loginData);

    @POST("/api/users/login-admin")
    Call<LoginData> postAdminData(@Body LoginData loginData);
}
