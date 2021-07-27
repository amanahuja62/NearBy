package com.example.nearby;

public class Coupon {
    String offerName;
    String description;
    String couponCount;
    String imageURL;
    String shopName;
    String city;
    String area;
    String code;

    public Coupon(String offerName, String description, String couponCount, String imageURL, String shopName, String city, String area, String code) {
        this.offerName = offerName;
        this.description = description;
        this.couponCount = couponCount;
        this.imageURL = imageURL;
        this.shopName = shopName;
        this.city = city;
        this.area = area;
        this.code = code;
    }

    public String getOfferName() {
        return offerName;
    }

    public String getDescription() {
        return description;
    }

    public String getCouponCount() {
        return couponCount;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getShopName() {
        return shopName;
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public String getCode() {
        return code;
    }


}
