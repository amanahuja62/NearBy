package com.example.nearby.user.offer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.nearby.CartAPI;
import com.example.nearby.R;
import com.example.nearby.model.Cart;
import com.example.nearby.model.Coupon;
import com.example.nearby.user.mycart.MyCartActivity;
import com.example.nearby.utils.Tools;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserOfferDetails extends AppCompatActivity {

    private ImageView offerImage;
    TextView shopName;
    TextView offerLocation;
    TextView offerName;
    ProgressBar progressBar;
    Cart cart;
    ImageButton backButton;
    TextView offerPrice;
    TextView offerDescription;
    Button addToCart;
    SharedPreferences sp;
    ArrayList<Long> couponIds = new ArrayList<>();
    Coupon coupon;
    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://nearby-backend.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    CartAPI cartAPI;
    private long duration = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getIntent().getStringExtra("prevActivity").equals("MainOfferActivity")) {
            duration = getIntent().getLongExtra("EXTRA_DURATION", 400L);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
                // Set up shared element transition
                findViewById(android.R.id.content).setTransitionName("EXTRA_VIEW");
                setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
                getWindow().setSharedElementEnterTransition(buildContainerTransform(true));
                getWindow().setSharedElementReturnTransition(buildContainerTransform(false));
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_offer_details);
        try {
            coupon = (Coupon) getIntent().getSerializableExtra("couponDetails");
            cart = (Cart) getIntent().getSerializableExtra("userCart");
            sp = getSharedPreferences("users",MODE_PRIVATE);
            ArrayList<Long> couponIds = cart.getCoupon_ids();
            Gson gson = new Gson();
            String json = gson.toJson(couponIds);
            sp.edit().putString("notPurchasedCoupons",json).apply();
        }
        catch (Exception e){

            Toast.makeText(UserOfferDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
        offerImage = findViewById(R.id.offerImage);
        shopName = findViewById(R.id.shopName);
        offerLocation = findViewById(R.id.offerLocation);
        offerName = findViewById(R.id.offerName);
        offerDescription = findViewById(R.id.offerDescription);
        addToCart = findViewById(R.id.addToCart);
        backButton = findViewById(R.id.bt_close);
        offerPrice = findViewById(R.id.textView2);
        progressBar = findViewById(R.id.progressBar2);

        Tools.displayImageOriginal(this, offerImage, coupon.getImage());
        shopName.setText(coupon.getShopName());
        offerLocation.setText(coupon.getArea()+", "+coupon.getCity());
        offerName.setText(coupon.getName());
        offerDescription.setText(coupon.getDescription());
        offerPrice.setText(String.valueOf(coupon.getPrice()));


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // animation transition
            ViewCompat.setTransitionName(findViewById(R.id.parent_view), "EXTRA_VIEW");
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MaterialContainerTransform buildContainerTransform(boolean entering) {
        MaterialContainerTransform transform = new MaterialContainerTransform();
        transform.setTransitionDirection(entering ? MaterialContainerTransform.TRANSITION_DIRECTION_ENTER : MaterialContainerTransform.TRANSITION_DIRECTION_RETURN);
        /*transform.setAllContainerColors(MaterialColors.getColor(findViewById(android.R.id.content), R.attr.colorSurface));*/
        transform.addTarget(android.R.id.content);
        transform.setDuration(duration);
        return transform;
    }




    public void addToCartClicked(View view) {

        if(couponIds.contains(coupon.getId())){
            Toast.makeText(UserOfferDetails.this, "Item already added to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        couponIds.addAll(cart.getCoupon_ids());
        if(couponIds.contains(coupon.getId())){
            Toast.makeText(UserOfferDetails.this, "Item already added to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        backButton.setEnabled(false);
        couponIds.add(coupon.getId());
        Cart cart2 = new Cart(cart.getId(), cart.getCreatedBy(),couponIds);
        cartAPI = retrofit.create(CartAPI.class);
        Call<Cart> call = cartAPI.updateUserCart(cart.getId(),cart2);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                progressBar.setVisibility(View.INVISIBLE);
                backButton.setEnabled(true);
                  if(!response.isSuccessful() || response.code()!= 200) {
                      Toast.makeText(UserOfferDetails.this, "Response code :" + response.code(), Toast.LENGTH_SHORT).show();
                      couponIds.remove(coupon.getId());
                      return;
                  }
                Gson gson = new Gson();
                String json = gson.toJson(couponIds);
                sp.edit().putString("notPurchasedCoupons",json).apply();


                Toast.makeText(UserOfferDetails.this, "Item added to cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                backButton.setEnabled(true);
                couponIds.remove(coupon.getId());
                Toast.makeText(UserOfferDetails.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cartButtonClicked(View view) {
        Intent intent = new Intent(UserOfferDetails.this, MyCartActivity.class);
        intent.putExtra("userId",cart.getCreatedBy());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void backButtonClicked(View view) {
        super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(sp.getString("purchased","no").equals("yes")){
            //user has purchased, cart is destroyed
            sp.edit().putString("purchased","no").apply();
            finish();
        }
        Gson gson = new Gson();
        sp.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {

        });
        String json = sp.getString("notPurchasedCoupons", "");
        Long[] remainingCoupons = gson.fromJson(json, Long[].class);
        ArrayList<Long> list = new ArrayList<>(Arrays.asList(remainingCoupons));
        couponIds.clear();
        couponIds.addAll(list);
        cart.setCoupon_ids(list);

    }
}
