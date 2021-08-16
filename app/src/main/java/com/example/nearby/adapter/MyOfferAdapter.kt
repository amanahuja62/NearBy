package com.example.nearby.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nearby.R
import com.example.nearby.model.IndivOfferPurchased
import com.example.nearby.utils.Tools

class MyOfferAdapter(val context : Context):RecyclerView.Adapter<MyOfferAdapter.ViewHolder>() {

    var mList = ArrayList<IndivOfferPurchased>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOfferAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.indiv_my_offer,parent,false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyOfferAdapter.ViewHolder, position: Int) {
        val offerPurchased = mList[position]


        holder.price.text = offerPurchased.coupon.price.toString()
        holder.purchasedDate.text = offerPurchased.transactionDate
        holder.couponCode.text = offerPurchased.coupon.code
        holder.shopName.text = offerPurchased.coupon.shopName
        Tools.displayImageOriginal(context,holder.image,offerPurchased.coupon.image)

    }

    class ViewHolder(ItemView : View):RecyclerView.ViewHolder(ItemView) {
        val image : ImageView = itemView.findViewById(R.id.purchasedOfferImage)
        val shopName : TextView = itemView.findViewById(R.id.shopName)
        val price : TextView = itemView.findViewById(R.id.offerAmount)
        val purchasedDate : TextView = itemView.findViewById(R.id.purchasedDate)
        val couponCode : TextView = itemView.findViewById(R.id.code)
    }

    override fun getItemCount(): Int {
       return mList.size
    }

    fun setData(offersAvailedList : List<IndivOfferPurchased>):Unit{
        mList.clear()
        mList.addAll(offersAvailedList)
        notifyDataSetChanged()
    }
}