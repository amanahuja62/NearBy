package com.example.nearby;

import com.example.nearby.model.LoginData;
import com.example.nearby.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LoginAPI {
    @POST("/api/users/login-user")
    Call<User> postUserData(@Body LoginData loginData);

    @POST("/api/users/login-admin")
    Call<LoginData> postAdminData(@Body LoginData loginData);

    @PUT("api/users/update-contact/{id}")
    Call<User> updateUserContacts(@Path("id") long id, @Body User user);
}
