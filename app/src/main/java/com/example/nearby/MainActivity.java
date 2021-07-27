package com.example.nearby;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
   TextView signup;
   EditText username, password;
   LoginAPI loginAPI;
    ProgressBar progressBar;
   Button signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.sign_up);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = username.getText().toString();
                String s2 = password.getText().toString();
                 if(s1.equals("") || s2.equals("")){
                     Toast.makeText(MainActivity.this, "Invalid username or password !!", Toast.LENGTH_SHORT).show();
                     return;
                 }
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://nearby-backend.herokuapp.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                loginAPI = retrofit.create(LoginAPI.class);
                username.setEnabled(false);
                password.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                validateCredentials(s1,s2);

            }
        });
    }

    private void validateCredentials(String user, String userPass){
        LoginData loginData = new LoginData(user, userPass);
         progressBar.setVisibility(View.VISIBLE);



        //first trying to post the credentials as user data in login-user api

        Call<LoginData> call = loginAPI.postAdminData(loginData);
        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                progressBar.setVisibility(View.GONE);
                LoginData loginData1 = response.body();
                if(loginData != null) {
                    Toast.makeText(MainActivity.this, "Welcome Admin !! " + loginData1.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,AdminPanel.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    username.setEnabled(true);
                    password.setEnabled(true);
                }

            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                Call<LoginData> call2 = loginAPI.postUserData(loginData);
                call2.enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                        LoginData loginData1 = response.body();
                        progressBar.setVisibility(View.GONE);

                        if(loginData1 != null)
                        Toast.makeText(MainActivity.this, "Login Successful !!" + loginData1.getUsername(), Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            username.setEnabled(true);
                            password.setEnabled(true);
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginData> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Invalid Credentials !!", Toast.LENGTH_SHORT).show();
                        username.setEnabled(true);
                        password.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}