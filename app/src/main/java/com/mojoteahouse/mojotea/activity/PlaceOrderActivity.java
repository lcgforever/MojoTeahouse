package com.mojoteahouse.mojotea.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.data.MojoData;
import com.mojoteahouse.mojotea.data.Order;
import com.mojoteahouse.mojotea.data.OrderItem;
import com.mojoteahouse.mojotea.data.Topping;
import com.mojoteahouse.mojotea.fragment.dialog.ConfirmOrderDialogFragment;
import com.mojoteahouse.mojotea.fragment.dialog.PlacingOrderDialogFragment;
import com.mojoteahouse.mojotea.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceOrderActivity extends BaseActivity implements View.OnClickListener,
        ConfirmOrderDialogFragment.PlaceOrderListener {

    public static final String NAME_SPLIT_SYMBOL = "   ";
    public static final String TOPPING_SPLIT_SYMBOL = ", ";

    private static final String EXTRA_MOJO_DATA = "EXTRA_MOJO_DATA";
    private static final String EXTRA_ORDER_ITEM_LIST = "EXTRA_ORDER_ITEM_LIST";
    private static final String EXTRA_ORDER = "EXTRA_ORDER";
    // Min deliver time is 30 mins
    private static final int MIN_DELIVER_TIME_MINS = 30;

    private CoordinatorLayout coordinatorLayout;
    private TextInputLayout nameTextLayout;
    private TextInputLayout addressTextLayout;
    private TextInputLayout phoneTextLayout;
    private EditText nameEditText;
    private EditText addressEditText;
    private EditText phoneEditText;
    private EditText noteEditText;
    private ImageButton clearNameButton;
    private ImageButton clearAddressButton;
    private ImageButton clearPhoneButton;
    private ImageButton clearNoteButton;
    private Button dateAndTimeButton;
    private TextView dateAndTimeErrorTextView;
    private Button placeOrderButton;

    private ConnectivityManager connectivityManager;
    private SharedPreferences sharedPreferences;
    private Calendar selectedDeliverTime;
    private SimpleDateFormat simpleDateFormat;
    private Pattern phoneNumberPattern;

    private MojoData mojoData;
    private ArrayList<OrderItem> orderItemList;
    private Order order;
    private Date deliverTime;
    private String customerName;
    private String customerAddress;
    private String customerZip;
    private String customerPhoneNumber;
    private String customerNote;
    private int totalQuantity = 0;

    public static void start(Activity activity, MojoData mojoData, ArrayList<OrderItem> orderItemList, Bundle optionsBundle) {
        Intent intent = new Intent(activity, PlaceOrderActivity.class);
        intent.putExtra(EXTRA_MOJO_DATA, mojoData);
        intent.putParcelableArrayListExtra(EXTRA_ORDER_ITEM_LIST, orderItemList);
        activity.startActivity(intent, optionsBundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.place_order_toolbar_title);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            mojoData = savedInstanceState.getParcelable(EXTRA_MOJO_DATA);
            orderItemList = savedInstanceState.getParcelableArrayList(EXTRA_ORDER_ITEM_LIST);
            order = savedInstanceState.getParcelable(EXTRA_ORDER);
        } else {
            mojoData = getIntent().getParcelableExtra(EXTRA_MOJO_DATA);
            orderItemList = getIntent().getParcelableArrayListExtra(EXTRA_ORDER_ITEM_LIST);
            order = null;
        }

        for (OrderItem orderItem : orderItemList) {
            totalQuantity += orderItem.getQuantity();
        }

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        phoneNumberPattern = Pattern.compile(getString(R.string.phone_number_regex));

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_container);
        nameTextLayout = (TextInputLayout) findViewById(R.id.name_edit_text_layout);
        addressTextLayout = (TextInputLayout) findViewById(R.id.address_edit_text_layout);
        phoneTextLayout = (TextInputLayout) findViewById(R.id.phone_edit_text_layout);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        addressEditText = (EditText) findViewById(R.id.address_edit_text);
        phoneEditText = (EditText) findViewById(R.id.phone_edit_text);
        noteEditText = (EditText) findViewById(R.id.note_edit_text);
        clearNameButton = (ImageButton) findViewById(R.id.name_clear_button);
        clearAddressButton = (ImageButton) findViewById(R.id.address_clear_button);
        clearPhoneButton = (ImageButton) findViewById(R.id.phone_clear_button);
        clearNoteButton = (ImageButton) findViewById(R.id.note_clear_button);
        dateAndTimeButton = (Button) findViewById(R.id.date_and_time_button);
        dateAndTimeErrorTextView = (TextView) findViewById(R.id.date_and_time_error_text);
        placeOrderButton = (Button) findViewById(R.id.bottom_action_button);
        Spinner zipSpinner = (Spinner) findViewById(R.id.zip_spinner);

        setupEditTexts();

        double totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();
        }
        placeOrderButton.setText(String.format(getString(R.string.place_order_button_text), totalPrice));

        customerZip = sharedPreferences.getString(getString(R.string.pref_customer_zip), mojoData.getAvailableZipList().get(0));
        final ArrayAdapter<String> zipAdapter = new ArrayAdapter<>(this, R.layout.layout_simple_spinner_item, mojoData.getAvailableZipList());
        zipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zipSpinner.setAdapter(zipAdapter);
        for (int i = 0; i < zipAdapter.getCount(); i++) {
            if (zipAdapter.getItem(i).equals(customerZip)) {
                zipSpinner.setSelection(i);
                break;
            }
        }
        zipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                customerZip = zipAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                customerZip = zipAdapter.getItem(0);
            }
        });

        clearNameButton.setOnClickListener(this);
        clearAddressButton.setOnClickListener(this);
        clearPhoneButton.setOnClickListener(this);
        clearNoteButton.setOnClickListener(this);
        dateAndTimeButton.setOnClickListener(this);
        placeOrderButton.setOnClickListener(this);

        selectedDeliverTime = Calendar.getInstance();
        selectedDeliverTime.add(Calendar.MINUTE, MIN_DELIVER_TIME_MINS);
        selectedDeliverTime.set(Calendar.SECOND, 59);
        simpleDateFormat = new SimpleDateFormat("EEE, MMM dd yyyy   h:mm a", Locale.US);
        updateDateAndTimeText();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_MOJO_DATA, mojoData);
        outState.putParcelableArrayList(EXTRA_ORDER_ITEM_LIST, orderItemList);
        outState.putParcelable(EXTRA_ORDER, order);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        placeOrderButton.setEnabled(!storeClosed && dateAndTimeErrorTextView.getVisibility() != View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    @Override
    void onStoreCloseStatusChanged() {
        super.onStoreCloseStatusChanged();
        placeOrderButton.setEnabled(!storeClosed && dateAndTimeErrorTextView.getVisibility() != View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.name_clear_button:
                nameEditText.setText("");
                nameTextLayout.setError(null);
                break;

            case R.id.address_clear_button:
                addressEditText.setText("");
                addressTextLayout.setError(null);
                break;

            case R.id.phone_clear_button:
                phoneEditText.setText("");
                phoneTextLayout.setError(null);
                break;

            case R.id.note_clear_button:
                noteEditText.setText("");
                break;

            case R.id.date_and_time_button:
                hideKeyboard();
                showDatePickerDialog();
                break;

            case R.id.bottom_action_button:
                hideKeyboard();
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                boolean isNetworkConnected = (activeNetworkInfo != null) && (activeNetworkInfo.isConnected());
                if (totalQuantity == 0) {
                    Toast.makeText(this, R.string.place_order_no_item_error_message, Toast.LENGTH_LONG).show();
                } else if (!isNetworkConnected) {
                    Toast.makeText(this, R.string.no_network_place_order_error_message, Toast.LENGTH_LONG).show();
                } else {
                    if (checkAllFields()) {
                        ConfirmOrderDialogFragment.show(getFragmentManager());
                    }
                }
                break;
        }
    }

    @Override
    public void onPlaceOrderConfirmed() {
        PlacingOrderDialogFragment.show(getFragmentManager());
        saveOrder();
    }

    private void setupEditTexts() {
        nameTextLayout.setErrorEnabled(true);
        addressTextLayout.setErrorEnabled(true);
        phoneTextLayout.setErrorEnabled(true);

        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    addressEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nameTextLayout.setError(null);
                clearNameButton.setVisibility(TextUtils.isEmpty(s)
                        ? View.GONE
                        : View.VISIBLE);
            }
        });

        addressEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    phoneEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                addressTextLayout.setError(null);
                clearAddressButton.setVisibility(TextUtils.isEmpty(s)
                        ? View.GONE
                        : View.VISIBLE);
            }
        });

        phoneEditText.addTextChangedListener(new HyphenTextChangedListener());

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

        nameEditText.setText(sharedPreferences.getString(getString(R.string.pref_customer_name), ""));
        addressEditText.setText(sharedPreferences.getString(getString(R.string.pref_customer_address), ""));
        phoneEditText.setText(sharedPreferences.getString(getString(R.string.pref_customer_phone), ""));
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
    }

    private boolean checkAllFields() {
        if (TextUtils.isEmpty(nameEditText.getText())) {
            nameTextLayout.setError(getString(R.string.name_error_message));
            return false;
        }
        Editable address = addressEditText.getText();
        if (TextUtils.isEmpty(address) || address.length() < 10) {
            addressTextLayout.setError(getString(R.string.address_error_message));
            return false;
        }
        Editable phone = phoneEditText.getText();
        if (TextUtils.isEmpty(phone) || !isPhoneNumberValid(phone.toString())) {
            phoneTextLayout.setError(getString(R.string.phone_error_message));
            return false;
        }

        return verifyDateAndTime(true);
    }

    private boolean isZipValid(String zipString) {
        if (zipString.length() != 5) {
            return false;
        }
        for (String availableZip : mojoData.getAvailableZipList()) {
            if (zipString.equalsIgnoreCase(availableZip)) {
                return true;
            }
        }
        return false;
    }

    private void showDatePickerDialog() {
        int year = selectedDeliverTime.get(Calendar.YEAR);
        int month = selectedDeliverTime.get(Calendar.MONTH);
        int day = selectedDeliverTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedDeliverTime.set(Calendar.YEAR, year);
                        selectedDeliverTime.set(Calendar.MONTH, monthOfYear);
                        selectedDeliverTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        showTimePickerDialog();
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        int currentHour = selectedDeliverTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = selectedDeliverTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedDeliverTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDeliverTime.set(Calendar.MINUTE, minute);
                        selectedDeliverTime.set(Calendar.SECOND, 59);
                        updateDateAndTimeText();
                        verifyDateAndTime(false);
                    }
                }, currentHour, currentMinute, false);

        timePickerDialog.show();
    }

    private void updateDateAndTimeText() {
        dateAndTimeButton.setText(simpleDateFormat.format(selectedDeliverTime.getTime()));
    }

    // Adjust offset is used
    private boolean verifyDateAndTime(boolean considerTimeOffset) {
        Calendar now = Calendar.getInstance();
        if (considerTimeOffset) {
            now.add(Calendar.MINUTE, MIN_DELIVER_TIME_MINS - 2);
        } else {
            now.add(Calendar.MINUTE, MIN_DELIVER_TIME_MINS);
        }
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_YEAR, 2);
        future.set(Calendar.HOUR_OF_DAY, 21);
        future.set(Calendar.MINUTE, 0);
        future.set(Calendar.SECOND, 0);
        int hour = selectedDeliverTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDeliverTime.get(Calendar.MINUTE);
        if (selectedDeliverTime.before(now)) {
            dateAndTimeErrorTextView.setText(R.string.deliver_too_soon_error_message);
            dateAndTimeErrorTextView.setVisibility(View.VISIBLE);
            placeOrderButton.setEnabled(false);
            return false;
        } else if (selectedDeliverTime.after(future)) {
            dateAndTimeErrorTextView.setText(R.string.deliver_date_error_message);
            dateAndTimeErrorTextView.setVisibility(View.VISIBLE);
            placeOrderButton.setEnabled(false);
            return false;
        } else if (hour < 12 || hour > 21 || (hour == 21 && minute > 0)) {
            dateAndTimeErrorTextView.setText(R.string.deliver_business_time_error_message);
            dateAndTimeErrorTextView.setVisibility(View.VISIBLE);
            placeOrderButton.setEnabled(false);
            return false;
        } else {
            dateAndTimeErrorTextView.setVisibility(View.GONE);
            placeOrderButton.setEnabled(true);
            return true;
        }
    }

    private void saveOrder() {
        saveCustomerDetails();
        if (order == null) {
            order = new Order();
            Date currentDate = new Date();
            order.setOrderTime(DateUtil.dateToString(currentDate));
            order.setDeliverByTime(DateUtil.dateToString(deliverTime));
            order.setCustomerName(customerName);
            order.setCustomerAddress(customerAddress);
            order.setCustomerZip(customerZip);
            order.setCustomerPhoneNumber(customerPhoneNumber);
            order.setCustomerNote(customerNote);
            order.setTotalQuantity(totalQuantity);
            double totalPrice = 0;
            List<String> completeOrderList = new ArrayList<>();
            StringBuilder stringBuilder;
            OrderItem orderItem;
            for (int i = 0; i < orderItemList.size(); i++) {
                orderItem = orderItemList.get(i);
                totalPrice += orderItem.getTotalPrice();
                stringBuilder = new StringBuilder();
                stringBuilder.append(orderItem.getMojoMenu().getName())
                        .append(NAME_SPLIT_SYMBOL);
                List<Topping> selectedToppingList = orderItem.getSelectedToppings();
                for (int j = 0; j < selectedToppingList.size(); j++) {
                    if (j > 0) {
                        stringBuilder.append(TOPPING_SPLIT_SYMBOL);
                    }
                    stringBuilder.append(selectedToppingList.get(j).getName());
                }
                completeOrderList.add(stringBuilder.toString());
            }
            order.setTotalPrice(totalPrice);
            order.setCompleteOrderList(completeOrderList);
            order.setOrderItemList(orderItemList);
        }

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkConnected = (activeNetworkInfo != null) && (activeNetworkInfo.isConnected());
        if (isNetworkConnected) {
            saveOrderToRemoteAndFinish();
        } else {
            PlacingOrderDialogFragment.dismiss(getFragmentManager());
            Snackbar.make(coordinatorLayout, R.string.no_network_place_order_error_message, Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean isPhoneNumberValid(String number) {
        Matcher matcher = phoneNumberPattern.matcher(number);
        return matcher.matches();
    }

    private void saveCustomerDetails() {
        deliverTime = selectedDeliverTime.getTime();
        customerName = (nameEditText.getText() == null ? "" : nameEditText.getText().toString());
        customerAddress = (addressEditText.getText() == null ? "" : addressEditText.getText().toString());
        customerPhoneNumber = (phoneEditText.getText() == null ? "" : phoneEditText.getText().toString());
        customerNote = (noteEditText.getText() == null ? "" : noteEditText.getText().toString());
        sharedPreferences.edit()
                .putString(getString(R.string.pref_customer_name), customerName)
                .putString(getString(R.string.pref_customer_address), customerAddress)
                .putString(getString(R.string.pref_customer_zip), customerZip)
                .putString(getString(R.string.pref_customer_phone), customerPhoneNumber)
                .apply();
    }

    private void saveOrderToRemoteAndFinish() {
        String userId = sharedPreferences.getString(getString(R.string.pref_user_id), "");
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("order").child(userId);
        DatabaseReference newOrderRef = orderRef.push();
        order.setId(newOrderRef.getKey());
        newOrderRef.setValue(order.getDataMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                PlacingOrderDialogFragment.dismiss(getFragmentManager());
                if (databaseError == null) {
                    PostOrderActivity.start(PlaceOrderActivity.this, customerName, order.getTotalPrice(), deliverTime.getTime());
                    supportFinishAfterTransition();
                } else {
                    Log.e("findme: ", "firebase error: " + databaseError.getMessage() + "    " + databaseError.getDetails());
                    Snackbar.make(coordinatorLayout, R.string.place_order_error_message, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


    private class HyphenTextChangedListener implements TextWatcher {

        private boolean isFormatting;
        private boolean deletingHyphen;
        private boolean deletingBackward;
        private int hyphenStart;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (isFormatting)
                return;

            // Make sure user is deleting one char, without a selection
            final int selStart = Selection.getSelectionStart(s);
            final int selEnd = Selection.getSelectionEnd(s);
            if (s.length() > 1                  // Can delete another character
                    && count == 1               // Deleting only one character
                    && after == 0               // Deleting
                    && s.charAt(start) == '-'   // a hyphen
                    && selStart == selEnd) {    // no selection
                deletingHyphen = true;
                hyphenStart = start;
                // Check if the user is deleting forward or backward
                deletingBackward = (selStart == start + 1);
            } else {
                deletingHyphen = false;
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable text) {
            phoneTextLayout.setError(null);
            clearPhoneButton.setVisibility(TextUtils.isEmpty(text)
                    ? View.GONE
                    : View.VISIBLE);

            if (isFormatting)
                return;

            isFormatting = true;

            // If deleting hyphen, also delete character before or after it
            if (deletingHyphen && hyphenStart > 0) {
                if (deletingBackward) {
                    if (hyphenStart - 1 < text.length()) {
                        text.delete(hyphenStart - 1, hyphenStart);
                    }
                } else if (hyphenStart < text.length()) {
                    text.delete(hyphenStart, hyphenStart + 1);
                }
            }
            if (text.length() == 3 || text.length() == 7) {
                text.append('-');
            }

            isFormatting = false;
        }
    }
}
