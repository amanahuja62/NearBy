package com.example.nearby.admin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.nearby.CouponAPI;
import com.example.nearby.R;
import com.example.nearby.model.Coupon;
import com.example.nearby.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CouponRegisterActivity extends AppCompatActivity {

    final static int READ_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    final static int PICK_IMAGE_GALLERY = 200;
    EditText e1,e2,e3,e4,e5,e6,e7,e8;
    TextView textView;
    CouponAPI couponAPI;
    ProgressDialog progressDialog;
    String imageURL;
    String imageName;
    String mode;
    LinearLayout linearLayout;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImageView imageView;
    Uri filepath;
    String[] optionsForImages = {"Upload image from Gallery","Show uploaded image", "Delete uploaded image"};
    Coupon couponForUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        Tools.setSystemBarColor(this, R.color.blue_900);
        mode = getIntent().getStringExtra("mode");
        couponForUpdate = (Coupon) getIntent().getSerializableExtra("Coupon");
         initComponent();
         if(mode.equals("update"))
             initialiseTextFields();

        (findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagesChoiceDialog();
            }
        });
    }

    private void showImagesChoiceDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        dialog.setSingleChoiceItems(optionsForImages, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0: checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_REQUEST_CODE);
                        if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_REQUEST_CODE)) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_GALLERY);
                        }
                        break;


                    case 1:  if(imageURL == null)
                             Toast.makeText(CouponRegisterActivity.this, "You have not uploaded any image", Toast.LENGTH_SHORT).show();
                             else{
                               Intent intent = new Intent(CouponRegisterActivity.this, UploadedImageActivity.class);
                               intent.putExtra("imageurl",imageURL);
                               startActivity(intent);

                              }
                             break;

                    case 2: if(imageURL == null)
                              Toast.makeText(CouponRegisterActivity.this, "You have not uploaded any image", Toast.LENGTH_SHORT).show();
                            else
                                deletePreviouslyUploadedImage();

                            break;

                    default: break;


                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data!=null && data.getData()!=null) {

            if(imageURL != null)
                deletePreviouslyUploadedImage();


            filepath = data.getData();
            uploadImage();

                if (filepath.toString().startsWith("file:")) {
                    imageName = filepath.getPath();
                } else { // uri.startsWith("content:")

                    Cursor c = getContentResolver().query(filepath, null, null, null, null);

                    if (c != null && c.moveToFirst()) {

                        int id = c.getColumnIndex(MediaStore.Images.Media.DATA);
                        if (id != -1) {
                            imageName = c.getString(id);
                        }
                    }
                }

        }
    }

    private void deletePreviouslyUploadedImage() {
        StorageReference ref = storage.getReferenceFromUrl(imageURL);
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                imageURL = null;
                filepath = null;
                textView.setText("Add Coupon Image");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(CouponRegisterActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void uploadImage()
    {
        if (filepath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            // adding listeners on upload
            // or failure of image
           ref.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                   ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                            imageURL = uri.toString();

                            progressDialog.dismiss();
                           Toast.makeText(CouponRegisterActivity.this, "Image uploaded successfully !", Toast.LENGTH_SHORT).show();
                           textView.setText(imageName);
                       }
                   });

               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(CouponRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                   progressDialog.dismiss();
               }
           });

        }
    }


    public void adminSubmit(View view) {
       String area, city, couponCode, offerDescription, shopName, offerName;
       int price, count;
       area = e1.getText().toString();
       city = e2.getText().toString();
       count = Integer.parseInt(e3.getText().toString());
       couponCode = e4.getText().toString();
       offerDescription = e5.getText().toString();
       shopName = e6.getText().toString();
       offerName = e7.getText().toString();
       price = Integer.parseInt(e8.getText().toString());

       Coupon coupon = new Coupon(offerName,offerDescription,count,imageURL,shopName,city,price,area,couponCode);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nearby-backend.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        couponAPI = retrofit.create(CouponAPI.class);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        if(mode.equals("create"))
            createMode(coupon);
        else
            updateMode(coupon);


    }

    private void updateMode(Coupon coupon) {
        Call<Coupon> call = couponAPI.putCoupon(couponForUpdate.getId(),coupon);
        call.enqueue(new Callback<Coupon>() {
            @Override
            public void onResponse(Call<Coupon> call, Response<Coupon> response) {
                progressDialog.dismiss();
                if(response.code() != 200 || (!response.isSuccessful())){
                    Toast.makeText(CouponRegisterActivity.this, "Code :" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                Coupon couponPutResponse = response.body();
                String s = "Response Code : " + response.code() + "\nName : " + couponPutResponse.getCity() + "\n" + "Email : " + couponPutResponse.getArea();
                Toast.makeText(CouponRegisterActivity.this, s, Toast.LENGTH_SHORT).show();
                CouponRegisterActivity.this.finish();

            }

            @Override
            public void onFailure(Call<Coupon> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CouponRegisterActivity.this, t.getStackTrace().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void initComponent(){
        e1 = findViewById(R.id.area);
        e2 = findViewById(R.id.city);
        e3 = findViewById(R.id.count);
        e4 = findViewById(R.id.couponCode);
        e5 = findViewById(R.id.offerDescription);
        e6 = findViewById(R.id.shopName);
        e7 = findViewById(R.id.offerName);
        e8 = findViewById(R.id.offerPrice);

        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.textview);
        linearLayout = findViewById(R.id.linearLayout);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressDialog = new ProgressDialog(this);

    }

    private void initialiseTextFields() {
        e1.setText(couponForUpdate.getArea());
        e2.setText(couponForUpdate.getCity());
        e3.setText(String.valueOf(couponForUpdate.getCount()));
        e4.setText(couponForUpdate.getCode());
        e5.setText(couponForUpdate.getDescription());
        e6.setText(couponForUpdate.getShopName());
        e7.setText(couponForUpdate.getName());
        e8.setText(String.valueOf(couponForUpdate.getPrice()));
        imageURL = couponForUpdate.getImage();
    }
    public void createMode(Coupon coupon){
        Call<Coupon> call = couponAPI.postCoupon(coupon);
        call.enqueue(new Callback<Coupon>() {
            @Override
            public void onResponse(Call<Coupon> call, Response<Coupon> response) {
                progressDialog.dismiss();
                if(response.code() != 200 || (!response.isSuccessful())){
                    Toast.makeText(CouponRegisterActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }


                Coupon couponPostResponse = response.body();

                String s = "Response Code : " + response.code() + "\nName : " + couponPostResponse.getCity() + "\n" + "Email : " + couponPostResponse.getArea();
                Toast.makeText(CouponRegisterActivity.this, s, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Coupon> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CouponRegisterActivity.this, t.getStackTrace().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkPermission (String permission, int requestCode){
           if(ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED){
               ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);
           }
    }

    private boolean isPermissionGranted(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_DENIED)
            return true;
        return false;
    }
}