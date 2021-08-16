package com.example.nearby.admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.nearby.CouponAPI;
import com.example.nearby.R;
import com.example.nearby.adapter.CouponAdapter;
import com.example.nearby.model.Coupon;
import com.example.nearby.model.DeleteResponse;
import com.example.nearby.user.login.MainActivity;
import com.example.nearby.utils.Tools;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminMainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    EditText selectCity;
    ListView areaListView;
    List<String> cityList = new ArrayList<>();
    List<String> areaList = new ArrayList<>();
    ArrayAdapter areaAdapter, cityAdapter;
    ListView cityListView;
    EditText selectArea;
    MaterialRippleLayout materialRippleLayout;
    private RecyclerView recyclerView;
    int updationIndex;
    private CouponAdapter mAdapter;
    final static int CREATE_COUPON_REQ = 120;
    final static int UPDATE_COUPON_REQ = 121;
    private DrawerLayout drawer;

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://nearby-backend.herokuapp.com").
            addConverterFactory(GsonConverterFactory.create())
            .build();
    CouponAPI couponAPI;
    String[] optionsForAdmin = {"Update the coupon", "Delete the coupon"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_drawer_filter);


        findViewById(R.id.btn_close_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.END);
            }
        });

        findViewById(R.id.bt_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });


        initToolbar();
        initComponent();

        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCity.setText(cityList.get(position));
                cityList.clear();
                cityAdapter.clear();
                cityAdapter.notifyDataSetChanged();
            }
        });
        areaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectArea.setText(areaList.get(position));
                areaList.clear();
                areaAdapter.clear();
                areaAdapter.notifyDataSetChanged();
            }
        });
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

        //set data and list adapter
        mAdapter = new CouponAdapter(this, R.layout.item_news_horizontal);
        recyclerView.setAdapter(mAdapter);

        getEachCoupon();
        // on item list clicked
        mAdapter.setOnItemClickListener(new CouponAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Coupon obj, int position) {

                final AlertDialog.Builder dialog = new AlertDialog.Builder(AdminMainActivity.this);
                dialog.setCancelable(true);
                dialog.setTitle("Choose an option");
                dialog.setSingleChoiceItems(optionsForAdmin, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch(which){
                            case 0 : //update the coupon // put request
                                Intent intent = new Intent(AdminMainActivity.this,CouponRegisterActivity.class);
                                intent.putExtra("mode","update");
                                intent.putExtra("Coupon",obj);
                                updationIndex = mAdapter.getItemPosition(obj);
                                startActivityForResult(intent, UPDATE_COUPON_REQ);
                                break;
                            case 1 : //delete the coupon
                                AlertDialog.Builder dialog2 = new AlertDialog.Builder(AdminMainActivity.this);
                                dialog2.setTitle("This will Delete the coupon");
                                dialog2.setCancelable(true);
                                dialog2.setNegativeButton("Cancel",null);
                                dialog2.setPositiveButton("Yes continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       deleteCoupon(obj.getId(),obj);
                                    }
                                });
                                dialog2.show();

                                //delete request
                                break;
                            default: break;
                        }
                    }
                });
                dialog.show();
            }
        });

        materialRippleLayout =findViewById(R.id.btn_apply);
        materialRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFilteredCoupons();
                if(!selectCity.getText().toString().equals(""))
                drawer.closeDrawer(GravityCompat.END);
                recyclerView.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }, 500);


            }
        });


    }

    private void deleteCoupon(long id,Coupon coupon) {
        Call<DeleteResponse> call = couponAPI.deleteCoupon(id);
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {

                if(response.code() != 200 || (!response.isSuccessful())){
                    Toast.makeText(AdminMainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }


                DeleteResponse deleteResponse = response.body();
                if(deleteResponse.getDeleted()==true) {
                    if(coupon.getImage() != null)
                        deletePreviouslyUploadedImage(coupon.getImage());
                    Toast.makeText(AdminMainActivity.this, "Deletion Successful", Toast.LENGTH_SHORT).show();
                    mAdapter.deleteAndRefresh(coupon);
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(AdminMainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deletePreviouslyUploadedImage(String imageURL) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReferenceFromUrl(imageURL);
        ref.delete();
    }

    private void getAreaList(Call<List<Coupon>> call) {
        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                if(response.code() != 200 || (!response.isSuccessful())){
                    Toast.makeText(AdminMainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Coupon> couponList2 = response.body();

                areaList.clear();
                areaAdapter.clear();
                areaAdapter.notifyDataSetChanged();
                if(couponList2 == null)
                    return;
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
                if(response.code() != 200 || (!response.isSuccessful())){
                    Toast.makeText(AdminMainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Coupon> couponList2 = response.body();

                cityList.clear();
                cityAdapter.clear();
                cityAdapter.notifyDataSetChanged();
                if(couponList2 == null)
                    return;
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

    private void initToolbar() {

        Tools.setSystemBarColor(this, R.color.red_600);
    }

    private void initComponent() {
        drawer = findViewById(R.id.drawer_layout);

        // open drawer at start
        drawer.openDrawer(GravityCompat.END);

        recyclerView = findViewById(R.id.recyclerView);
        progressDialog = new ProgressDialog(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        selectCity = findViewById(R.id.selectCity);
        areaListView = findViewById(R.id.areaListView);
        cityListView = findViewById(R.id.cityListView);
        areaAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,areaList);
        cityAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,cityList);
        cityListView.setAdapter(cityAdapter);
        areaListView.setAdapter(areaAdapter);
        selectArea = findViewById(R.id.selectArea);

    }

    public void getEachCoupon(){
        progressDialog.setTitle("Fetching Results...");
        progressDialog.show();

         couponAPI = retrofit.create(CouponAPI.class);

        Call<List<Coupon>> call = couponAPI.getAllCoupons();
        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                progressDialog.dismiss();
                if(response.code() != 200 || (!response.isSuccessful())){
                    Toast.makeText(AdminMainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }


              List<Coupon> couponList2 = response.body();
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

    private void filterCouponsOnBasisOfCityAndArea() {

        String citySelected = selectCity.getText().toString();
        String areaSelected = selectArea.getText().toString();



        couponAPI = retrofit.create(CouponAPI.class);

        Call<List<Coupon>> call = couponAPI.getSelectedCoupons("/api/coupons/coupon/area/"+citySelected+"/"+areaSelected);
        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                progressDialog.dismiss();
                if(response.code() != 200 || (!response.isSuccessful())){
                    Toast.makeText(AdminMainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Coupon> couponList2 = response.body();
                mAdapter.setData(couponList2);

            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void filterCouponsOnBasisOfCity() {
        progressDialog.setTitle("Fetching Results...");
        progressDialog.show();
        String citySelected = selectCity.getText().toString();
        couponAPI = retrofit.create(CouponAPI.class);

        Call<List<Coupon>> call = couponAPI.getSelectedCoupons("/api/coupons/coupon/city/"+citySelected);
        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                progressDialog.dismiss();
                if(response.code() != 200 || (!response.isSuccessful())){
                    Toast.makeText(AdminMainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Coupon> couponList2 = response.body();
                mAdapter.setData(couponList2);

            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    public void createNewCoupon(View view) {
        Intent intent = new Intent(AdminMainActivity.this, CouponRegisterActivity.class);
        intent.putExtra("mode","create");
        startActivityForResult(intent, CREATE_COUPON_REQ);
  }

    public void signAdminOut(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CREATE_COUPON_REQ )
            getEachCoupon();

        if(requestCode == UPDATE_COUPON_REQ){
            couponAPI = retrofit.create(CouponAPI.class);
            Call<Coupon> call = couponAPI.getCouponHavingID(mAdapter.getCouponIdPresentAtIndex(updationIndex));
            call.enqueue(new Callback<Coupon>() {
                @Override
                public void onResponse(Call<Coupon> call, Response<Coupon> response) {
                    if(!response.isSuccessful())
                        return;
                    if(response.code()!=200)
                        return;
                    Coupon coupon = response.body();
                    mAdapter.updateAndRefresh(updationIndex,coupon);
                }

                @Override
                public void onFailure(Call<Coupon> call, Throwable t) {

                }
            });
        }

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

}