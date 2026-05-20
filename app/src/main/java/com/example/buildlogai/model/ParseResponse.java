package com.example.buildlogai.model;

import java.util.List;

public class ParseResponse {
    private List<RecordDTO> records;

    public List<RecordDTO> getRecords() {
        return records;
    }

    public void setRecords(List<RecordDTO> records) {
        this.records = records;
    }
}
