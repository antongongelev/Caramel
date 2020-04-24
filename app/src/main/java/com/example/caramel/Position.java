package com.example.caramel;

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

    public Position(String id, String name, double price, int quantity, String imageName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageName = imageName;
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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeString(soldTime);
        dest.writeString(imageName);
    }

    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        return obj instanceof Position &&
                this.name.equals(((Position) obj).getName()) &&
                this.quantity == ((Position) obj).getQuantity() &&
                this.price == ((Position) obj).getPrice();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM HH:mm");
        return format.format(new Date());
    }

    //AND FIX THAT
//    public static String setImage(Bitmap image) {
//        if (image != null) {
//            FileOutputStream outputStream = null;
//            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Caramel");
//            dir.mkdir();
//            String fileName = System.currentTimeMillis() + ".jpg";
//            File file = new File(dir, fileName);
//            try {
//                outputStream = new FileOutputStream(file);
//                image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                outputStream.flush();
//                outputStream.close();
//                return fileName;
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (outputStream != null) {
//                        outputStream.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }

    //FIX THAT SHIT
//    public Bitmap getImage() {
//        if (this.imageName != null) {
//            FileInputStream inputStream = null;
//            try {
//                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Caramel");
//                dir.mkdir();
//                File file = new File(dir, this.imageName);
//                inputStream = new FileInputStream(file);
//                return BitmapFactory.decodeStream(inputStream);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }
}