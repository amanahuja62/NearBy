package com.example.nearby.user.mycart

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balysv.materialripple.MaterialRippleLayout
import com.example.nearby.LoginAPI
import com.example.nearby.MyOffersApi

import com.example.nearby.R
import com.example.nearby.adapter.MyCartOfferAdapter
import com.example.nearby.model.Coupon
import com.example.nearby.model.PostPurchase
import com.example.nearby.model.PostPurchaseResponse
import com.example.nearby.model.User
import com.example.nearby.user.offer.MyOffersActivity
import com.example.nearby.utils.Tools
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class CheckoutActivity : AppCompatActivity() {
    val retrofit = Retrofit.Builder()
            .baseUrl("https://nearby-backend.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    lateinit var recyclerView: RecyclerView
    lateinit var phone : TextView
    lateinit var materialRippleLayout: MaterialRippleLayout
    lateinit var toolbar : androidx.appcompat.widget.Toolbar
    lateinit var address: TextView
    lateinit var totalPrice : TextView
    lateinit var sp: SharedPreferences
    lateinit var progressDialog : ProgressDialog
    lateinit var obj : User
    var cartId : Long? = null

    val mAdapter = MyCartOfferAdapter(this,R.layout.indiv_checkout_offer)
    lateinit var couponsList : ArrayList<Coupon>
    lateinit var couponIdsList : ArrayList<Long>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        initComponent()
        initToolbar()
        mAdapter.setData(couponsList)

    }

    private fun initComponent() {
        recyclerView = findViewById(R.id.recyclerView)
        phone = findViewById(R.id.phone)
        address = findViewById(R.id.address)
        materialRippleLayout = findViewById(R.id.submit)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        try {
            sp = getSharedPreferences("users", MODE_PRIVATE)
            val gson = Gson()
            val json = sp.getString("userDetails", "")
            Thread.sleep(50)
            obj = gson.fromJson(json, User::class.java)
        }
        catch (e : Exception){
            finish()
        }
        totalPrice = findViewById(R.id.totalprice)
        couponsList = intent.getSerializableExtra("couponsList") as ArrayList<Coupon>
        totalPrice.text = intent.getLongExtra("totalPrice",100).toString()
        cartId = intent.getLongExtra("cartId",1)
        couponIdsList = ArrayList<Long>()
        for(coupon in couponsList){
            couponIdsList.add(coupon.id)
        }

        materialRippleLayout.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                submitClicked(v)
            }

        })
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setTitleTextColor(Color.BLACK)
        toolbar.navigationIcon!!
            .setColorFilter(resources.getColor(R.color.grey_90), PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Checkout"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this, R.color.grey_10)
        Tools.setSystemBarLight(this)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    fun submitClicked(view: View?) {
        if(phone.text.length !=10) {
            phone.error = "Phone no must have 10 digits"
            return
        }
        if(address.text.toString() == ""){
            address.error = "Invalid address"
            return
        }
        obj.address = address.text.toString()
        obj.phone = phone.text.toString()

        val gson = Gson()
        val sp : SharedPreferences = getSharedPreferences("users", MODE_PRIVATE)
        val json =  sp.getString("notPurchasedCoupons","")
        val notPurchasedCoupons = gson.fromJson(json, Array<Long>::class.java)
        Log.d("abcdefghi",notPurchasedCoupons[0].toString())
        val list = ArrayList(Arrays.asList<Long>(*notPurchasedCoupons))



        val loginAPI = retrofit.create(LoginAPI::class.java)
        val call = loginAPI.updateUserContacts(obj.id,obj)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Placing order...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        call.enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {

                if(response.code() == 200)
                    //user contacts updated
                    purchaseItemsInCart(list)

            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                progressDialog.dismiss()
              Toast.makeText(this@CheckoutActivity,t.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun purchaseItemsInCart(notPurchasedCoupons: ArrayList<Long>) {
        val postPurchase = PostPurchase(cartId,obj.id,obj.email)
        val myOffersApi= retrofit.create(MyOffersApi::class.java)
        val call = myOffersApi.purchaseItemsInCart(obj.phone,obj.address,postPurchase)
        call.enqueue(object : Callback<PostPurchaseResponse>{
            override fun onResponse(call: Call<PostPurchaseResponse>,response: Response<PostPurchaseResponse>) {
              if(response.code()==200){
                  progressDialog.dismiss()
                  notPurchasedCoupons.removeAll(couponIdsList)

                  val gson = Gson()
                  val json = gson.toJson(notPurchasedCoupons)
                  sp.edit().putString("notPurchasedCoupons", json).apply()
                  sp.edit().putString("purchased","yes").apply()
                  Toast.makeText(this@CheckoutActivity,"Your Order has been placed",Toast.LENGTH_LONG).show()
                  val intent = Intent(this@CheckoutActivity,MyOffersActivity::class.java)
                  intent.putExtra("userDetails",obj)
                  startActivity(intent)
                  finish()
              }
            }

            override fun onFailure(call: Call<PostPurchaseResponse>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@CheckoutActivity,t.message,Toast.LENGTH_SHORT).show()
            }

        })
    }


}