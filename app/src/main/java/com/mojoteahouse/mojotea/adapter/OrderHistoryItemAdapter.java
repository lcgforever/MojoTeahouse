package com.mojoteahouse.mojotea.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.activity.PlaceOrderActivity;
import com.mojoteahouse.mojotea.data.Order;
import com.mojoteahouse.mojotea.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryItemAdapter extends RecyclerView.Adapter<OrderHistoryItemAdapter.OrderViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Order> orderList;
    private String orderSummaryFormat;
    private String orderPriceFormat;
    private String todayString;
    private SimpleDateFormat fullDateFormat;
    private SimpleDateFormat hourFormat;
    private OrderHistoryClickListener orderHistoryClickListener;

    public interface OrderHistoryClickListener {

        void onOrderHistoryClicked(Order order);
    }

    public OrderHistoryItemAdapter(Context context, List<Order> orderList, OrderHistoryClickListener listener) {
        layoutInflater = LayoutInflater.from(context);
        this.orderList = orderList;
        orderSummaryFormat = context.getString(R.string.order_summary_additional_count_format);
        orderPriceFormat = context.getString(R.string.price_format);
        todayString = context.getString(R.string.today_text);
        fullDateFormat = new SimpleDateFormat("EEE, MMM dd yyyy, h:mm a", Locale.getDefault());
        hourFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        orderHistoryClickListener = listener;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.order_history_list_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        String orderTimeString = order.getOrderTime();
        Date orderTimeDate = DateUtil.stringToDate(orderTimeString);
        Date today = new Date();
        if (isSameDay(orderTimeDate, today)) {
            orderTimeString = todayString + " " + hourFormat.format(orderTimeDate);
        } else {
            orderTimeString = fullDateFormat.format(orderTimeDate);
        }
        holder.orderTimeText.setText(orderTimeString);
        holder.orderStatusText.setText(order.getStatus());
        holder.orderPriceText.setText(String.format(orderPriceFormat, order.getTotalPrice()));

        List<String> completeOrderList = order.getCompleteOrderList();
        StringBuilder stringBuilder = new StringBuilder();
        String orderItemName;
        for (int i = 0; i < completeOrderList.size(); i++) {
            orderItemName = completeOrderList.get(i).split(PlaceOrderActivity.NAME_SPLIT_SYMBOL)[0];
            if (i == 0) {
                stringBuilder.append(orderItemName);
            } else if (i == 1) {
                stringBuilder.append(", ")
                        .append(orderItemName);
            } else {
                stringBuilder.append(" ")
                        .append(String.format(orderSummaryFormat, completeOrderList.size() - 2));
                break;
            }
        }
        holder.orderSummaryText.setText(stringBuilder.toString());
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public void updateOrderList(List<Order> orderList) {
        if (orderList != null) {
            Collections.sort(orderList);
            this.orderList.clear();
            this.orderList.addAll(orderList);
            notifyDataSetChanged();
        }
    }

    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }


    class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView orderTimeText;
        private TextView orderSummaryText;
        private TextView orderStatusText;
        private TextView orderPriceText;

        OrderViewHolder(View itemView) {
            super(itemView);

            orderTimeText = (TextView) itemView.findViewById(R.id.order_time_text);
            orderSummaryText = (TextView) itemView.findViewById(R.id.order_summary_text);
            orderStatusText = (TextView) itemView.findViewById(R.id.order_status_text);
            orderPriceText = (TextView) itemView.findViewById(R.id.order_price_text);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Order order = orderList.get(getLayoutPosition());
            orderHistoryClickListener.onOrderHistoryClicked(order);
        }
    }
}
