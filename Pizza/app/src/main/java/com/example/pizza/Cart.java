package com.example.pizza;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * This app displays an order form to order pizza.
 */
public class Cart extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    TextView quantityTextView;
    TextView textViewDate, textViewTime;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Handler mainThreadHandler;
    private CartDatabase pizzaDB;
    private CartAdapter adapter;
    private ListView pizzaView;
    private final Calendar cldr = Calendar.getInstance();
    private AlertDialog.Builder builder;

    private Map<String, Integer> extraType = new HashMap<>();
    private List<PizzaDTO> orderID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        textViewDate = findViewById(R.id.text_view_date);
        textViewTime = findViewById(R.id.text_view_time);
        pizzaDB = new CartDatabase(Cart.this);
        pizzaView = findViewById(R.id.order_view);
        builder = new AlertDialog.Builder(Cart.this);

        /**
         * Add additive pricing.
         */
        extraType.put("Corn", 1);
        extraType.put("Mushrooms", 1);
        extraType.put("Tomato sauce", 2);
        extraType.put("Cheese", 4);
        extraType.put("Olives", 3);

        findViewById(R.id.button_time).setOnClickListener(this);
        findViewById(R.id.button_date).setOnClickListener(this);

        builder.setMessage("Are you sure you want to order??")
                .setTitle("Confirm")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        List<Integer> total;
                        total = pizzaDB.getTotal();
                        int totalPrice = 0;
                        for (int i : total) {
                            totalPrice += i;
                        }
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"example@gmail.com"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Pizza order");
                        intent.putExtra(Intent.EXTRA_TEXT, "Hello,\n you have ordered pizzas from \"The Pizza Place\" for a total of " +
                                totalPrice + "BGN.\nYou will receive your order by " +
                                textViewDate.getText().toString() + " " + textViewTime.getText().toString() +
                                " o'clock.\nHave a good day!\nRegards,\n\"The Pizza Place\"");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            pizzaDB.clearDb();
                            startActivity(intent);
                        }
                        finish();
                    }
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        makeSpinner();
        Executor executor = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());
        executor.execute(getPizzasFromDB);

    }

    private void makeSpinner() {
        Spinner spinner = findViewById(R.id.city_spinner0);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.city_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
    * This method handles choosing which time dialog to open. e.g. Date or Time.
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_date:
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(Cart.this,
                        (view1, year1, monthOfYear, dayOfMonth) -> {

                            textViewDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                        }, year, month, day);
                datePickerDialog.show();
                break;
            case R.id.button_time:
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minute = cldr.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(Cart.this,
                        (view12, hour1, minute1) -> textViewTime.setText(hour1 + ":" + minute1), hour, minute, true);
                timePickerDialog.show();
                break;
        }
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This method is used to go back to the previous activity.
     */
    public void onBack(View view) {
        finish();
    }

    /**
    * Unused NumberFormat for the bulgarian currency.
     */
    private NumberFormat bgCurrency() {
        NumberFormat df = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("BGN");
        dfs.setGroupingSeparator('.');
        dfs.setMonetaryDecimalSeparator(',');
        ((DecimalFormat) df).setDecimalFormatSymbols(dfs);
        return df;
    }

    private final Runnable getPizzasFromDB = new Runnable() {
        @Override
        public void run() {
            orderID = pizzaDB.getPizzaData();
            mainThreadHandler.post(displayOrders);
        }
    };
    private final Runnable displayOrders = new Runnable() {
        @Override
        public void run() {

            adapter = new CartAdapter(
                    Cart.this, orderID);
            pizzaView.setAdapter(adapter);
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}