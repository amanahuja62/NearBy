package com.example.nearby.user.offer;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import com.example.nearby.R;
import com.example.nearby.model.Coupon;
import com.example.nearby.utils.Tools;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;



public class UserOfferDetails extends AppCompatActivity {

    private ImageView offerImage;
    TextView shopName;
    TextView offerLocation;
    TextView offerName;
    TextView offerPrice;
    TextView offerDescription;
    Button addToCart;
    Coupon coupon;
    private long duration = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        duration = getIntent().getLongExtra("EXTRA_DURATION", 400L);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            // Set up shared element transition
            findViewById(android.R.id.content).setTransitionName("EXTRA_VIEW");
            setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
            getWindow().setSharedElementEnterTransition(buildContainerTransform(true));
            getWindow().setSharedElementReturnTransition(buildContainerTransform(false));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_offer_details);
        coupon = (Coupon) getIntent().getSerializableExtra("couponDetails");


        offerImage = findViewById(R.id.offerImage);
        shopName = findViewById(R.id.shopName);
        offerLocation = findViewById(R.id.offerLocation);
        offerName = findViewById(R.id.offerName);
        offerDescription = findViewById(R.id.offerDescription);
        addToCart = findViewById(R.id.addToCart);
        offerPrice = findViewById(R.id.textView2);


        Tools.displayImageOriginal(this, offerImage, coupon.getImage());
        shopName.setText(coupon.getShopName());
        offerLocation.setText(coupon.getCity()+", "+coupon.getArea());
        offerName.setText(coupon.getName());
        offerDescription.setText(coupon.getDescription());
        offerPrice.setText(String.valueOf(coupon.getPrice()));


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // animation transition
            ViewCompat.setTransitionName(findViewById(R.id.parent_view), "EXTRA_VIEW");
        }

        initToolbar();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MaterialContainerTransform buildContainerTransform(boolean entering) {
        MaterialContainerTransform transform = new MaterialContainerTransform();
        transform.setTransitionDirection(entering ? MaterialContainerTransform.TRANSITION_DIRECTION_ENTER : MaterialContainerTransform.TRANSITION_DIRECTION_RETURN);
  /*      transform.setAllContainerColors(MaterialColors.getColor(findViewById(android.R.id.content), R.attr.colorSurface));*/
        transform.addTarget(android.R.id.content);
        transform.setDuration(duration);
        return transform;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
