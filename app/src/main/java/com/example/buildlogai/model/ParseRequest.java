package com.example.buildlogai.model;

public class ParseRequest {
    public String text;
    public Long projectId;

    public ParseRequest(String text, Long projectId){
        this.text = text;
        this.projectId = projectId;
    }
}
