package com.mojoteahouse.mojotea.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mojoteahouse.mojotea.MojoTeaApp;
import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.adapter.CartSummaryItemAdapter;
import com.mojoteahouse.mojotea.data.MojoData;
import com.mojoteahouse.mojotea.data.OrderItem;
import com.mojoteahouse.mojotea.fragment.dialog.ClosedNowDialogFragment;
import com.mojoteahouse.mojotea.fragment.dialog.DeleteCartItemDialogFragment;

import java.util.ArrayList;
import java.util.Iterator;

public class CartActivity extends BaseActivity implements CartSummaryItemAdapter.CartSummaryItemClickListener,
        CartSummaryItemAdapter.CartSummaryItemLongClickListener, View.OnClickListener, ActionMode.Callback,
        DeleteCartItemDialogFragment.DeleteCartItemListener {

    private static final String EXTRA_MOJO_DATA = "EXTRA_MOJO_DATA";
    public static final String EXTRA_ORDER_ITEM_LIST = "EXTRA_ORDER_ITEM_LIST";
    private static final String EXTRA_SELECTED_POSITIONS = "EXTRA_SELECTED_POSITIONS";

    private Toolbar toolbar;
    private TextView totalPriceText;
    private Button checkOutButton;

    private CartSummaryItemAdapter cartItemAdapter;
    private ActionMode actionMode;
    private MojoData mojoData;
    private ArrayList<OrderItem> orderItemList;
    private ArrayList<OrderItem> selectedOrderItems;

    public static void startForResult(Activity activity,
                                      MojoData mojoData,
                                      ArrayList<OrderItem> orderItemList,
                                      Bundle optionsBundle) {
        Intent intent = new Intent(activity, CartActivity.class);
        intent.putExtra(EXTRA_MOJO_DATA, mojoData);
        intent.putExtra(EXTRA_ORDER_ITEM_LIST, orderItemList);
        activity.startActivityForResult(intent, MojoTeaApp.SHOW_CART_REQUEST_CODE, optionsBundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.cart_toolbar_title);
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            mojoData = savedInstanceState.getParcelable(EXTRA_MOJO_DATA);
            orderItemList = savedInstanceState.getParcelableArrayList(EXTRA_ORDER_ITEM_LIST);
        } else {
            mojoData = getIntent().getParcelableExtra(EXTRA_MOJO_DATA);
            orderItemList = getIntent().getParcelableArrayListExtra(EXTRA_ORDER_ITEM_LIST);
        }

        totalPriceText = (TextView) findViewById(R.id.total_price_text);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.summary_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        cartItemAdapter = new CartSummaryItemAdapter(this, orderItemList, this, this);
        recyclerView.setAdapter(cartItemAdapter);

        checkOutButton = (Button) findViewById(R.id.bottom_action_button);
        checkOutButton.setOnClickListener(this);

        updatePriceText();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_MOJO_DATA, mojoData);
        outState.putParcelableArrayList(EXTRA_ORDER_ITEM_LIST, orderItemList);
        SparseBooleanArray selectedItemPositions = cartItemAdapter.getSelectedItemPositions();
        ArrayList<Integer> positionList = new ArrayList<>();
        for (int i = 0; i < selectedItemPositions.size(); i++) {
            positionList.add(selectedItemPositions.keyAt(i));
        }
        outState.putIntegerArrayList(EXTRA_SELECTED_POSITIONS, positionList);
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Integer> positionList = (ArrayList<Integer>) savedInstanceState.get(EXTRA_SELECTED_POSITIONS);
        if (positionList != null && !positionList.isEmpty()) {
            actionMode = startSupportActionMode(this);
            for (Integer position : positionList) {
                cartItemAdapter.setSelectionAtPosition(position, true);
            }
            actionMode.setTitle(String.format(getString(R.string.action_mode_title_format), cartItemAdapter.getSelectedCount()));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(EXTRA_ORDER_ITEM_LIST, orderItemList);
        setResult(RESULT_CANCELED, data);
        supportFinishAfterTransition();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(EXTRA_ORDER_ITEM_LIST, orderItemList);
        setResult(RESULT_CANCELED, data);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MojoTeaApp.EDIT_CART_ITEM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                boolean shouldDelete = data.getBooleanExtra(EditCartItemActivity.EXTRA_SHOULD_DELETE, false);
                OrderItem orderItem = data.getParcelableExtra(EditCartItemActivity.EXTRA_ORDER_ITEM);
                updateOrderItem(orderItem, shouldDelete);
                updatePriceText();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_action_button:
                if (actionMode != null) {
                    actionMode.finish();
                }
                if (storeClosed) {
                    ClosedNowDialogFragment.show(getFragmentManager());
                } else {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this, toolbar, getString(R.string.toolbar_transition));
                    PlaceOrderActivity.start(this, mojoData, orderItemList, optionsCompat.toBundle());
                }
                break;
        }
    }

    @Override
    void onStoreCloseStatusChanged() {
        super.onStoreCloseStatusChanged();
        checkOutButton.setEnabled(!storeClosed);
    }

    @Override
    public void onCartSummaryItemClicked(int position) {
        if (actionMode == null) {
            OrderItem orderItem = cartItemAdapter.getOrderItemAtPosition(position);
            EditCartItemActivity.startForResult(this, orderItem, mojoData.getToppingList());
        } else {
            onListItemSelected(position);
        }
    }

    @Override
    public void onCartSummaryItemLongClicked(int position) {
        onListItemSelected(position);
    }

    @Override
    public void onDeleteConfirmed() {
        deleteSelectedOrderItems();
        actionMode.finish();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.cart_summary_action_mode_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                selectedOrderItems = new ArrayList<>();
                SparseBooleanArray selectedPositions = cartItemAdapter.getSelectedItemPositions();
                for (int i = 0; i < selectedPositions.size(); i++) {
                    selectedOrderItems.add(cartItemAdapter.getOrderItemAtPosition(selectedPositions.keyAt(i)));
                }

                DeleteCartItemDialogFragment.showWithMessage(getFragmentManager(),
                        getString(R.string.delete_selected_items_dialog_message));
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        cartItemAdapter.clearSelection();
        actionMode = null;
    }

    private void onListItemSelected(int position) {
        cartItemAdapter.setSelectionAtPosition(position, !cartItemAdapter.isSelectedAtPosition(position));
        boolean hasCheckedItems = cartItemAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && actionMode == null) {
            actionMode = startSupportActionMode(this);
        } else if (!hasCheckedItems && actionMode != null) {
            actionMode.finish();
        }

        if (actionMode != null) {
            actionMode.setTitle(String.format(getString(R.string.action_mode_title_format), cartItemAdapter.getSelectedCount()));
        }
    }

    private void updateOrderItem(OrderItem orderItem, boolean shouldDelete) {
        if (shouldDelete) {
            Iterator<OrderItem> itemIterator = orderItemList.iterator();
            while (itemIterator.hasNext()) {
                OrderItem item = itemIterator.next();
                if (item.getId().equals(orderItem.getId())) {
                    itemIterator.remove();
                    break;
                }
            }
        } else {
            for (int i = 0; i < orderItemList.size(); i++) {
                if (orderItemList.get(i).getId().equals(orderItem.getId())) {
                    orderItemList.set(i, orderItem);
                    break;
                }
            }
        }

        if (orderItemList.isEmpty()) {
            Intent data = new Intent();
            data.putParcelableArrayListExtra(EXTRA_ORDER_ITEM_LIST, orderItemList);
            setResult(RESULT_CANCELED, data);
            supportFinishAfterTransition();
        } else {
            cartItemAdapter.updateOrderItemList(orderItemList);
        }
    }

    private void updatePriceText() {
        double totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();
        }
        totalPriceText.setText(String.format(getString(R.string.price_format), totalPrice));
    }

    private void deleteSelectedOrderItems() {
        orderItemList.removeAll(selectedOrderItems);
        if (orderItemList.isEmpty()) {
            Intent data = new Intent();
            data.putParcelableArrayListExtra(EXTRA_ORDER_ITEM_LIST, orderItemList);
            setResult(RESULT_CANCELED, data);
            supportFinishAfterTransition();
        } else {
            cartItemAdapter.updateOrderItemList(orderItemList);
            updatePriceText();
        }
    }
}
