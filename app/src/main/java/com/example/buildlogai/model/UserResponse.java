package com.example.buildlogai.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserResponse implements Parcelable {
    @SerializedName("email")
    private String email;
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;

    public UserResponse(){}

    protected UserResponse(Parcel in) {
        email = in.readString();
        id = (Long) in.readValue(Long.class.getClassLoader());
        name = in.readString();
    }

    public static final Creator<UserResponse> CREATOR =
            new Creator<UserResponse>() {
                @Override
                public UserResponse createFromParcel(Parcel in) {
                    return new UserResponse(in);
                }

                @Override
                public UserResponse[] newArray(int size) {
                    return new UserResponse[size];
                }
            };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeValue(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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