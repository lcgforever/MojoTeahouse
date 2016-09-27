package com.mojoteahouse.mojotea.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.data.MojoMenu;
import com.mojoteahouse.mojotea.data.OrderItem;
import com.mojoteahouse.mojotea.data.Topping;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class ViewOrderItemActivity extends BaseActivity {

    private static final String EXTRA_ORDER_ITEM = "EXTRA_ORDER_ITEM";

    private OrderItem orderItem;

    public static void start(Activity activity, OrderItem orderItem, Bundle optionsBundle) {
        Intent intent = new Intent(activity, ViewOrderItemActivity.class);
        intent.putExtra(EXTRA_ORDER_ITEM, orderItem);
        activity.startActivity(intent, optionsBundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_item);

        if (savedInstanceState != null) {
            orderItem = savedInstanceState.getParcelable(EXTRA_ORDER_ITEM);
        } else {
            orderItem = getIntent().getParcelableExtra(EXTRA_ORDER_ITEM);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        ImageView orderItemImageView = (ImageView) findViewById(R.id.mojo_item_image);
        TextView orderItemNameTextView = (TextView) findViewById(R.id.mojo_item_name_text);
        TextView quantityText = (TextView) findViewById(R.id.quantity_text);
        TextView noteText = (TextView) findViewById(R.id.note_text);
        TextView noToppingTextView = (TextView) findViewById(R.id.no_topping_text);
        LinearLayout noteLayout = (LinearLayout) findViewById(R.id.note_layout);
        CollapsingToolbarLayout collapsingToolbarLayout
                = (CollapsingToolbarLayout) findViewById(R.id.view_order_item_collapse_layout);

        MojoMenu mojoMenu = orderItem.getMojoMenu();
        String imageUri = mojoMenu.getImageUrl();
        if (TextUtils.isEmpty(imageUri)) {
            Picasso.with(this)
                    .load(R.drawable.ic_no_image)
                    .into(orderItemImageView);
        } else {
            Picasso.with(this)
                    .load(imageUri)
                    .error(R.drawable.ic_no_image)
                    .into(orderItemImageView);
        }

        String name = mojoMenu.getName();
        String chineseName = mojoMenu.getChineseName();
        collapsingToolbarLayout.setTitle(name);
        if (TextUtils.isEmpty(chineseName)) {
            orderItemNameTextView.setText(name);
        } else {
            orderItemNameTextView.setText(String.format(getString(R.string.mojo_item_name_format), name, chineseName));
        }

        List<Topping> selectedToppings = orderItem.getSelectedToppings();
        if (selectedToppings.isEmpty()) {
            noToppingTextView.setVisibility(View.VISIBLE);
        } else {
            addToppings(selectedToppings);
        }

        quantityText.setText(String.valueOf(orderItem.getQuantity()));
        if (!TextUtils.isEmpty(orderItem.getNote())) {
            noteText.setText(orderItem.getNote());
            noteLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_ORDER_ITEM, orderItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    private void addToppings(List<Topping> toppingList) {
        Collections.sort(toppingList);
        LinearLayout toppingContainer = (LinearLayout) findViewById(R.id.topping_container);
        toppingContainer.removeAllViews();
        for (final Topping topping : toppingList) {
            final TextView textView = (TextView) getLayoutInflater().inflate(
                    R.layout.topping_non_checkable_list_item, toppingContainer, false);
            textView.setText(String.format(getString(R.string.topping_format),
                    topping.getName(), topping.getChineseName(), topping.getPrice()));
            toppingContainer.addView(textView);
        }
    }
}
