package com.example.nearby.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nearby.R;
import com.example.nearby.model.Coupon;
import com.example.nearby.utils.Tools;


import java.util.ArrayList;
import java.util.List;

public class UserOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Coupon> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Coupon obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public UserOfferAdapter(Context context) {
           ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView shopName;
        public TextView offerLocation;
        public TextView offerName;
        public TextView offerPrice;
        public LinearLayout lyt_bottom;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
             image = v.findViewById(R.id.image);
             shopName = v.findViewById(R.id.shopName);
             offerLocation = v.findViewById(R.id.offerLocation);
             offerName = v.findViewById(R.id.offerName);
             lyt_bottom = v.findViewById(R.id.lyt_bottom);
             offerPrice = v.findViewById(R.id.textView2);
             lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indiv_user_offer, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Coupon obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.offerName.setText(obj.getName());
            view.offerLocation.setText(obj.getArea()+", "+obj.getCity());
            view.offerPrice.setText(String.valueOf(obj.getPrice()));
            view.shopName.setText(obj.getShopName());
            Tools.displayImageOriginal(ctx, view.image, obj.getImage());
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
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


}