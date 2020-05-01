package com.example.caramel.persist;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Position implements Serializable, Parcelable {

    private String id;
    private String name;
    private double price;
    private int quantity;
    private String soldTime;
    private String imageName;
    private String barcode;
    private int categoryId;

    public Position(String id, String name, double price, int quantity, String imageName, String barcode, int categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageName = imageName;
        this.barcode = barcode;
        this.categoryId = categoryId;
    }

    public Position(String id, String name, double price, int quantity, String imageName, String barcode) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageName = imageName;
        this.barcode = barcode;
        this.categoryId = categoryId;
    }

    public Position(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    protected Position(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
        soldTime = in.readString();
        imageName = in.readString();
        barcode = in.readString();
        categoryId = in.readInt();
    }

    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setSoldTime(String time) {
        this.soldTime = time;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getImageName() {
        return this.imageName;
    }

    public String getSoldTime() {
        return this.soldTime;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        return obj instanceof Position &&
                this.name.equals(((Position) obj).getName()) &&
                this.quantity == ((Position) obj).getQuantity() &&
                this.price == ((Position) obj).getPrice() &&
                this.categoryId == ((Position) obj).getCategoryId();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM HH:mm");
        Date now = new Date();
        return format.format(now);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeString(soldTime);
        dest.writeString(imageName);
        dest.writeString(barcode);
        dest.writeInt(categoryId);
    }
}