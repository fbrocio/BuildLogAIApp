package com.example.buildlogai.model;

public class ReportRequestDTO {
    private String topic;

    public ReportRequestDTO(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
