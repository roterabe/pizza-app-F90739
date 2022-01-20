package com.example.pizza;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Dialog fragment.
 */
public class CustomizePizzaDialog extends DialogFragment implements View.OnClickListener {

    private int quantity = 1;
    private TextView quantityTextView;
    private TextView subtotalTextView;
    private View v;
    private CartDatabase cartDB;
    private Pizza piz;
    private int price, basePrice;
    private String name, description;

    private CheckBox corn, mushrooms, tomatoSauce, cheese, olives;

    private List<CheckBox> extraTopping = new ArrayList<>();
    private Map<String, Boolean> additives = new HashMap<>();
    private Map<String, Integer> extraType = new HashMap<>();

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        cartDB = new CartDatabase(getContext());
        v = inflater.inflate(R.layout.activity_dialog_fragment, container, false);
        corn = v.findViewById(R.id.corn);
        mushrooms = v.findViewById(R.id.mushrooms);
        tomatoSauce = v.findViewById(R.id.tomato_sauce);
        cheese = v.findViewById(R.id.cheese);
        olives = v.findViewById(R.id.olives);
        quantityTextView = v.findViewById(R.id.quantity_text_view);

        if (savedInstanceState != null) {
            price = savedInstanceState.getInt("price");
            quantity = savedInstanceState.getInt("quantity");
            basePrice = savedInstanceState.getInt("basePrice");
            name = savedInstanceState.getString("pizzaName");
            description = savedInstanceState.getString("description");
            corn.setChecked(savedInstanceState.getBoolean("corn"));
            mushrooms.setChecked(savedInstanceState.getBoolean("mushrooms"));
            tomatoSauce.setChecked(savedInstanceState.getBoolean("tomatoSauce"));
            cheese.setChecked(savedInstanceState.getBoolean("cheese"));
            olives.setChecked(savedInstanceState.getBoolean("olives"));
        }

        quantityTextView = v.findViewById(R.id.quantity_text_view);
        subtotalTextView = v.findViewById(R.id.subtotal_text_view);
        extraType.put("Zero", 0);
        display(quantity);
        subtotalTextView.setText("subtotal: " + calculatePrice("Zero", true));

        /**
         * Add additive pricing.
         */
        extraType.put("Corn", 1);
        extraType.put("Mushrooms", 1);
        extraType.put("Tomato sauce", 2);
        extraType.put("Cheese", 4);
        extraType.put("Olives", 3);

        v.findViewById(R.id.add_to_cart).setOnClickListener(this);
        v.findViewById(R.id.cancel).setOnClickListener(this);
        v.findViewById(R.id.minus).setOnClickListener(this);
        v.findViewById(R.id.plus).setOnClickListener(this);
        corn.setOnClickListener(this);
        mushrooms.setOnClickListener(this);
        tomatoSauce.setOnClickListener(this);
        cheese.setOnClickListener(this);
        olives.setOnClickListener(this);

        return v;
    }

    /**
    * Handle on click event for all buttons in dialog fragment.
     */
    public void onClick(View view) {
        int newPrice = 0;
        switch (view.getId()) {
            case R.id.add_to_cart:
                addToCart();
                //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                dismiss();
                break;
            case R.id.cancel:
                //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                dismiss();
                break;
            case R.id.minus:
                decrement();
                newPrice = calculatePrice("Zero", true);
                displaySubtotal(newPrice);
                break;
            case R.id.plus:
                increment();
                newPrice = calculatePrice("Zero", true);
                displaySubtotal(newPrice);
                break;
            case R.id.corn:
                CheckBox corn = v.findViewById(R.id.corn);
                newPrice = calculatePrice((String) corn.getText(), corn.isChecked());
                displaySubtotal(newPrice);
                break;
            case R.id.mushrooms:
                CheckBox mushrooms = v.findViewById(R.id.mushrooms);
                newPrice = calculatePrice((String) mushrooms.getText(), mushrooms.isChecked());
                displaySubtotal(newPrice);
                break;
            case R.id.tomato_sauce:
                CheckBox tomatoSauce = v.findViewById(R.id.tomato_sauce);
                newPrice = calculatePrice((String) tomatoSauce.getText(), tomatoSauce.isChecked());
                displaySubtotal(newPrice);
                break;
            case R.id.cheese:
                CheckBox cheese = v.findViewById(R.id.cheese);
                newPrice = calculatePrice((String) cheese.getText(), cheese.isChecked());
                displaySubtotal(newPrice);
                break;
            case R.id.olives:
                CheckBox olives = v.findViewById(R.id.olives);
                newPrice = calculatePrice((String) olives.getText(), olives.isChecked());
                displaySubtotal(newPrice);
                break;
        }
    }

    /**
    * Set pizza object for gathering variable data.
     */
    public void setCurrPizza(Pizza piz) {
        this.piz = piz;
        this.name = piz.getName();
        this.price = piz.getPrice();
        this.basePrice = this.price;
        this.description = piz.getDescription();
    }

    /**
     * This method is called when the "Add to cart" button is clicked.
     */
    public void addToCart() {
        int newPrice = 0;
        for (int i = 0; i < extraTopping.size(); i++) {
            additives.put((String) extraTopping.get(i).getText().toString(), extraTopping.get(i).isChecked());
        }

        display(quantity);
        newPrice = calculatePrice("Zero", true);
        displaySubtotal(newPrice);
        if (cartDB.getPizzaQuantity(name) > 0)
            cartDB.updatePizza(name, newPrice, quantity);
        else
            cartDB.addNewPizza(name, description, price, quantity);
        Toast.makeText(getContext(), name + " added to cart", Toast.LENGTH_SHORT).show();
    }

    /**
    * Calculate subtotal price.
     */
    private int calculatePrice(String text, boolean checked) {

        int currPrice = 0;
        System.out.println(checked);

        if (!checked) {
            price -= extraType.get(text);
        } else {
            price += extraType.get(text);
        }
        currPrice = price;
        currPrice = quantity * currPrice;

        return currPrice;
    }

    public void decrement() {
        if (this.quantity > 1) {
            this.quantity -= 1;
            display(quantity);
        } else {
            Toast.makeText(getContext(), "You can't order less than a single pizza.", Toast.LENGTH_SHORT).show();
        }
    }

    public void increment() {
        if (quantity >= 101) {
            Toast.makeText(getContext(), "If you wish to order more than 100 pizzas, please give us a call.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            quantity += 1;
            display(quantity);
        }
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void display(int quantity) {

        quantityTextView.setText(" "    + quantity);
    }

    private void displaySubtotal(int price) {
        subtotalTextView.setText("subtotal: " + price);
    }

    /**
    * We use the following in order to stretch the dialog fragment.
     */
    @Override
    public void onResume() {
        super.onResume();

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /**
    * This method saves the state of valuable data.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("pizzaName", name);
        savedInstanceState.putInt("price", price);
        savedInstanceState.putInt("quantity", quantity);
        savedInstanceState.putString("description", description);
        savedInstanceState.putInt("basePrice", basePrice);
        savedInstanceState.putBoolean("corn", corn.isChecked());
        savedInstanceState.putBoolean("tomatoSauce", tomatoSauce.isChecked());
        savedInstanceState.putBoolean("mushrooms", mushrooms.isChecked());
        savedInstanceState.putBoolean("cheese", cheese.isChecked());
        savedInstanceState.putBoolean("olives", olives.isChecked());
        super.onSaveInstanceState(savedInstanceState);
    }


}

