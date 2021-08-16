package com.example.nearby.model

class MyOffers {
    var id : Long = 0
    var userId : Long = 0
    var phone : String? = null
    var email : String? = null
    var address : String? = null
    var coupons = ArrayList<Long>()
    var transactionDate : String? = null
}