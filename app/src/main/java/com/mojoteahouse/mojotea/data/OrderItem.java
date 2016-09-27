package com.mojoteahouse.mojotea.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderItem implements Parcelable, Comparable<OrderItem> {

    private String id;
    private MojoMenu mojoMenu;
    private double totalPrice;
    private int quantity;
    private List<Topping> selectedToppings;
    private double selectedToppingPrice;
    private String note;

    public OrderItem() {
        selectedToppings = new ArrayList<>();
    }

    public OrderItem(Map<String, Object> orderItemDataMap) {
        id = orderItemDataMap.get("id").toString();
        quantity = Integer.parseInt(orderItemDataMap.get("quantity").toString());
        totalPrice = Double.parseDouble(orderItemDataMap.get("totalPrice").toString());
        selectedToppingPrice = orderItemDataMap.containsKey("selectedToppingPrice")
                ? Double.parseDouble(orderItemDataMap.get("selectedToppingPrice").toString()) : 0;
        note = orderItemDataMap.containsKey("note") ? orderItemDataMap.get("note").toString() : "";
        if (orderItemDataMap.containsKey("mojoMenu")) {
            mojoMenu = new MojoMenu((Map<String, Object>) orderItemDataMap.get("mojoMenu"));
        } else {
            mojoMenu = new MojoMenu();
        }
        selectedToppings = new ArrayList<>();
        if (orderItemDataMap.containsKey("selectedToppings")) {
            List<Map<String, Object>> toppingDataList = (List<Map<String, Object>>) orderItemDataMap.get("selectedToppings");
            for (Map<String, Object> toppingData : toppingDataList) {
                selectedToppings.add(new Topping(toppingData));
            }
        }
    }

    private OrderItem(Parcel in) {
        id = in.readString();
        mojoMenu = in.readParcelable(MojoMenu.class.getClassLoader());
        totalPrice = in.readDouble();
        quantity = in.readInt();
        selectedToppings = in.createTypedArrayList(Topping.CREATOR);
        selectedToppingPrice = in.readDouble();
        note = in.readString();
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(mojoMenu, flags);
        dest.writeDouble(totalPrice);
        dest.writeInt(quantity);
        dest.writeTypedList(selectedToppings);
        dest.writeDouble(selectedToppingPrice);
        dest.writeString(note);
    }

    @Override
    public int compareTo(OrderItem another) {
        return id.compareTo(another.getId());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MojoMenu getMojoMenu() {
        return mojoMenu;
    }

    public void setMojoMenu(MojoMenu mojoMenu) {
        this.mojoMenu = mojoMenu;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<Topping> getSelectedToppings() {
        return selectedToppings;
    }

    public void setSelectedToppings(List<Topping> selectedToppings) {
        this.selectedToppings = selectedToppings;
    }

    public double getSelectedToppingPrice() {
        return selectedToppingPrice;
    }

    public void setSelectedToppingPrice(double selectedToppingPrice) {
        this.selectedToppingPrice = selectedToppingPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
