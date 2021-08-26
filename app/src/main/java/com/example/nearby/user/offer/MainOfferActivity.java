package com.example.nearby.user.offer;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.nearby.CartAPI;
import com.example.nearby.CouponAPI;
import com.example.nearby.R;
import com.example.nearby.adapter.UserOfferAdapter;
import com.example.nearby.model.Cart;
import com.example.nearby.model.Coupon;
import com.example.nearby.model.User;
import com.example.nearby.user.mycart.MyCartActivity;
import com.example.nearby.utils.Tools;
import com.example.nearby.widget.SpacingItemDecoration;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainOfferActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserOfferAdapter mAdapter;
    CartAPI cartAPI;
    EditText selectCity;
    ListView areaListView;
    List<String> cityList = new ArrayList<>();
    List<String> areaList = new ArrayList<>();
    ArrayAdapter areaAdapter, cityAdapter;
    ListView cityListView;
    EditText selectArea;
    MaterialRippleLayout materialRippleLayout;
    private DrawerLayout drawer;
    SharedPreferences sp;
    Cart cart;
    ProgressDialog progressDialog;
    SharedPreferences.Editor editor;

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://nearby-backend.herokuapp.com").
            addConverterFactory(GsonConverterFactory.create())
            .build();
    CouponAPI couponAPI;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            // Set up shared element transition and disable overlay so views don't show above system bars
            setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
            getWindow().setSharedElementsUseOverlay(false);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_offer);
        sp = getSharedPreferences("users",MODE_PRIVATE);
        editor = sp.edit();
        user = (User)getIntent().getSerializableExtra("userDetails");
        Log.d("myMessage",""+user.getId());
        getUserCart();
        initToolbar();
        initComponent();



        findViewById(R.id.btn_close_filter).setOnClickListener(v -> drawer.closeDrawer(GravityCompat.END));
        findViewById(R.id.bt_cart).setOnClickListener(v -> {
            //open cart page
            Intent intent = new Intent(MainOfferActivity.this, MyCartActivity.class);
            intent.putExtra("userId",user.getId());
            startActivity(intent);
        });

        findViewById(R.id.bt_filter).setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));

        materialRippleLayout.setOnClickListener(v -> {
            getFilteredCoupons();
            if(!selectCity.getText().toString().equals(""))
                drawer.closeDrawer(GravityCompat.END);
            recyclerView.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(() -> recyclerView.setVisibility(View.VISIBLE), 500);


        });

        cityListView.setOnItemClickListener((parent, view, position, id) -> {
            selectCity.setText(cityList.get(position));
            cityList.clear();
            cityAdapter.clear();
            cityAdapter.notifyDataSetChanged();
        });
        areaListView.setOnItemClickListener((parent, view, position, id) -> {
            selectArea.setText(areaList.get(position));
            areaList.clear();
            areaAdapter.clear();
            areaAdapter.notifyDataSetChanged();
        });

        findViewById(R.id.clearAllfilters).setOnClickListener((l)-> clearAllfilters());
        selectCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")) {
                    cityAdapter.clear();
                    cityList.clear();
                    cityAdapter.notifyDataSetChanged();
                    return;
                }

                Call<List<Coupon>> call = couponAPI.getSelectedCoupons("/api/coupons/coupon/city/" + s.toString());
                getCityList(call);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        selectArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String citySelected = selectCity.getText().toString();
                String areaSelected = selectArea.getText().toString();
                if(citySelected.equals(""))
                    return;

                if(s.toString().equals("")) {
                    areaAdapter.clear();
                    areaList.clear();
                    areaAdapter.notifyDataSetChanged();
                    return;
                }

                Call<List<Coupon>> call = couponAPI.getSelectedCoupons("/api/coupons/coupon/area/"+citySelected+"/"+areaSelected);
                getAreaList(call);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getEachCoupon();
    }

    private void clearAllfilters() {
        selectArea.setText("");
        selectCity.setText("");
        cityList.clear();
        cityAdapter.clear();
        cityAdapter.notifyDataSetChanged();
        areaList.clear();
        areaAdapter.clear();
        areaAdapter.notifyDataSetChanged();
    }

    private void initToolbar() {
        Tools.setSystemBarColor(this, R.color.blue_500);
        Toast.makeText(this, "Hi " + user.getName(), Toast.LENGTH_SHORT).show();
    }

    private void initComponent() {
        drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.END);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 4), true));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new UserOfferAdapter(this, user.getId());
        recyclerView.setAdapter(mAdapter);
        progressDialog = new ProgressDialog(this);

        // on item list clicked
        mAdapter.setOnItemClickListener((view, obj, position) -> onItemGridClicked(view, obj));
        mAdapter.setOnLikeButtonClickListener((textView, obj, imageButton, progressBar) -> {
            int count = Integer.parseInt(textView.getText().toString());
            updateLikesOnServer(user.getId(),obj.getId(),textView,count,imageButton,progressBar);

        });
        selectCity = findViewById(R.id.selectCity);
        areaListView = findViewById(R.id.areaListView);
        cityListView = findViewById(R.id.cityListView);
        areaAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,areaList);
        cityAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,cityList);
        cityListView.setAdapter(cityAdapter);
        areaListView.setAdapter(areaAdapter);
        selectArea = findViewById(R.id.selectArea);

        materialRippleLayout =findViewById(R.id.btn_apply);
    }

    private void updateLikesOnServer(long userId, long couponId, TextView textView, int count, ImageButton imageButton, ProgressBar progressBar) {
        imageButton.setVisibility(View.GONE);
        imageButton.setClickable(false);
        progressBar.setVisibility(View.VISIBLE);
        CouponAPI couponAPI = retrofit.create(CouponAPI.class);
        Call<Coupon> call = couponAPI.updateLikes(couponId,userId);
        call.enqueue(new Callback<Coupon>() {
            @Override
            public void onResponse(Call<Coupon> call, Response<Coupon> response) {
                progressBar.setVisibility(View.GONE);
                imageButton.setVisibility(View.VISIBLE);
                imageButton.setClickable(true);
                if(response.code()==200){
                    Coupon coupon = response.body();
                    if(coupon.getLikedBy().contains(userId)) {
                        textView.setText("" + (count + 1));
                        imageButton.setColorFilter(Color.parseColor("#C62828"));
                    }
                    else {
                        textView.setText("" + (count - 1));
                        imageButton.setColorFilter(Color.parseColor("#EF9A9A"));
                    }


                }
            }

            @Override
            public void onFailure(Call<Coupon> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                imageButton.setVisibility(View.VISIBLE);
                imageButton.setClickable(true);
                Toast.makeText(MainOfferActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void onItemGridClicked(View sharedElement, Coupon obj) {
        synchronized (MainOfferActivity.this) {
            Intent intent = new Intent(this, UserOfferDetails.class);
            intent.putExtra("couponDetails", obj);
            intent.putExtra("userCart", cart);
            intent.putExtra("EXTRA_DURATION", 700L);
            intent.putExtra("prevActivity","MainOfferActivity");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, sharedElement, "EXTRA_VIEW");
                startActivity(intent, options.toBundle());

            } else {
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElement, "EXTRA_VIEW");
                // Now we can start the Activity, providing the activity options as a bundle
                ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
            }
        }

    }

    public void getEachCoupon(){
        progressDialog.setTitle("Fetching Results...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        couponAPI = retrofit.create(CouponAPI.class);

        Call<List<Coupon>> call = couponAPI.getAllCoupons();
        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                progressDialog.dismiss();
                if((!response.isSuccessful())|| response.code()!=200){

                    Toast.makeText(MainOfferActivity.this, "Code :" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Coupon> couponList2 = response.body();
                ArrayList<Coupon> indices = new ArrayList<>();
                for(Coupon coupon : couponList2) {
                    if (coupon.getCount() == 0)
                        indices.add(coupon);
                }
                couponList2.removeAll(indices);
                mAdapter.setData(couponList2);

                /* recyclerView.setAdapter(new CouponAdapter(AdminMainActivity.this, couponList, R.layout.item_news_horizontal));*/

            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
    public void getFilteredCoupons(){
        progressDialog.setTitle("Fetching Results...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String citySelected = selectCity.getText().toString();
        String areaSelected = selectArea.getText().toString();
        if(citySelected.equals("") && areaSelected.equals("")){
            getEachCoupon();
            return;
        }
        if(citySelected.equals("")){
            selectCity.setError("Please enter city !");
            progressDialog.dismiss();
            return;
        }
        if(areaSelected.equals("")){
            filterCouponsOnBasisOfCity();
            return;
        }
        filterCouponsOnBasisOfCityAndArea();

    }

    private void filterCouponsOnBasisOfCity() {
        progressDialog.setTitle("Fetching Results...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String citySelected = selectCity.getText().toString();
        couponAPI = retrofit.create(CouponAPI.class);

        Call<List<Coupon>> call = couponAPI.getSelectedCoupons("/api/coupons/coupon/city/"+citySelected);
        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                progressDialog.dismiss();
                if(response.code()!=200 || (!response.isSuccessful())){
                    Toast.makeText(MainOfferActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                List<Coupon> couponList2 = response.body();
                ArrayList<Coupon> indices = new ArrayList<>();
                for(Coupon coupon : couponList2) {
                    if (coupon.getCount() == 0)
                        indices.add(coupon);
                }
                couponList2.removeAll(indices);

                mAdapter.setData(couponList2);

            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }
    private void filterCouponsOnBasisOfCityAndArea() {

        String citySelected = selectCity.getText().toString();
        String areaSelected = selectArea.getText().toString();

        couponAPI = retrofit.create(CouponAPI.class);

        Call<List<Coupon>> call = couponAPI.getSelectedCoupons("/api/coupons/coupon/area/"+citySelected+"/"+areaSelected);
        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                progressDialog.dismiss();
                if(response.code()!=200 || (!response.isSuccessful())){
                    Toast.makeText(MainOfferActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                List<Coupon> couponList2 = response.body();
                ArrayList<Coupon> indices = new ArrayList<>();
                for(Coupon coupon : couponList2) {
                    if (coupon.getCount() == 0)
                        indices.add(coupon);
                }
                couponList2.removeAll(indices);
                mAdapter.setData(couponList2);

            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void getAreaList(Call<List<Coupon>> call) {
        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {

                if(response.code()!=200 || (!response.isSuccessful())){
                    Toast.makeText(MainOfferActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                List<Coupon> couponList2 = response.body();

                areaList.clear();
                areaAdapter.clear();
                areaAdapter.notifyDataSetChanged();
                if(couponList2 == null)
                    return;

                ArrayList<Coupon> indices = new ArrayList<>();
                for(Coupon coupon : couponList2) {
                    if (coupon.getCount() == 0)
                        indices.add(coupon);
                }
                couponList2.removeAll(indices);
                if(couponList2.size() == 0)
                    return;
                if(selectArea.getText().toString().equals(couponList2.get(0).getArea()))
                    return;
                for(Coupon coupon : couponList2){
                    if((areaList.size() == 0) || (!areaList.contains(coupon.getCity()))) {
                        areaList.add(coupon.getArea());
                        areaAdapter.notifyDataSetChanged();
                    }
                }
                Log.d("aman here", String.valueOf(cityList.size()));

            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void getCityList(Call<List<Coupon>> call) {

        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {

                if(response.code()!=200 || (!response.isSuccessful())){
                    Toast.makeText(MainOfferActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                List<Coupon> couponList2 = response.body();

                cityList.clear();
                cityAdapter.clear();
                cityAdapter.notifyDataSetChanged();
                if(couponList2 == null)
                    return;
                ArrayList<Coupon> indices = new ArrayList<>();
                for(Coupon coupon : couponList2) {
                    if (coupon.getCount() == 0)
                        indices.add(coupon);
                }
                couponList2.removeAll(indices);
                if(couponList2.size() == 0)
                    return;
                if(selectCity.getText().toString().equals(couponList2.get(0).getCity()))
                    return;
                for(Coupon coupon : couponList2){
                    if((cityList.size() == 0) || (!cityList.contains(coupon.getCity()))) {
                        cityList.add(coupon.getCity());
                        cityAdapter.notifyDataSetChanged();
                    }
                }
                Log.d("aman here", String.valueOf(cityList.size()));

            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    public void goToMyOffers(View view) {
        Intent intent = new Intent(MainOfferActivity.this,MyOffersActivity.class);
        intent.putExtra("userDetails",user);
        startActivity(intent);
    }

    public void signUserOut(View view) {
        editor.putBoolean("logged",false).apply();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_BACK :
                minimizeApp();
        }
        return super.onKeyDown(keyCode, event);
    }
    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void getUserCart(){
        synchronized (MainOfferActivity.this) {
            cartAPI = retrofit.create(CartAPI.class);
            Call<Cart> call = cartAPI.getUserCart("/api/cart/get-cart/" + user.getId());
            call.enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(Call<Cart> call, Response<Cart> response) {
                    if (response.code() != 200 || !response.isSuccessful()) {
                        Toast.makeText(MainOfferActivity.this, "Response : " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cart = response.body();

                }

                @Override
                public void onFailure(Call<Cart> call, Throwable t) {
                    Toast.makeText(MainOfferActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getUserCart();
    }

}
