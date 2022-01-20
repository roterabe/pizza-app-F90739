package com.example.pizza;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PizzaMain extends AppCompatActivity {

    private ListView pizzaView;
    private String pizzaUrl = "https://raw.githubusercontent.com/roterabe/roterabe/master/pizzas.json";
    private Handler mainThreadHandler;
    private final String TAG = PizzaMain.class.getSimpleName();
    private PizzaAdapter adapter;
    private CustomizePizzaDialog dia;
    List<Pizza> pizzas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_main);
        getSupportActionBar();

        pizzaView = findViewById(R.id.pizza_view);
        Executor executor = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());
        executor.execute(getPizzas);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                Intent intent = new Intent(PizzaMain.this, Cart.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private final Runnable getPizzas = new Runnable() {
        @Override
        public void run() {

            try {

                HttpHandler sh = new HttpHandler();

                String url = pizzaUrl;
                String jsonStr = sh.makeServiceCall(url);

                Log.e(TAG, "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);


                        JSONArray pizzaType = jsonObj.getJSONArray("Pizzas");


                        JSONObject pizza;
                        String name;
                        String description;
                        int price;


                        for (int i = 0; i < pizzaType.length(); i++) {
                            pizza = pizzaType.getJSONObject(i);
                            name = pizza.getString("name");
                            description = pizza.getString("description");
                            price = pizza.getInt("price");

                            Pizza examplePizza = new Pizza(name, description, price);

                            pizzas.add(examplePizza);
                        }
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());

                    }

                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG).show());
                }

                mainThreadHandler.post(displayPizzas);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /**
    * This method is for filling up the listview and handling clicks.
     */
    private final Runnable displayPizzas = new Runnable() {
        @Override
        public void run() {

            try {

                adapter = new PizzaAdapter(
                        PizzaMain.this, pizzas);
                pizzaView.setAdapter(adapter);

                pizzaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Pizza currentPizza = adapter.getItem(position);
                        String name = currentPizza.getName();

                        String itemName = String.valueOf(name);
                        dia = new CustomizePizzaDialog();
                        dia.setCurrPizza(currentPizza);
                        dia.show(getSupportFragmentManager(), "CustomizePizzaDialog");

                        Toast.makeText(PizzaMain.this, itemName, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}