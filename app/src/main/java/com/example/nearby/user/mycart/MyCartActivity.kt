package com.example.nearby.user.mycart

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balysv.materialripple.MaterialRippleLayout
import com.example.nearby.CartAPI
import com.example.nearby.CouponAPI
import com.example.nearby.R
import com.example.nearby.adapter.MyCartOfferAdapter
import com.example.nearby.model.Cart
import com.example.nearby.model.Coupon
import com.example.nearby.user.offer.UserOfferDetails
import com.example.nearby.utils.Tools
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyCartActivity : AppCompatActivity() {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://nearby-backend.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var optionsForUser = arrayOf("Go to offers page", "Remove item from cart")
    var totalAmount : Long =0
    val arrayList = ArrayList<Coupon>()
    var loadedNum : Int =0
    lateinit var progressDialog : ProgressDialog

    var myCartOfferAdapter = MyCartOfferAdapter(this,R.layout.indiv_cart_offer)
    lateinit var recyclerView: RecyclerView
    lateinit var totalPrice : TextView
    lateinit var materialRippleLayout: MaterialRippleLayout
    lateinit var progressBar : ProgressBar
     lateinit var cart : Cart
     var userId : Long = 0
    lateinit var toolbar : androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)
        initToolbar()
        initComponent()
        userId = intent.getLongExtra("userId",1)
        getCouponIDsFromApi()

    }

    private fun getCouponIDsFromApi() {
        arrayList.clear()
        totalPrice.text = "0"
        myCartOfferAdapter.setData(arrayList)
        val cartAPI = retrofit.create(CartAPI::class.java)
        val call = cartAPI.getUserCart("/api/cart/get-cart/${userId.toString()}")
        progressBar.visibility = View.VISIBLE
        call.enqueue(object : Callback<Cart>{
            override fun onResponse(call: Call<Cart>, response: Response<Cart>) {
                if(response.code()==200){
                    totalAmount = 0
                    loadedNum = 0
                    var index : Int = 0
                    cart  = response.body()!!
                    if(cart.coupon_ids.size == 0)
                    {
                        Toast.makeText(this@MyCartActivity,"Your cart is empty",Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.GONE
                    }
                    for(couponId in cart.coupon_ids){
                        addCouponToList(couponId,index)
                        index++
                    }


                }
            }

            override fun onFailure(call: Call<Cart>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@MyCartActivity,t.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addCouponToList(couponId: Long, index: Int) {
           val couponAPI = retrofit.create(CouponAPI::class.java)
           val call = couponAPI.getCouponHavingID(couponId)
           call.enqueue(object :Callback<Coupon>{
            override fun onResponse(call: Call<Coupon>, response: Response<Coupon>) {
                if(response.code()==200){
                    val coupon = response.body()!!
                    arrayList.add(coupon)
                    myCartOfferAdapter.setData(arrayList)
                    totalAmount = totalAmount + coupon.price
                    loadedNum++
                    totalPrice.text = totalAmount.toString()

                }
                if(index == 0)
                    progressBar.visibility = View.GONE

            }

            override fun onFailure(call: Call<Coupon>, t: Throwable) {
                Toast.makeText(this@MyCartActivity,t.message,Toast.LENGTH_SHORT).show()
                if(index == 0)
                    progressBar.visibility = View.GONE
            }

        })
    }
    private fun initComponent() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = myCartOfferAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        totalPrice = findViewById(R.id.price)
        progressBar = findViewById(R.id.progressBar3)
        materialRippleLayout = findViewById(R.id.checkoutButton)

        materialRippleLayout.setOnClickListener {
            if (!(this::cart.isInitialized) || loadedNum != cart.coupon_ids.size ){
                Toast.makeText(this,"Please wait...",Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this@MyCartActivity,CheckoutActivity::class.java)
                intent.putExtra("couponsList",arrayList)
                intent.putExtra("totalPrice",totalAmount)
                intent.putExtra("cartId",cart.id)
                startActivity(intent)
            }
        }

        myCartOfferAdapter.setOnItemClickListener(object : MyCartOfferAdapter.OnItemClickListener {
            override fun onItemClick(view: View, obj: Coupon, position: Int) {
                val dialog = AlertDialog.Builder(this@MyCartActivity)
                dialog.setCancelable(true)
                dialog.setTitle("Choose an option")

                dialog.setSingleChoiceItems(optionsForUser,0,  DialogInterface.OnClickListener{ dialog,which ->
                        dialog.dismiss()

                        when(which){
                            0 -> {val intent = Intent(this@MyCartActivity,UserOfferDetails::class.java)
                                  intent.putExtra("prevActivity","MyCartActivity")
                                  intent.putExtra("userCart",cart)
                                  intent.putExtra("couponDetails",obj)
                                  startActivity(intent)
                            }
                            1 -> {
                                progressDialog = ProgressDialog(this@MyCartActivity)
                                progressDialog.setTitle("Deleting item from cart...")
                                progressDialog.setCancelable(false)
                                progressDialog.show()
                                val cartAPI = retrofit.create(CartAPI::class.java)
                                cart.coupon_ids.remove(obj.id)
                                val call = cartAPI.updateUserCart(cart.id,cart)
                                call.enqueue(object : Callback<Cart>{
                                    override fun onResponse(call: Call<Cart>,response: Response<Cart>) {
                                      progressDialog.dismiss()
                                       if(response.code()==200){
                                           totalAmount = totalAmount -obj.price
                                           totalPrice.text = totalAmount.toString()
                                           arrayList.remove(obj)
                                           loadedNum--
                                           myCartOfferAdapter.mList.remove(obj)
                                           myCartOfferAdapter.notifyDataSetChanged()

                                           return
                                       }
                                        cart.coupon_ids.add(obj.id)
                                    }

                                    override fun onFailure(call: Call<Cart>, t: Throwable) {
                                             cart.coupon_ids.add(obj.id)
                                           progressDialog.dismiss()
                                             Toast.makeText(this@MyCartActivity,t.message,Toast.LENGTH_SHORT).show()
                                    }

                                })
                            }
                        }


                }).show()

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
        supportActionBar!!.title = "Your Cart"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this, R.color.grey_10)
        Tools.setSystemBarLight(this)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
        getCouponIDsFromApi()

    }


}



