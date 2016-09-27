package com.mojoteahouse.mojotea.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.data.MojoMenu;
import com.mojoteahouse.mojotea.data.OrderItem;
import com.mojoteahouse.mojotea.data.Topping;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CartSummaryItemAdapter extends RecyclerView.Adapter<CartSummaryItemAdapter.CartSummaryViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<OrderItem> orderItemList;
    private CartSummaryItemClickListener itemClickListener;
    private CartSummaryItemLongClickListener itemLongClickListener;
    private SparseBooleanArray selectedItemPositions;
    private String quantityFormat;
    private String priceFormat;

    public interface CartSummaryItemClickListener {

        void onCartSummaryItemClicked(int position);
    }

    public interface CartSummaryItemLongClickListener {

        void onCartSummaryItemLongClicked(int position);
    }

    public CartSummaryItemAdapter(Context context,
                                  List<OrderItem> orderItemList,
                                  CartSummaryItemClickListener itemClickListener,
                                  CartSummaryItemLongClickListener itemLongClickListener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.orderItemList = new ArrayList<>(orderItemList);
        Collections.sort(this.orderItemList, new OrderItemComparator());
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
        selectedItemPositions = new SparseBooleanArray();
        quantityFormat = context.getString(R.string.quantity_format);
        priceFormat = context.getString(R.string.price_format);
    }

    @Override
    public CartSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cart_summary_list_item, parent, false);
        return new CartSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CartSummaryViewHolder holder, int position) {
        OrderItem orderItem = orderItemList.get(position);

        MojoMenu mojoMenu = orderItem.getMojoMenu();
        String imageUrl = mojoMenu.getImageUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            Picasso.with(context)
                    .load(R.drawable.ic_no_image)
                    .into(holder.mojoMenuImageView);
        } else {
            Picasso.with(context)
                    .load(imageUrl)
                    .error(R.drawable.ic_no_image)
                    .into(holder.mojoMenuImageView);
        }
        holder.nameText.setText(mojoMenu.getName());
        holder.chineseNameText.setText(mojoMenu.getChineseName());
        holder.quantityText.setText(String.format(quantityFormat, orderItem.getQuantity()));
        holder.priceText.setText(String.format(priceFormat, orderItem.getTotalPrice()));
        String toppingString = "";
        List<Topping> toppingList = orderItem.getSelectedToppings();
        int size = toppingList.size();
        for (int i = 0; i < size; i++) {
            toppingString += toppingList.get(i).getName();
            if (i < size - 1) {
                toppingString += ", ";
            }
        }
        holder.toppingText.setText(toppingString);

        holder.summaryLayout.setActivated(selectedItemPositions.get(position));
    }

    @Override
    public int getItemCount() {
        return orderItemList == null
                ? 0
                : orderItemList.size();
    }

    public void updateOrderItemList(List<OrderItem> orderItemList) {
        if (orderItemList != null) {
            clearSelection();
            this.orderItemList.clear();
            this.orderItemList.addAll(orderItemList);
            Collections.sort(this.orderItemList, new OrderItemComparator());
            notifyDataSetChanged();
        }
    }

    public OrderItem getOrderItemAtPosition(int position) {
        return orderItemList.get(position);
    }

    public void clearSelection() {
        selectedItemPositions.clear();
        notifyDataSetChanged();
    }

    public void setSelectionAtPosition(int position, boolean selected) {
        if (selected) {
            selectedItemPositions.put(position, true);
        } else {
            selectedItemPositions.delete(position);
        }

        notifyDataSetChanged();
    }

    public boolean isSelectedAtPosition(int position) {
        return selectedItemPositions.get(position, false);
    }

    public int getSelectedCount() {
        return selectedItemPositions.size();
    }

    public SparseBooleanArray getSelectedItemPositions() {
        return selectedItemPositions;
    }


    class CartSummaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private LinearLayout summaryLayout;
        private ImageView mojoMenuImageView;
        private TextView nameText;
        private TextView chineseNameText;
        private TextView quantityText;
        private TextView toppingText;
        private TextView priceText;

        CartSummaryViewHolder(View itemView) {
            super(itemView);

            summaryLayout = (LinearLayout) itemView.findViewById(R.id.summary_layout);
            mojoMenuImageView = (ImageView) itemView.findViewById(R.id.image_view);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            chineseNameText = (TextView) itemView.findViewById(R.id.chinese_name_text);
            quantityText = (TextView) itemView.findViewById(R.id.quantity_text);
            toppingText = (TextView) itemView.findViewById(R.id.topping_text);
            priceText = (TextView) itemView.findViewById(R.id.price_text);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onCartSummaryItemClicked(getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            itemLongClickListener.onCartSummaryItemLongClicked(getLayoutPosition());
            return true;
        }
    }

    private class OrderItemComparator implements Comparator<OrderItem> {

        @Override
        public int compare(OrderItem first, OrderItem second) {
            return first.getId().compareTo(second.getId());
        }
    }
}
