package com.mojoteahouse.mojotea.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mojoteahouse.mojotea.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PostOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_CUSTOMER_NAME = "EXTRA_CUSTOMER_NAME";
    private static final String EXTRA_TOTAL_PRICE = "EXTRA_TOTAL_PRICE";
    private static final String EXTRA_DELIVER_TIME = "EXTRA_DELIVER_TIME";

    private String customerName;
    private double totalPrice;
    private long deliverTime;

    public static void start(Context context, String customerName, double totalPrice, long deliverTime) {
        Intent intent = new Intent(context, PostOrderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_CUSTOMER_NAME, customerName);
        intent.putExtra(EXTRA_TOTAL_PRICE, totalPrice);
        intent.putExtra(EXTRA_DELIVER_TIME, deliverTime);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_order);

        if (savedInstanceState != null) {
            customerName = savedInstanceState.getString(EXTRA_CUSTOMER_NAME);
            totalPrice = savedInstanceState.getDouble(EXTRA_TOTAL_PRICE);
            deliverTime = savedInstanceState.getLong(EXTRA_DELIVER_TIME);
        } else {
            customerName = getIntent().getStringExtra(EXTRA_CUSTOMER_NAME);
            totalPrice = getIntent().getDoubleExtra(EXTRA_TOTAL_PRICE, 0);
            deliverTime = getIntent().getLongExtra(EXTRA_DELIVER_TIME, 0);
        }

        TextView nameTextView = (TextView) findViewById(R.id.post_order_name_text_view);
        TextView priceTextView = (TextView) findViewById(R.id.post_order_price_text_view);
        TextView deliverTimeTextView = (TextView) findViewById(R.id.post_order_deliver_time_text_view);
        Button contactButton = (Button) findViewById(R.id.post_order_contact_button);
        ImageButton closeButton = (ImageButton) findViewById(R.id.post_order_close_button);

        nameTextView.setText(String.format(getString(R.string.customer_name_text), customerName));
        priceTextView.setText(String.format(getString(R.string.order_price_format), totalPrice));
        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 23);
        todayCal.set(Calendar.MINUTE, 59);
        todayCal.set(Calendar.SECOND, 59);
        todayCal.set(Calendar.MILLISECOND, 0);
        Calendar deliverTimeCal = Calendar.getInstance();
        deliverTimeCal.setTimeInMillis(deliverTime);
        if (deliverTimeCal.before(todayCal)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            deliverTimeTextView.setText(String.format(getString(R.string.order_deliver_time_today_format),
                    simpleDateFormat.format(deliverTimeCal.getTime())));
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd h:mm a", Locale.getDefault());
            deliverTimeTextView.setText(String.format(getString(R.string.order_deliver_time_format),
                    simpleDateFormat.format(deliverTimeCal.getTime())));
        }

        contactButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_CUSTOMER_NAME, customerName);
        outState.putDouble(EXTRA_TOTAL_PRICE, totalPrice);
        outState.putLong(EXTRA_DELIVER_TIME, deliverTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        finishAndShowMainPage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_order_contact_button:
                AboutMojoActivity.start(this, null);
                break;

            case R.id.post_order_close_button:
                finishAndShowMainPage();
                break;
        }
    }

    private void finishAndShowMainPage() {
        MojoMenuActivity.start(this);
        supportFinishAfterTransition();
    }
}
