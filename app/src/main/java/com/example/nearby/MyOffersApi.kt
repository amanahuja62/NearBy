package com.example.nearby

import com.example.nearby.model.*
import retrofit2.Call
import retrofit2.http.*

interface MyOffersApi {
    @Headers("key: password")
    @GET()
    fun getPurchasedOffers(@Url url : String):Call<List<MyOffers>>

    @Headers("key: password")
    @POST("api/myOffers/payment-success/{phone}/{address}")
    fun purchaseItemsInCart(@Path("phone")phone:String, @Path("address")address:String,@Body postPurchase: PostPurchase): Call<PostPurchaseResponse>


}