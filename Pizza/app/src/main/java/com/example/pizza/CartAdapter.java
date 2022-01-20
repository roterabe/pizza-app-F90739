package com.example.pizza;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends ArrayAdapter<PizzaDTO>{
    public CartAdapter(Context context, List<PizzaDTO> pizza) {
        super(context, 0, pizza);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_items_cart, parent, false);
        }

        PizzaDTO currentPizza = getItem(position);
        String name = currentPizza.getName();
        String description = currentPizza.getDescription();
        int price = currentPizza.getPrice();
        int quantity = currentPizza.getQuantity();

        TextView nameView = listItemView.findViewById(R.id.name);
        nameView.setText("Name: " + name);

        Button remove = listItemView.findViewById(R.id.remove);


        TextView descriptionView = listItemView.findViewById(R.id.description);
        descriptionView.setText("Description: " + description);

        TextView priceView = listItemView.findViewById(R.id.price);
        priceView.setText("Price: " + price);

        TextView qtyView = listItemView.findViewById(R.id.quantity);
        qtyView.setText("Quantity: " + quantity);


        return listItemView;
    }
}
