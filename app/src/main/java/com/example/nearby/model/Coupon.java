package com.example.nearby.model;

import java.io.Serializable;

public class Coupon implements Serializable {
    String name;
    String description;
    int count;
    String image;
    String shopName;
    long id;
    String city;
    int price;
    String area;
    String code;
    public Coupon(){

    }
    public Coupon(String name, String description, int count, String image, String shopName,String city, int price, String area, String code) {
        this.name = name;
        this.description = description;
        this.count = count;
        this.image = image;
        this.shopName = shopName;
        this.id = id;
        this.city = city;
        this.price = price;
        this.area = area;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCount() {
        return count;
    }

    public String getImage() {
        return image;
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

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public int getPrice() {
        return price;
    }

}
