package com.example.buildlogai.model;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("email")
    private String email;
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;

    public UserResponse(){}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}