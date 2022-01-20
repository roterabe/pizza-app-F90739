package com.example.pizza;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CartDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "cartDB";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "myCart";

    private static final String ID_COL = "id";

    private static final String NAME_COL = "pizzaName";

    private static final String DESCRIPTION_COL = "pizzaDescription";

    private static final String PRICE_COL = "pizzaPrice";

    private static final String QUANTITY_COL = "quantity";

    public CartDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT UNIQUE,"
                + DESCRIPTION_COL + " TEXT,"
                + QUANTITY_COL + " INTEGER,"
                + PRICE_COL + " INTEGER)";

        db.execSQL(query);
    }

    /**
     * Adding in a new pizza to the cart.
     */
    public void addNewPizza(String name, String description, int price, int quantity) {

        SQLiteDatabase db = this.getWritableDatabase();

        String pizzaName = name;
        String pizzaDescription = description;
        int pizzaPrice = price;
        int pizzaQuantity = quantity;

        ContentValues values = new ContentValues();

        values.put(NAME_COL, pizzaName);
        values.put(DESCRIPTION_COL, pizzaDescription);
        values.put(QUANTITY_COL, pizzaQuantity);
        values.put(PRICE_COL, pizzaPrice);

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    public void updatePizza(String name, int price, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        System.out.println(name);
        v.put(QUANTITY_COL, quantity);
        v.put(PRICE_COL, price);
        db.update(TABLE_NAME, v, "pizzaName=?", new String[]{name});
        db.close();
    }

    public int getPizzaQuantity(String pizzaName) {
        int qty = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cp = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cp.moveToFirst()) {
            do {
                if (cp.getString(1).equals(pizzaName))
                    qty = cp.getInt(3);
            } while (cp.moveToNext());
        }
        return qty;
    }

    public List<PizzaDTO> getPizzaData() {
        List<PizzaDTO> orderID = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cp = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cp.moveToFirst()) {
            do {

                orderID.add(new PizzaDTO(cp.getString(1),
                        cp.getString(2),
                        cp.getInt(4),
                        cp.getInt(3)));
            } while (cp.moveToNext());

        }

        cp.close();

        return orderID;
    }

    public void clearDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public List<Integer> getTotal() {
        List<Integer> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cp = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cp.moveToFirst()) {
            do {
                data.add(cp.getInt(4));
            } while (cp.moveToNext());
        }
        return data;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
