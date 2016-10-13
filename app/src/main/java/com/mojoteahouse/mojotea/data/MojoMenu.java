package com.mojoteahouse.mojotea.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MojoMenu implements Parcelable, Comparable<MojoMenu> {

    public static final Creator<MojoMenu> CREATOR = new Creator<MojoMenu>() {
        @Override
        public MojoMenu createFromParcel(Parcel in) {
            return new MojoMenu(in);
        }

        @Override
        public MojoMenu[] newArray(int size) {
            return new MojoMenu[size];
        }
    };

    private String id;
    private String imageUrl;
    private String name;
    private String chineseName;
    private double price;
    private List<String> toppingIds;
    private String category;
    private boolean isNewMenu;
    private boolean isSoldOut;

    public MojoMenu() {

    }

    public MojoMenu(Map<String, Object> menuDataMap) {
        id = menuDataMap.get("id").toString();
        imageUrl = menuDataMap.containsKey("imageUrl") ? menuDataMap.get("imageUrl").toString() : "";
        name = menuDataMap.containsKey("name") ? menuDataMap.get("name").toString() : "";
        chineseName = menuDataMap.containsKey("chineseName") ? menuDataMap.get("chineseName").toString() : "";
        category = menuDataMap.containsKey("category") ? menuDataMap.get("category").toString() : "";
        price = menuDataMap.containsKey("price") ? Double.parseDouble(menuDataMap.get("price").toString()) : 0;
        isNewMenu = menuDataMap.containsKey("isNewMenu") && (boolean) menuDataMap.get("isNewMenu");
        isSoldOut = menuDataMap.containsKey("isSoldOut") && (boolean) menuDataMap.get("isSoldOut");
        toppingIds = new ArrayList<>();
        if (menuDataMap.containsKey("toppingIds")) {
            toppingIds.addAll((List<String>) menuDataMap.get("toppingIds"));
        }
    }

    private MojoMenu(Parcel in) {
        id = in.readString();
        imageUrl = in.readString();
        name = in.readString();
        chineseName = in.readString();
        price = in.readDouble();
        toppingIds = new ArrayList<>();
        in.readStringList(toppingIds);
        category = in.readString();
        isNewMenu = in.readByte() != 0;
        isSoldOut = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeString(chineseName);
        dest.writeDouble(price);
        dest.writeStringList(toppingIds);
        dest.writeString(category);
        dest.writeByte((byte) (isNewMenu ? 1 : 0));
        dest.writeByte((byte) (isSoldOut ? 1 : 0));
    }

    @Override
    public int compareTo(@Nullable MojoMenu another) {
        if (another == null) {
            return -1;
        }
        if (isSoldOut == another.isSoldOut()) {
            if (isNewMenu == another.isNewMenu()) {
                if (name == null) {
                    return 1;
                }
                return name.compareTo(another.getName());
            } else if (isNewMenu) {
                return -1;
            } else {
                return 1;
            }
        } else if (isSoldOut) {
            return 1;
        } else {
            return -1;
        }
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public List<String> getToppingIds() {
        return toppingIds;
    }

    public String getCategory() {
        return category;
    }

    public boolean isNewMenu() {
        return isNewMenu;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }
}