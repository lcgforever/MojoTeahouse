package com.mojoteahouse.mojotea.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
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

    public OrderItem(MojoData mojoData, Map<String, Object> orderItemDataMap) {
        id = orderItemDataMap.get("id").toString();
        String mojoMenuId = orderItemDataMap.get("mojoMenuId").toString();
        mojoMenu = mojoData.getMojoMenuById(mojoMenuId);
        quantity = Integer.parseInt(orderItemDataMap.get("quantity").toString());
        totalPrice = Double.parseDouble(orderItemDataMap.get("totalPrice").toString());
        selectedToppingPrice = orderItemDataMap.containsKey("selectedToppingPrice")
                ? Double.parseDouble(orderItemDataMap.get("selectedToppingPrice").toString()) : 0;
        note = orderItemDataMap.containsKey("note") ? orderItemDataMap.get("note").toString() : "";
        selectedToppings = new ArrayList<>();
        if (orderItemDataMap.containsKey("selectedToppingIds")) {
            List<String> selectedToppingIdList = (List<String>) orderItemDataMap.get("selectedToppingIds");
            for (String toppingId : selectedToppingIdList) {
                selectedToppings.add(mojoData.getToppingById(toppingId));
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
    public int compareTo(@Nullable OrderItem another) {
        if (another == null) {
            return -1;
        }
        return id.compareTo(another.getId());
    }

    Map<String, Object> getDataMap() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", id);
        dataMap.put("mojoMenuId", mojoMenu.getId());
        dataMap.put("totalPrice", totalPrice);
        dataMap.put("quantity", quantity);
        List<String> selectedToppingIds = new ArrayList<>();
        for (Topping topping : selectedToppings) {
            selectedToppingIds.add(topping.getId());
        }
        dataMap.put("selectedToppingIds", selectedToppingIds);
        dataMap.put("selectedToppingPrice", selectedToppingPrice);
        dataMap.put("note", note);
        return dataMap;
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
