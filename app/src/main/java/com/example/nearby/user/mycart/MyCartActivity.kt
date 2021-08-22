package com.example.nearby.user.mycart

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.nearby.R
import com.example.nearby.adapter.MyCartOfferAdapter
import com.example.nearby.model.Cart
import com.example.nearby.model.Coupon
import com.example.nearby.user.offer.UserOfferDetails
import com.example.nearby.utils.Tools
import com.google.gson.Gson
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
    lateinit var arrayList : ArrayList<Coupon>
    lateinit var progressDialog : ProgressDialog
    lateinit var sp : SharedPreferences
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
        getCartIDusingUserID()

    }

    private fun getCartIDusingUserID() {
       val tempList = ArrayList<Coupon>()
        myCartOfferAdapter.setData(tempList)
        totalPrice.text = "0"
        val cartAPI = retrofit.create(CartAPI::class.java)
        val call = cartAPI.getUserCart("/api/cart/get-cart/"+userId.toString())
        progressBar.visibility = View.VISIBLE
        call.enqueue(object : Callback<Cart>{
            override fun onResponse(call: Call<Cart>, response: Response<Cart>) {
                if(response.code()==200){
                    totalAmount = 0
                    cart  = response.body()!!
                    //now that I have cart id, I can hit the api and get List<Coupon> using cartID
                    setCouponsInAdapter(cart.id,cartAPI)
                }
            }

            override fun onFailure(call: Call<Cart>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@MyCartActivity,t.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

   // This method hits api and gets List<Coupon> and sets them in the adapter
    private fun setCouponsInAdapter(id: Long, cartAPI: CartAPI) {
        val call = cartAPI.getCouponsOfCartHavingID(id)
        call.enqueue(object : Callback<ArrayList<Coupon>>{
            override fun onResponse(call: Call<ArrayList<Coupon>>,response: Response<ArrayList<Coupon>>) {
                progressBar.visibility = View.GONE
                if(response.code() != 200){
                    Toast.makeText(this@MyCartActivity,response.code().toString(),Toast.LENGTH_SHORT).show()
                    return
                }
                arrayList = response.body()!!
                if(arrayList.size == 0){
                    Toast.makeText(this@MyCartActivity,"Your cart is empty !",Toast.LENGTH_LONG).show()
                    return
                }

                for(coupon in arrayList)
                    totalAmount = totalAmount + coupon.price
                totalPrice.text = totalAmount.toString()
                myCartOfferAdapter.setData(arrayList)
            }

            override fun onFailure(call: Call<ArrayList<Coupon>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@MyCartActivity,t.message,Toast.LENGTH_SHORT).show()
            }

        })

    }


    private fun initComponent() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = myCartOfferAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        totalPrice = findViewById(R.id.price)
        progressBar = findViewById(R.id.progressBar3)
        materialRippleLayout = findViewById(R.id.checkoutButton2)

        materialRippleLayout.setOnClickListener {
            if (!(this::cart.isInitialized) || !(this::arrayList.isInitialized) ){
                Toast.makeText(this,"Please wait...",Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this@MyCartActivity,CheckoutActivity::class.java)
                intent.putExtra("couponsList",arrayList)
                intent.putExtra("totalPrice",totalAmount)
                intent.putExtra("cartId",cart.id)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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
                                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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
                                           val gson = Gson()
                                           val json = gson.toJson(cart.coupon_ids)
                                           sp  = getSharedPreferences("users", MODE_PRIVATE)
                                           sp.edit().putString("notPurchasedCoupons", json).apply()

                                           myCartOfferAdapter.mList.remove(obj)
                                           myCartOfferAdapter.notifyDataSetChanged()

                                           return
                                       }
                                        Toast.makeText(this@MyCartActivity,response.code().toString(),Toast.LENGTH_SHORT).show()
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
        getCartIDusingUserID()

    }


}




