package com.mojoteahouse.mojotea.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.mojoteahouse.mojotea.MojoTeaApp;
import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.data.MojoMenu;
import com.mojoteahouse.mojotea.data.OrderItem;
import com.mojoteahouse.mojotea.data.Topping;
import com.mojoteahouse.mojotea.util.DataUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditMojoItemActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_ORDER_ITEM = "EXTRA_ORDER_ITEM";
    private static final String EXTRA_MOJO_MENU = "EXTRA_MOJO_MENU";
    private static final String EXTRA_ALL_TOPPING_LIST = "EXTRA_ALL_TOPPING_LIST";
    private static final String EXTRA_SELECTED_TOPPINGS = "EXTRA_SELECTED_TOPPINGS";
    private static final String TOPPING_SPLIT_SYMBOL = ",";

    private Button addToCartButton;
    private EditText noteEditText;

    private MojoMenu mojoMenu;
    private ArrayList<Topping> allToppingsList;
    private ArrayList<Topping> selectedToppings;
    private int quantity;
    private double totalPrice;
    private double toppingPrice;

    public static void startForResult(Activity activity, MojoMenu mojoMenu, ArrayList<Topping> toppingList) {
        Intent intent = new Intent(activity, EditMojoItemActivity.class);
        intent.putExtra(EXTRA_MOJO_MENU, mojoMenu);
        intent.putExtra(EXTRA_ALL_TOPPING_LIST, toppingList);
        activity.startActivityForResult(intent, MojoTeaApp.EDIT_MOJO_MENU_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            mojoMenu = savedInstanceState.getParcelable(EXTRA_MOJO_MENU);
            allToppingsList = savedInstanceState.getParcelableArrayList(EXTRA_ALL_TOPPING_LIST);
            selectedToppings = savedInstanceState.getParcelableArrayList(EXTRA_SELECTED_TOPPINGS);
        } else {
            mojoMenu = getIntent().getParcelableExtra(EXTRA_MOJO_MENU);
            allToppingsList = getIntent().getParcelableArrayListExtra(EXTRA_ALL_TOPPING_LIST);
            selectedToppings = new ArrayList<>();
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_layout);
        collapsingToolbarLayout.setTitle(mojoMenu.getName());

        quantity = 1;
        toppingPrice = 0;
        setupViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_MOJO_MENU, mojoMenu);
        outState.putParcelableArrayList(EXTRA_ALL_TOPPING_LIST, allToppingsList);
        outState.putParcelableArrayList(EXTRA_SELECTED_TOPPINGS, selectedToppings);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_mojo_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelAndFinish();
                break;

            case R.id.action_add:
                saveOrderItemAndFinish();
                break;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        cancelAndFinish();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_action_button:
                saveOrderItemAndFinish();
                break;

            case R.id.note_clear_button:
                noteEditText.setText("");
                break;
        }
    }

    private void setupViews() {
        ImageView mojoItemImageView = (ImageView) findViewById(R.id.mojo_item_image);
        TextView mojoItemNameTextView = (TextView) findViewById(R.id.mojo_item_name_text);
        TextView noToppingTextView = (TextView) findViewById(R.id.no_topping_text);
        Spinner quantitySpinner = (Spinner) findViewById(R.id.quantity_spinner);
        final ViewSwitcher quantityViewSwitcher = (ViewSwitcher) findViewById(R.id.quantity_view_switcher);
        final EditText quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        final ImageButton clearNoteButton = (ImageButton) findViewById(R.id.note_clear_button);
        addToCartButton = (Button) findViewById(R.id.edit_action_button);
        noteEditText = (EditText) findViewById(R.id.note_edit_text);

        addToCartButton.setOnClickListener(this);
        clearNoteButton.setOnClickListener(this);

        String imageUri = mojoMenu.getImageUrl();
        if (TextUtils.isEmpty(imageUri)) {
            Picasso.with(this)
                    .load(R.drawable.ic_no_image)
                    .into(mojoItemImageView);
        } else {
            Picasso.with(this)
                    .load(imageUri)
                    .error(R.drawable.ic_no_image)
                    .into(mojoItemImageView);
        }
        String name = mojoMenu.getName();
        String chineseName = mojoMenu.getChineseName();
        if (TextUtils.isEmpty(chineseName)) {
            mojoItemNameTextView.setText(name);
        } else {
            mojoItemNameTextView.setText(String.format(getString(R.string.mojo_item_name_format), name, chineseName));
        }
        updatePriceAndText();

        String availableToppingIds = mojoMenu.getToppingIds();
        if (TextUtils.isEmpty(availableToppingIds)) {
            noToppingTextView.setVisibility(View.VISIBLE);
        } else {
            List<Topping> availableToppingsList = new ArrayList<>();
            for (String toppingId : availableToppingIds.split(TOPPING_SPLIT_SYMBOL)) {
                availableToppingsList.add(DataUtil.getToppingById(allToppingsList, toppingId));
            }
            if (availableToppingsList.isEmpty()) {
                noToppingTextView.setVisibility(View.VISIBLE);
            } else {
                addToppings(availableToppingsList);
            }
        }

        quantityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    if (TextUtils.isEmpty(quantityEditText.getText())) {
                        quantity = 1;
                        quantityEditText.setText(R.string.quantity_initial_value);
                    } else {
                        quantity = Integer.parseInt(quantityEditText.getText().toString());
                    }
                    if (quantity < 1) {
                        quantity = 1;
                    }
                    updatePriceAndText();
                }
                return false;
            }
        });
        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    quantity = 1;
                } else {
                    quantity = Integer.parseInt(s.toString());
                }
                if (quantity < 1) {
                    quantity = 1;
                }
                updatePriceAndText();
            }
        });

        quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (getString(R.string.quantity_customize_text).equals(selectedItem)) {
                    quantityViewSwitcher.showNext();
                    quantityEditText.setText("");
                    quantityEditText.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(quantityEditText, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    quantity = Integer.parseInt(selectedItem);
                    updatePriceAndText();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        noteEditText.clearFocus();
        noteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                clearNoteButton.setVisibility(TextUtils.isEmpty(s)
                        ? View.GONE
                        : View.VISIBLE);
            }
        });
    }

    private void addToppings(List<Topping> toppingList) {
        Collections.sort(toppingList);
        LinearLayout toppingContainer = (LinearLayout) findViewById(R.id.topping_container);
        toppingContainer.removeAllViews();
        for (final Topping topping : toppingList) {
            final CheckedTextView checkedTextView = (CheckedTextView) getLayoutInflater().inflate(
                    R.layout.topping_list_item, toppingContainer, false);
            checkedTextView.setText(String.format(getString(R.string.topping_format),
                    topping.getName(), topping.getChineseName(), topping.getPrice()));
            if (topping.isSoldOut()) {
                checkedTextView.setEnabled(false);
            } else {
                checkedTextView.setEnabled(true);
                checkedTextView.setTag(topping);
                checkedTextView.setChecked(selectedToppings.contains(topping));
                checkedTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkedTextView.toggle();
                        Topping relatedTopping = (Topping) checkedTextView.getTag();
                        if (checkedTextView.isChecked()) {
                            selectedToppings.add(relatedTopping);
                            updateSelectedToppingPrice(relatedTopping.getPrice());
                        } else {
                            selectedToppings.remove(relatedTopping);
                            updateSelectedToppingPrice(-relatedTopping.getPrice());
                        }
                    }
                });
            }
            toppingContainer.addView(checkedTextView);
        }
    }

    private void updateSelectedToppingPrice(double toppingPrice) {
        this.toppingPrice += toppingPrice;
        if (this.toppingPrice < 0) {
            this.toppingPrice = 0;
        }
        updatePriceAndText();
    }

    private void updatePriceAndText() {
        totalPrice = quantity * (mojoMenu.getPrice() + toppingPrice);
        addToCartButton.setText(String.format(getString(R.string.add_to_cart_button_text), totalPrice));
    }

    private void saveOrderItemAndFinish() {
        Collections.sort(selectedToppings);

        OrderItem orderItem = new OrderItem();
        String orderItemId = String.valueOf(System.currentTimeMillis());
        orderItem.setId(orderItemId);
        orderItem.setMojoMenu(mojoMenu);
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(totalPrice);
        orderItem.setSelectedToppings(selectedToppings);
        orderItem.setSelectedToppingPrice(toppingPrice);
        orderItem.setNote(noteEditText.getText() == null ? "" : noteEditText.getText().toString());

        Intent data = new Intent();
        data.putExtra(EXTRA_ORDER_ITEM, orderItem);
        setResult(RESULT_OK, data);
        supportFinishAfterTransition();
    }

    private void cancelAndFinish() {
        setResult(RESULT_CANCELED);
        supportFinishAfterTransition();
    }
}
