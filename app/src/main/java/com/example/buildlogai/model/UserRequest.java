package com.example.buildlogai.model;

public class UserRequest {
    private String name;
    private String email;
    private String password;

    public UserRequest(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }
}