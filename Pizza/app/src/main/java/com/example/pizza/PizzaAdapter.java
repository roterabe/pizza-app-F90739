package com.example.pizza;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PizzaAdapter extends ArrayAdapter<Pizza> {

    public PizzaAdapter(Context context, List<Pizza> pizza) {
        super(context, 0, pizza);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_items, parent, false);
        }

        Pizza currentPizza = getItem(position);
        String name = currentPizza.getName();
        String description = currentPizza.getDescription();
        int price = currentPizza.getPrice();

        TextView nameView = listItemView.findViewById(R.id.name);
        nameView.setText("Name: " + name);

        TextView sauceView = listItemView.findViewById(R.id.description);
        sauceView.setText("Description: " + description);

        TextView meatView = listItemView.findViewById(R.id.price);
        meatView.setText("Price: " + price);


        return listItemView;
    }

}
