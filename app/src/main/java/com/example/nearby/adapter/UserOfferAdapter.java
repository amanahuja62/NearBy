package com.example.nearby.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nearby.R;
import com.example.nearby.model.Coupon;
import com.example.nearby.utils.Tools;


import java.util.ArrayList;
import java.util.List;

public class UserOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Coupon> items = new ArrayList<>();
    private long userId;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private onLikeButtonClickListener onLikeButtonClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Coupon obj, int position);
    }
    public interface onLikeButtonClickListener{
        void onLikeClikced(TextView textView, Coupon obj, ImageButton imageButton, ProgressBar progressBar);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public void setOnLikeButtonClickListener(final onLikeButtonClickListener onLikeButtonClickListener) {
        this.onLikeButtonClickListener = onLikeButtonClickListener;
    }

    public UserOfferAdapter(Context context, long userId) {
           ctx = context;
           this.userId = userId;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView shopName;
        public TextView offerLocation;
        public TextView offerName;
        public TextView offerPrice;
        public LinearLayout lyt_bottom;
        public ImageButton like;
        public View lyt_parent;
        public TextView noOfLikes;
        public ProgressBar progressBar;
        public OriginalViewHolder(View v) {
            super(v);
             image = v.findViewById(R.id.image);
             shopName = v.findViewById(R.id.shopName);
             offerLocation = v.findViewById(R.id.offerLocation);
             offerName = v.findViewById(R.id.offerName);
             lyt_bottom = v.findViewById(R.id.lyt_bottom);
             progressBar = v.findViewById(R.id.progressBar4);
             like = v.findViewById(R.id.btn_like);
             offerPrice = v.findViewById(R.id.textView2);
             lyt_parent = v.findViewById(R.id.lyt_parent);
             noOfLikes = v.findViewById(R.id.noOfLikes);

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

            if(obj.getLikedBy()==null) {
                view.noOfLikes.setText("0");
                view.like.setColorFilter(Color.parseColor("#EF9A9A"));

            }
            else {
                view.noOfLikes.setText(String.valueOf(obj.getLikedBy().size()));
                if (obj.getLikedBy().contains(userId))
                 view.like.setColorFilter(Color.parseColor("#C62828"));
                else
                    view.like.setColorFilter(Color.parseColor("#EF9A9A"));


            }
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
            view.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onLikeButtonClickListener != null) {
                        onLikeButtonClickListener.onLikeClikced(view.noOfLikes,items.get(position),view.like, view.progressBar);
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