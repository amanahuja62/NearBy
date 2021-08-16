package com.example.nearby.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nearby.R
import com.example.nearby.model.Coupon
import com.example.nearby.utils.Tools

class MyCartOfferAdapter(val context: Context, val layout: Int): RecyclerView.Adapter<MyCartOfferAdapter.ViewHolder>() {


    var mList = ArrayList<Coupon>()
     var mOnItemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartOfferAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return MyCartOfferAdapter.ViewHolder(view)
    }
    override fun getItemCount(): Int {
      return mList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coupon = mList[position]
        Tools.displayImageOriginal(context,holder.image,coupon.image)
        holder.shopName.text = coupon.shopName
        holder.price.text = coupon.price.toString()
        holder.couponCode.text = "Coupon Code: "+coupon.code.toString()

        holder.lyt.setOnClickListener(View.OnClickListener { view ->
            if (mOnItemClickListener == null) return@OnClickListener
            mOnItemClickListener!!.onItemClick(view, mList.get(position), position)
        })

    }
    public interface OnItemClickListener{
        fun onItemClick(view : View, obj: Coupon, position: Int )
    }
    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener
    }

    class ViewHolder(ItemView : View):RecyclerView.ViewHolder(ItemView){
        val image : ImageView = itemView.findViewById(R.id.image)
        val shopName : TextView = itemView.findViewById(R.id.shopName)
        val lyt : View = itemView.findViewById(R.id.lyt)
        val price : TextView = itemView.findViewById(R.id.price)
        val couponCode : TextView = itemView.findViewById(R.id.couponCode)
    }
   fun setData(arrayList: ArrayList<Coupon>){
       mList.clear()
       mList.addAll(arrayList)
       notifyDataSetChanged()
   }


}