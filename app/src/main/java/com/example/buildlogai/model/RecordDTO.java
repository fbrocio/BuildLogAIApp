package com.example.buildlogai.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RecordDTO implements Parcelable {

    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private String type;
    private String status;
    private StructuredData structuredData;
    @SerializedName("createdBy")
    private UserResponse createdBy;
    private String createdAt;
    private transient Uri localImageUri;

    // Constructor vacío (necesario para Gson)
    public RecordDTO() {}

    // Constructor desde Parcel
    protected RecordDTO(Parcel in) {
        id = (Long) in.readValue(Long.class.getClassLoader());
        projectId = (Long) in.readValue(Long.class.getClassLoader());
        title = in.readString();
        description = in.readString();
        type = in.readString();
        status = in.readString();
        structuredData = in.readParcelable(StructuredData.class.getClassLoader());
        createdAt = in.readString();
    }

    // CREATOR (obligatorio)
    public static final Creator<RecordDTO> CREATOR = new Creator<RecordDTO>() {
        @Override
        public RecordDTO createFromParcel(Parcel in) {
            return new RecordDTO(in);
        }

        @Override
        public RecordDTO[] newArray(int size) {
            return new RecordDTO[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(projectId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(status);
        dest.writeParcelable(structuredData, flags);
        dest.writeString(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StructuredData getStructuredData() {
        return structuredData;
    }

    public void setStructuredData(StructuredData structuredData) {
        this.structuredData = structuredData;
    }

    public UserResponse getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserResponse createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Uri getLocalImageUri() {
        return localImageUri;
    }

    public void setLocalImageUri(Uri localImageUri) {
        this.localImageUri = localImageUri;
    }
}