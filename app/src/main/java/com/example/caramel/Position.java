package com.example.caramel;

import android.os.Parcel;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Position implements Serializable {

    private String name;
    private double price;
    private int quantity;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    @NonNull
    public String getCreatedMessage() {
        return String.format("Позиция с именем \'%s\', ценой %s руб/шт в кол-ве %s шт была добавлена", name, price, quantity);
    }
}
