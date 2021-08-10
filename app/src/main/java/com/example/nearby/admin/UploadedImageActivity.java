package com.example.nearby.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.nearby.R;

public class UploadedImageActivity extends AppCompatActivity {
    Uri imageUri;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_image);
        imageView = findViewById(R.id.imageView);
        imageUri = getIntent().getParcelableExtra("imageuri");
        imageView.setImageURI(imageUri);

    }
}