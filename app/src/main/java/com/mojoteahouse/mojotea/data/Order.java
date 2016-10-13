package com.mojoteahouse.mojotea.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order implements Parcelable, Comparable<Order> {

    private String id;
    private String orderTime;
    private String deliverByTime;
    private List<String> completeOrderList;
    private int totalQuantity;
    private double totalPrice;
    private String customerName;
    private String customerAddress;
    private String customerPhoneNumber;
    private String customerZip;
    private String customerNote;
    private String status;
    private List<OrderItem> orderItemList;

    public Order() {
        completeOrderList = new ArrayList<>();
        orderItemList = new ArrayList<>();
    }

    public Order(MojoData mojoData, Map<String, Object> orderDataMap) {
        id = orderDataMap.get("id").toString();
        orderTime = orderDataMap.get("orderTime").toString();
        deliverByTime = orderDataMap.get("deliverByTime").toString();
        customerName = orderDataMap.get("customerName").toString();
        customerAddress = orderDataMap.get("customerAddress").toString();
        customerZip = orderDataMap.get("customerZip").toString();
        customerPhoneNumber = orderDataMap.get("customerPhoneNumber").toString();
        customerNote = orderDataMap.get("customerNote").toString();
        totalQuantity = Integer.parseInt(orderDataMap.get("totalQuantity").toString());
        totalPrice = Double.parseDouble(orderDataMap.get("totalPrice").toString());
        status = orderDataMap.containsKey("status") ? orderDataMap.get("status").toString() : "";
        completeOrderList = new ArrayList<>();
        if (orderDataMap.containsKey("completeOrderList")) {
            completeOrderList.addAll((List<String>) orderDataMap.get("completeOrderList"));
        }
        orderItemList = new ArrayList<>();
        if (orderDataMap.containsKey("orderItemList")) {
            List<Map<String, Object>> orderItemDataList = (List<Map<String, Object>>) orderDataMap.get("orderItemList");
            for (Map<String, Object> orderItemData : orderItemDataList) {
                orderItemList.add(new OrderItem(mojoData, orderItemData));
            }
        }
    }

    protected Order(Parcel in) {
        id = in.readString();
        orderTime = in.readString();
        deliverByTime = in.readString();
        completeOrderList = in.createStringArrayList();
        totalQuantity = in.readInt();
        totalPrice = in.readDouble();
        customerName = in.readString();
        customerAddress = in.readString();
        customerZip = in.readString();
        customerPhoneNumber = in.readString();
        customerNote = in.readString();
        status = in.readString();
        orderItemList = in.createTypedArrayList(OrderItem.CREATOR);
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(orderTime);
        dest.writeString(deliverByTime);
        dest.writeStringList(completeOrderList);
        dest.writeInt(totalQuantity);
        dest.writeDouble(totalPrice);
        dest.writeString(customerName);
        dest.writeString(customerAddress);
        dest.writeString(customerZip);
        dest.writeString(customerPhoneNumber);
        dest.writeString(customerNote);
        dest.writeString(status);
        dest.writeTypedList(orderItemList);
    }

    @Override
    public int compareTo(@Nullable Order another) {
        if (another == null) {
            return -1;
        }
        return another.getOrderTime().compareTo(orderTime);
    }

    public Map<String, Object> getDataMap() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", id);
        dataMap.put("orderTime", orderTime);
        dataMap.put("deliverByTime", deliverByTime);
        dataMap.put("completeOrderList", completeOrderList);
        dataMap.put("totalQuantity", totalQuantity);
        dataMap.put("totalPrice", totalPrice);
        dataMap.put("customerName", customerName);
        dataMap.put("customerAddress", customerAddress);
        dataMap.put("customerZip", customerZip);
        dataMap.put("customerPhoneNumber", customerPhoneNumber);
        dataMap.put("customerNote", customerNote);
        dataMap.put("status", status);
        List<Map<String, Object>> orderItemDataMaps = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            orderItemDataMaps.add(orderItem.getDataMap());
        }
        dataMap.put("orderItemList", orderItemDataMaps);
        return dataMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getDeliverByTime() {
        return deliverByTime;
    }

    public void setDeliverByTime(String deliverByTime) {
        this.deliverByTime = deliverByTime;
    }

    public List<String> getCompleteOrderList() {
        return completeOrderList;
    }

    public void setCompleteOrderList(List<String> completeOrderList) {
        this.completeOrderList = completeOrderList;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerZip() {
        return customerZip;
    }

    public void setCustomerZip(String customerZip) {
        this.customerZip = customerZip;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItemList.add(orderItem);
        Collections.sort(orderItemList);
    }

    public OrderItem getOrderItemById(String orderItemId) {
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getId().equals(orderItemId)) {
                return orderItem;
            }
        }
        return null;
    }
}
