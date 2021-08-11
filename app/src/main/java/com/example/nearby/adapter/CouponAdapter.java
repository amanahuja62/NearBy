package com.example.nearby.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nearby.R;
import com.example.nearby.utils.Tools;
import com.example.nearby.model.Coupon;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Coupon> items = new ArrayList<>();
    FirebaseStorage storage;
    StorageReference ref;
    private Context ctx;

    @LayoutRes
    private int layout_id;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Coupon obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public CouponAdapter(Context context,  @LayoutRes int layout_id) {
        storage = FirebaseStorage.getInstance();
        ctx = context;
        this.layout_id = layout_id;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView description;
        public TextView scc; // shopname, count and code
        public TextView city;
        public TextView area;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            image = v.findViewById(R.id.image);
            description = v.findViewById(R.id.desctiption);
            scc = v.findViewById(R.id.scc);
            city = v.findViewById(R.id.city);
            area = v.findViewById(R.id.area);
            lyt_parent = v.findViewById(R.id.lyt_parent);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout_id, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Coupon n = items.get(position);
            view.name.setText(n.getName());
            view.description.setText(n.getDescription());
            view.scc.setText(n.getShopName()+"\n"+"Count = "+n.getCount()+"\n"+"Coupon Code = "+n.getCode()
            +"\n"+"Price = Rs "+n.getPrice());

            view.city.setText(n.getCity());
            view.area.setText(n.getArea());
         /*   ref = storage.getReferenceFromUrl(n.getImage());*/
            Tools.displayImageOriginal(ctx, view.image, n.getImage());
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(view, items.get(position), position);

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
    public void deleteAndRefresh(Coupon coupon){
        items.remove(coupon);
        notifyDataSetChanged();
    }

    public int getItemPosition(Coupon coupon){
        return items.indexOf(coupon);

    }

    public void updateAndRefresh(int position, Coupon coupon){
        //used for updating a coupon in itemsList
        items.set(position,coupon);
        notifyDataSetChanged();

    }

    public long getCouponIdPresentAtIndex(int position){
        return items.get(position).getId();
    }



}