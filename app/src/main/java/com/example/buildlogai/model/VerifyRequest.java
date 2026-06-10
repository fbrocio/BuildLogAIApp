package com.example.buildlogai.model;

public class VerifyRequest {

    private String email;
    private String code;

    public VerifyRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }
}