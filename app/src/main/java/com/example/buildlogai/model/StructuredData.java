package com.example.buildlogai.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class StructuredData implements Parcelable {

    private String company;

    private String subject;

    private Double quantity;

    private String unit;

    @SerializedName("due_date")
    private String dueDate;

    private Double percentage;

    private Double price;

    // Constructor vacío (Gson)
    public StructuredData() {}

    // Constructor Parcel
    protected StructuredData(Parcel in) {

        company = in.readString();

        subject = in.readString();

        quantity = (Double) in.readValue(Double.class.getClassLoader());

        unit = in.readString();

        dueDate = in.readString();

        percentage = (Double) in.readValue(Double.class.getClassLoader());

        price = (Double) in.readValue(Double.class.getClassLoader());
    }

    // CREATOR
    public static final Creator<StructuredData> CREATOR =
            new Creator<StructuredData>() {

                @Override
                public StructuredData createFromParcel(Parcel in) {
                    return new StructuredData(in);
                }

                @Override
                public StructuredData[] newArray(int size) {
                    return new StructuredData[size];
                }
            };

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(company);

        dest.writeString(subject);

        dest.writeValue(quantity);

        dest.writeString(unit);

        dest.writeString(dueDate);

        dest.writeValue(percentage);

        dest.writeValue(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    public boolean isEmpty() {

        return company == null
                && subject == null
                && quantity == null
                && unit == null
                && dueDate == null
                && percentage == null
                && price == null;
    }
}