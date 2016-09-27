package com.mojoteahouse.mojotea.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MojoData implements Parcelable {

    private ArrayList<MojoMenu> mojoMenuList;
    private ArrayList<Topping> toppingList;
    private ArrayList<String> availableZipList;

    public MojoData() {
        mojoMenuList = new ArrayList<>();
        toppingList = new ArrayList<>();
        availableZipList = new ArrayList<>();
    }

    private MojoData(Parcel in) {
        mojoMenuList = in.createTypedArrayList(MojoMenu.CREATOR);
        toppingList = in.createTypedArrayList(Topping.CREATOR);
        availableZipList = in.createStringArrayList();
    }

    public static final Creator<MojoData> CREATOR = new Creator<MojoData>() {
        @Override
        public MojoData createFromParcel(Parcel in) {
            return new MojoData(in);
        }

        @Override
        public MojoData[] newArray(int size) {
            return new MojoData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mojoMenuList);
        dest.writeTypedList(toppingList);
        dest.writeStringList(availableZipList);
    }

    public ArrayList<MojoMenu> getMojoMenuList() {
        return mojoMenuList;
    }

    public void setMojoMenuList(ArrayList<MojoMenu> mojoMenuList) {
        this.mojoMenuList = mojoMenuList;
    }

    public ArrayList<Topping> getToppingList() {
        return toppingList;
    }

    public void setToppingList(ArrayList<Topping> toppingList) {
        this.toppingList = toppingList;
    }

    public ArrayList<String> getAvailableZipList() {
        return availableZipList;
    }

    public void setAvailableZipList(ArrayList<String> availableZipList) {
        this.availableZipList = availableZipList;
    }
}
