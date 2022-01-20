package com.example.pizza;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class Pizza {
    private String name, description;
    private int price;

    public Pizza(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
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

    public void setPrice(int price) { this.price = price; }


}
