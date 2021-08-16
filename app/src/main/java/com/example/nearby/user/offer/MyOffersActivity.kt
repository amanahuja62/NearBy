package com.example.nearby.user.offer

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearby.CouponAPI
import com.example.nearby.MyOffersApi
import com.example.nearby.R
import com.example.nearby.adapter.MyOfferAdapter
import com.example.nearby.model.Coupon
import com.example.nearby.model.IndivOfferPurchased
import com.example.nearby.model.MyOffers
import com.example.nearby.model.User
import com.example.nearby.user.mycart.MyCartActivity
import com.example.nearby.utils.Tools
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

@Suppress("DEPRECATION")
class MyOffersActivity : AppCompatActivity() {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://nearby-backend.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    lateinit var indivOfferList : ArrayList<IndivOfferPurchased>
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var user: User
    lateinit var progressDialog: ProgressDialog
    var myOfferAdapter = MyOfferAdapter(this)
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_myoffers)

        user  = intent.getSerializableExtra("userDetails") as User
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = myOfferAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        initToolbar()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Fetching results...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val myOffersApi = retrofit.create(MyOffersApi::class.java)
        val call = myOffersApi.getPurchasedOffers("api/myOffers/myOffers-purchased/${user.id.toString()}")
        call.enqueue(object : Callback<List<MyOffers>>{
            override fun onResponse(call: Call<List<MyOffers>>, response: Response<List<MyOffers>>) {
                 if(response.code()==200){
                     indivOfferList = ArrayList<IndivOfferPurchased>()
                     val myOffersList = response.body()!!
                     for(myOffer in myOffersList){
                         for(id in myOffer.coupons)
                             addOfferAvailedDetailsToList(id,myOffer.address,myOffer.transactionDate)
                     }
                       progressDialog.dismiss()

                     Log.d("abcdef","${indivOfferList.size.toString()}")

                 }
            }

            override fun onFailure(call: Call<List<MyOffers>>, t: Throwable) {
                 Toast.makeText(applicationContext,t.message,Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }

        })
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.navigationIcon!!
            .setColorFilter(resources.getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Your Orders"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this, R.color.grey_10)
        Tools.setSystemBarLight(this)
    }

    private fun addOfferAvailedDetailsToList(id: Long,address : String?, transactionDate : String?){
        val couponAPI = retrofit.create(CouponAPI::class.java)
        val call = couponAPI.getCouponHavingID(id)
        lateinit var coupon : Coupon

            call.enqueue(object : Callback<Coupon> {
                override fun onResponse(call: Call<Coupon>, response: Response<Coupon>) {
                    coupon = response.body()!!
                    indivOfferList.add(IndivOfferPurchased(coupon,address,transactionDate))
                    Log.d("abcdeff","${indivOfferList.size.toString()}")
                    myOfferAdapter.setData(indivOfferList)
                }

                override fun onFailure(call: Call<Coupon>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }

            })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite_setting, menu)
        Tools.changeMenuIconColor(menu, resources.getColor(R.color.grey_10))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            val intent = Intent(this@MyOffersActivity, MyCartActivity::class.java)
            intent.putExtra("userId", (user.id))
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }



}