package com.mojoteahouse.mojotea.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.adapter.CartSummaryItemAdapter;
import com.mojoteahouse.mojotea.data.Order;
import com.mojoteahouse.mojotea.data.OrderItem;

public class ViewOrderActivity extends BaseActivity implements CartSummaryItemAdapter.CartSummaryItemClickListener,
        CartSummaryItemAdapter.CartSummaryItemLongClickListener {

    public static final String EXTRA_ORDER = "EXTRA_ORDER";

    private Toolbar toolbar;

    private CartSummaryItemAdapter itemAdapter;
    private Order order;

    public static void start(Activity activity, Order order, Bundle optionsBundle) {
        Intent intent = new Intent(activity, ViewOrderActivity.class);
        intent.putExtra(EXTRA_ORDER, order);
        activity.startActivity(intent, optionsBundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        if (savedInstanceState != null) {
            order = savedInstanceState.getParcelable(EXTRA_ORDER);
        } else {
            order = getIntent().getParcelableExtra(EXTRA_ORDER);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.view_order_toolbar_title);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView totalPriceText = (TextView) findViewById(R.id.total_price_text);
        totalPriceText.setText(String.format(getString(R.string.price_format), order.getTotalPrice()));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.order_item_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        itemAdapter = new CartSummaryItemAdapter(this, order.getOrderItemList(), this, this);
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_ORDER, order);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    @Override
    public void onCartSummaryItemClicked(int position) {
        OrderItem orderItem = itemAdapter.getOrderItemAtPosition(position);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, toolbar, getString(R.string.toolbar_transition));
        ViewOrderItemActivity.start(this, orderItem, optionsCompat.toBundle());
    }

    @Override
    public void onCartSummaryItemLongClicked(int position) {
        // Do nothing
    }
}
