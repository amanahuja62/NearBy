package com.example.nearby.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    long id;
    long createdBy;
    ArrayList<Long> coupon_ids;

    public Cart(long id, long createdBy, ArrayList<Long> coupon_ids) {
        this.id = id;
        this.createdBy = createdBy;
        this.coupon_ids = coupon_ids;
    }

    public long getId() {
        return id;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public ArrayList<Long> getCoupon_ids() {
        return coupon_ids;
    }
}
