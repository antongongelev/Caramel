package com.example.caramel;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Position implements Serializable, Parcelable {

    private String name;
    private double price;
    private int quantity;
    private Image image;

    public Position(String name, double price, int quantity, Image image) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    public Position(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }


    protected Position(Parcel in) {
        name = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
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

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImage(Image image) {
        this.image = image;
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

    public Image getImage() {
        return this.image;
    }

    @NonNull
    public String getCreatedMessage() {
        return String.format("Позиция с именем \'%s\', ценой %s руб/шт в кол-ве %s шт была добавлена", name, price, quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeInt(quantity);
    }
}
