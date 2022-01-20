package com.example.pizza;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class PizzaDTO {
    private String name, description;
    private int price, quantity;

    public PizzaDTO(String name, String description, int price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() { return quantity; }


}

