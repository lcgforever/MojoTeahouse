package com.mojoteahouse.mojotea.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class Topping implements Parcelable, Comparable<Topping> {

    private String id;
    private String name;
    private String chineseName;
    private double price;
    private boolean isSoldOut;

    public Topping(Map<String, Object> toppingData) {
        id = toppingData.get("id").toString();
        name = toppingData.containsKey("name") ? toppingData.get("name").toString() : "";
        chineseName = toppingData.containsKey("chineseName") ? toppingData.get("chineseName").toString() : "";
        price = toppingData.containsKey("price") ? Double.parseDouble(toppingData.get("price").toString()) : 0;
        isSoldOut = toppingData.containsKey("isSoldOut") && (boolean) toppingData.get("isSoldOut");
    }

    private Topping(Parcel in) {
        id = in.readString();
        name = in.readString();
        chineseName = in.readString();
        price = in.readDouble();
        isSoldOut = in.readByte() != 0;
    }

    public static final Creator<Topping> CREATOR = new Creator<Topping>() {
        @Override
        public Topping createFromParcel(Parcel in) {
            return new Topping(in);
        }

        @Override
        public Topping[] newArray(int size) {
            return new Topping[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(chineseName);
        dest.writeDouble(price);
        dest.writeByte((byte) (isSoldOut ? 1 : 0));
    }

    @Override
    public int compareTo(Topping another) {
        if (isSoldOut == another.isSoldOut()) {
            if (name == null) {
                return 1;
            }
            return name.compareTo(another.getName());
        } else if (isSoldOut) {
            return 1;
        } else {
            return -1;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getChineseName() {
        return chineseName;
    }

    public double getPrice() {
        return price;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }
}