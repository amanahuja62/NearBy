package com.example.nearby.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nearby.R;
import com.example.nearby.model.Coupon;

import java.util.ArrayList;
import java.util.List;

public class MyOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Coupon> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private Context ctx;

    public interface OnItemClickListener{
        public void onItemClick(View view, Coupon obj, int position);
    }
    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public MyOfferAdapter(Context context){
        ctx = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indiv_my_offer,parent,false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
          Coupon obj = items.get(position);
          if(holder instanceof OriginalViewHolder){
              OriginalViewHolder view = (OriginalViewHolder) holder;
              view.offerPrice.setText(String.valueOf(obj.getPrice()));
              view.shopName.setText(obj.getShopName());
            /*  view.purchasedDate.setText();*/
          }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<Coupon> couponList){
        items.clear();
        items.addAll(couponList);
        notifyDataSetChanged();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder{
        TextView shopName;
        TextView purchasedDate;
        TextView offerPrice;
        ImageView imageView;
        public OriginalViewHolder(@NonNull View itemView) {
            super(itemView);
            shopName = itemView.findViewById(R.id.shopName);
            purchasedDate = itemView.findViewById(R.id.purchasedDate);
            offerPrice = itemView.findViewById(R.id.offerAmount);
            imageView = itemView.findViewById(R.id.purchasedOfferImage);

        }
    }


}
