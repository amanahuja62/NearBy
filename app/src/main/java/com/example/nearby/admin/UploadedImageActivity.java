package com.example.nearby.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.nearby.R;
import com.example.nearby.utils.Tools;

public class UploadedImageActivity extends AppCompatActivity {
    String url;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_image);
        imageView = findViewById(R.id.imageView);
        url = getIntent().getStringExtra("imageurl");
        Tools.displayImageOriginal(this,imageView,url);

    }
}