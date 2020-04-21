package com.example.caramel;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Position implements Serializable, Parcelable {

    private String id;
    private String name;
    private double price;
    private int quantity;
    private Bitmap image;

    public Position(String id, String name, double price, int quantity, Bitmap image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
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
        image = in.readParcelable(Bitmap.class.getClassLoader());
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

    public void setImage(Bitmap image) {
        this.image = image;
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

    public Bitmap getImage() {
        return this.image;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        return obj instanceof Position &&
                this.name.equals(((Position) obj).getName()) &&
                this.quantity == ((Position) obj).getQuantity() &&
                this.price == ((Position) obj).getPrice();
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
        dest.writeParcelable(image, flags);
    }
}
