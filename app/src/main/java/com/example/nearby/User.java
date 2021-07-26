package com.example.nearby;

public class User {
    String username;
    String name;
    String email;
    String password;
    String phone;
    String address;

    public User(String username, String name, String email, String password, String phone, String address) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}






