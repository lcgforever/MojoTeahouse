package com.mojoteahouse.mojotea.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.adapter.OrderHistoryItemAdapter;
import com.mojoteahouse.mojotea.data.Order;
import com.mojoteahouse.mojotea.data.OrderItem;

import java.util.ArrayList;
import java.util.Map;

public class MojoOrderActivity extends BaseActivity implements OrderHistoryItemAdapter.OrderHistoryClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String EXTRA_ORDER_LIST = "EXTRA_ORDER_LIST";
    private static final String EXTRA_SAVED_ORDER_ITEM_LIST = "EXTRA_SAVED_ORDER_ITEM_LIST";

    private Toolbar toolbar;
    private DrawerLayout navigationDrawer;
    private ActionBarDrawerToggle navigationDrawerToggle;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView noOrderText;
    private OrderHistoryItemAdapter orderAdapter;

    private ArrayList<Order> orderList;
    private ArrayList<OrderItem> savedOrderItemList;

    public static void start(Activity activity, ArrayList<OrderItem> savedOrderItemList, Bundle optionsBundle) {
        Intent intent = new Intent(activity, MojoOrderActivity.class);
        intent.putExtra(EXTRA_SAVED_ORDER_ITEM_LIST, savedOrderItemList);
        activity.startActivity(intent, optionsBundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mojo_order);

        if (savedInstanceState != null) {
            orderList = savedInstanceState.getParcelableArrayList(EXTRA_ORDER_LIST);
            savedOrderItemList = savedInstanceState.getParcelableArrayList(EXTRA_SAVED_ORDER_ITEM_LIST);
        } else {
            orderList = new ArrayList<>();
            savedOrderItemList = getIntent().getParcelableArrayListExtra(EXTRA_SAVED_ORDER_ITEM_LIST);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(R.string.nav_orders_title);
        }

        navigationDrawer = (DrawerLayout) findViewById(R.id.mojo_order_drawer_layout);
        navigationDrawerToggle = new ActionBarDrawerToggle(this, navigationDrawer, toolbar,
                R.string.action_open_navigation_drawer, R.string.action_close_navigation_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (navigationView.getMenu().findItem(R.id.nav_menu).isChecked()) {
                    supportFinishAfterTransition();
                } else if (navigationView.getMenu().findItem(R.id.nav_settings).isChecked()) {
                    SettingsActivity.start(MojoOrderActivity.this);
                }
                navigationView.setCheckedItem(R.id.nav_orders);
            }
        };
        navigationDrawer.addDrawerListener(navigationDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.mojo_order_nav_view);
        navigationView.setCheckedItem(R.id.nav_orders);
        navigationView.setNavigationItemSelectedListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mojo_order_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(R.color.accent, R.color.red, R.color.green_background);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadOrderData();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.order_history_recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstPostion = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                swipeRefreshLayout.setEnabled(firstPostion == 0);
            }
        });
        orderAdapter = new OrderHistoryItemAdapter(this, new ArrayList<Order>(), this);
        recyclerView.setAdapter(orderAdapter);
        noOrderText = (TextView) findViewById(R.id.no_order_text);

        loadOrderData();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        navigationDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(EXTRA_ORDER_LIST, orderList);
        outState.putParcelableArrayList(EXTRA_SAVED_ORDER_ITEM_LIST, savedOrderItemList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navigationDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawer.isDrawerOpen(GravityCompat.START)) {
            navigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        navigationView.setCheckedItem(menuItem.getItemId());
        navigationDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onOrderHistoryClicked(Order order) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, toolbar, getString(R.string.toolbar_transition));
        ViewOrderActivity.start(this, order, optionsCompat.toBundle());
    }

    private void loadOrderData() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("order");
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipeRefreshLayout.setRefreshing(false);
                if (dataSnapshot != null && dataSnapshot.exists()) {
                    Map<String, Map<String, Object>> orderMap = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                    orderList.clear();
                    for (Map<String, Object> orderData : orderMap.values()) {
                        orderList.add(new Order(orderData));
                    }
                    if (orderList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        noOrderText.setVisibility(View.VISIBLE);
                    } else {
                        noOrderText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        orderAdapter.updateOrderList(orderList);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e("findme", "Load order data failed: " + databaseError.getMessage());
            }
        });
    }
}
