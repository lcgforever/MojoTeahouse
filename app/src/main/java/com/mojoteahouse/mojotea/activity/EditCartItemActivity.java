package com.mojoteahouse.mojotea.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.mojoteahouse.mojotea.fragment.dialog.DeleteCartItemDialogFragment;
import com.mojoteahouse.mojotea.util.DataUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditCartItemActivity extends AppCompatActivity implements View.OnClickListener,
        DeleteCartItemDialogFragment.DeleteCartItemListener {

    public static final String EXTRA_SHOULD_DELETE = "EXTRA_SHOULD_DELETE";
    public static final String EXTRA_ORDER_ITEM = "EXTRA_ORDER_ITEM";
    private static final String EXTRA_ALL_TOPPINGS_LIST = "EXTRA_ALL_TOPPINGS_LIST";
    private static final String TOPPING_SPLIT_SYMBOL = ",";
    private static final int SPINNER_MAX_VALUE = 5;

    private Button editDoneButton;
    private EditText noteEditText;

    private OrderItem orderItem;
    private ArrayList<Topping> allToppingsList;
    private int quantity;
    private double totalPrice;
    private double mojoItemPrice;
    private double selectedToppingPrice;

    public static void startForResult(Activity activity, OrderItem orderItem, ArrayList<Topping> allToppingsList) {
        Intent intent = new Intent(activity, EditCartItemActivity.class);
        intent.putExtra(EXTRA_ORDER_ITEM, orderItem);
        intent.putExtra(EXTRA_ALL_TOPPINGS_LIST, allToppingsList);
        activity.startActivityForResult(intent, MojoTeaApp.EDIT_CART_ITEM_REQUEST_CODE);
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
            orderItem = savedInstanceState.getParcelable(EXTRA_ORDER_ITEM);
            allToppingsList = savedInstanceState.getParcelableArrayList(EXTRA_ALL_TOPPINGS_LIST);
        } else {
            orderItem = getIntent().getParcelableExtra(EXTRA_ORDER_ITEM);
            allToppingsList = getIntent().getParcelableArrayListExtra(EXTRA_ALL_TOPPINGS_LIST);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_layout);
        collapsingToolbarLayout.setTitle(orderItem.getMojoMenu().getName());

        quantity = orderItem.getQuantity();
        totalPrice = orderItem.getTotalPrice();
        mojoItemPrice = orderItem.getMojoMenu().getPrice();
        selectedToppingPrice = orderItem.getSelectedToppingPrice();
        setupViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_ORDER_ITEM, orderItem);
        outState.putParcelableArrayList(EXTRA_ALL_TOPPINGS_LIST, allToppingsList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_summary_action_mode_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelAndFinish();
                break;

            case R.id.action_delete:
                DeleteCartItemDialogFragment.show(getFragmentManager());
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

    @Override
    public void onDeleteConfirmed() {
        Intent data = new Intent();
        data.putExtra(EXTRA_SHOULD_DELETE, true);
        data.putExtra(EXTRA_ORDER_ITEM, orderItem);
        setResult(RESULT_OK, data);
        supportFinishAfterTransition();
    }

    private void setupViews() {
        ImageView orderItemImageView = (ImageView) findViewById(R.id.mojo_item_image);
        TextView orderItemNameTextView = (TextView) findViewById(R.id.mojo_item_name_text);
        Spinner quantitySpinner = (Spinner) findViewById(R.id.quantity_spinner);
        TextView noToppingTextView = (TextView) findViewById(R.id.no_topping_text);
        final ViewSwitcher quantityViewSwitcher = (ViewSwitcher) findViewById(R.id.quantity_view_switcher);
        final EditText quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        final ImageButton clearNoteButton = (ImageButton) findViewById(R.id.note_clear_button);
        editDoneButton = (Button) findViewById(R.id.edit_action_button);
        noteEditText = (EditText) findViewById(R.id.note_edit_text);

        editDoneButton.setOnClickListener(this);
        clearNoteButton.setOnClickListener(this);

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
        if (TextUtils.isEmpty(chineseName)) {
            orderItemNameTextView.setText(name);
        } else {
            orderItemNameTextView.setText(String.format(getString(R.string.mojo_item_name_format), name, chineseName));
        }
        mojoItemPrice = mojoMenu.getPrice();
        selectedToppingPrice = orderItem.getSelectedToppingPrice();
        updatePriceAndText();
        noteEditText.setText(orderItem.getNote());

        List<String> availableToppingIds = mojoMenu.getToppingIds();
        if (availableToppingIds.isEmpty()) {
            noToppingTextView.setVisibility(View.VISIBLE);
        } else {
            List<Topping> availableToppingList = new ArrayList<>();
            for (String toppingId : availableToppingIds) {
                availableToppingList.add(DataUtil.getToppingById(allToppingsList, toppingId));
            }
            if (availableToppingList.isEmpty()) {
                noToppingTextView.setVisibility(View.VISIBLE);
            } else {
                addToppings(availableToppingList);
            }
        }

        if (quantity > SPINNER_MAX_VALUE) {
            quantityEditText.setText(String.valueOf(quantity));
            quantityViewSwitcher.showNext();
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
                    return true;
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

        quantitySpinner.setSelection(quantity > SPINNER_MAX_VALUE ? 0 : quantity - 1);
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
        final List<Topping> selectedToppings = orderItem.getSelectedToppings();
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
        this.selectedToppingPrice += toppingPrice;
        if (this.selectedToppingPrice < 0) {
            this.selectedToppingPrice = 0;
        }
        updatePriceAndText();
    }

    private void updatePriceAndText() {
        totalPrice = quantity * (mojoItemPrice + selectedToppingPrice);
        editDoneButton.setText(String.format(getString(R.string.edit_done_button_text), totalPrice));
    }

    private void saveOrderItemAndFinish() {
        Collections.sort(orderItem.getSelectedToppings());
        orderItem.setTotalPrice(totalPrice);
        orderItem.setSelectedToppingPrice(selectedToppingPrice);
        orderItem.setQuantity(quantity);
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
