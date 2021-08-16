package com.example.nearby.model

class PostPurchaseResponse (val id : Int, val userId : Int, val phone :String, val email:String,val address: String,val couponIds : ArrayList<Long>,
                 val transactionDate : String) {
}