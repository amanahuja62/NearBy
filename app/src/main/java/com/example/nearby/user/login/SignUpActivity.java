package com.example.nearby.user.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nearby.R;
import com.example.nearby.RegisterUserAPI;
import com.example.nearby.model.User;
import com.example.nearby.user.offer.MainOfferActivity;
import com.example.nearby.utils.Tools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    EditText e1,e2,e3,e4,e6;
    Boolean  var2, var3;
    ProgressBar loadingPB;

    private RegisterUserAPI registerUserAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.pink_400);
    }

    private void initComponent() {
        //initialisations for editText
        e1 = findViewById(R.id.name);
        e2 = findViewById(R.id.pass);
        e3 = findViewById(R.id.confirmPass);
        e4 = findViewById(R.id.email);
        e6 = findViewById(R.id.username);
        loadingPB = findViewById(R.id.progressBar);

    }



    private Boolean phoneValidation(String phoneNo) {

        if(phoneNo.length() != 10)
            return false;
        if(phoneNo.charAt(0) == '0')
            return false;

        return true;

    }

    private Boolean passwordValidation(String password){

        if(password.length() < 5)
            return false;
        return true;
    }

    private Boolean matchingValidation(String password, String confirmPassword){

        if(password.equals(confirmPassword))
            return true;
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {

            var2 = passwordValidation(e2.getText().toString());
            var3 = matchingValidation(e2.getText().toString(), e3.getText().toString());

            if( var2 && var3){
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://nearby-backend.herokuapp.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                registerUserAPI = retrofit.create(RegisterUserAPI.class);
                createUser();


            }else{

                 if(!var2){
                    e2.setError("Password must have more than 4 characters");
                }
                else{
                    Toast.makeText(this, "Passwords donot match", Toast.LENGTH_SHORT).show();
                }
            }


        }
        return super.onOptionsItemSelected(item);
    }

    private void makeEditTextNonEditable(){
        e1.setEnabled(false);
        e2.setEnabled(false);
        e3.setEnabled(false);
        e4.setEnabled(false);
        e6.setEnabled(false);

    }
    private void makeEditTextEditable(){
        e1.setEnabled(true);
        e2.setEnabled(true);
        e3.setEnabled(true);
        e4.setEnabled(true);
        e6.setEnabled(true);
    }

    private void createUser() {
        String username, email, password, name , address, phone;
        phone = null;
        address = null;
        username = e6.getText().toString();
        email = e4.getText().toString();
        name = e1.getText().toString();
        password = e2.getText().toString();
        makeEditTextNonEditable();

        loadingPB.setVisibility(View.VISIBLE);
        User user = new User(username, name, email, password, phone, address);
        Call<User> call = registerUserAPI.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if(response.code()!=200 || (!response.isSuccessful())){
                    Toast.makeText(SignUpActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

              /* Toast.makeText(SignUpActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();*/
                loadingPB.setVisibility(View.GONE);
                User userPostResponse = response.body();
                Intent intent = new Intent(SignUpActivity.this, MainOfferActivity.class);
                intent.putExtra("userDetails",userPostResponse);
                startActivity(intent);
                finish();



            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loadingPB.setVisibility(View.GONE);
                makeEditTextEditable();
                Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}