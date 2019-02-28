package com.saveethataskdoor.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.saveethataskdoor.app.model.Cart;
import com.saveethataskdoor.app.model.Product;

import java.util.ArrayList;

/*
 * Created by OCSPL-83 on 3/1/2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static String sqLiteDatabase_PATH = "";
    public static final String DATABASE_NAME = "SAMPLE_DATABASE";
    private static SQLiteDatabase sqLiteDatabase;

    //CART TABLE KEYS
    private static final String TABLE_CART_SAMPLE = "TABLE_CART_SAMPLE";
    private static final String TABLE_CART = "TABLE_CART";

    public static final String CART_ROW_ID = "CART_ROW_ID";
    public static final String CUSTOMER_EMAIL = "CUSTOMER_EMAIL";
    public static final String CART_PRODUCT_ID = "CART_PRODUCT_ID";
    public static final String CART_PRODUCT_IMAGE = "CART_PRODUCT_IMAGE";
    public static final String CART_PRODUCT_NAME = "CART_PRODUCT_NAME";
    public static final String CART_PRODUCT_PRICE = "CART_PRODUCT_PRICE";
    public static final String CART_PRODUCT_QUANTITY = "CART_PRODUCT_QUANTITY";
    public static final String CART_PRODUCT_WEIGHT = "CART_PRODUCT_WEIGHT";
    public static final String CART_PRODUCT_SPECIAL_PRICE = "CART_PRODUCT_SPECIAL_PRICE";
    public static final String CART_PRODUCT_TOTAL = "CART_PRODUCT_TOTAL";
    public static final String CART_QUANTITY_POSITION = "CART_QUANTITY_POSITION";
    public static final String PRODUCT_OPTIONS_ID = "PRODUCT_OPTIONS_ID";
    public static final String PRODUCT_OPTIONS_VALUE_ID = "PRODUCT_OPTIONS_VALUE_ID";
    public static final String PRODUCT_IS_OPTIONS = "PRODUCT_IS_OPTIONS";
    public static final String IS_PRICE_CHANGED = "IS_PRICE_CHANGED";

    //MULTI OPTIONS KEYS
    private static final String MULTI_OPTIONS_TABLE_CART = "MULTI_OPTIONS_TABLE_CART";
    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String PRODUCT_NAME = "PRODUCT_NAME";
    public static final String PRODUCT_MULTI_OPTION_TYPE_NAME = "PRODUCT_MULTI_OPTION_TYPE_NAME";
    public static final String PRODUCT_OPTION_NAME = "PRODUCT_OPTION_NAME";
    public static final String PRODUCT_MULTI_OPTIONS_ID = "PRODUCT_OPTIONS_ID";
    public static final String PRODUCT_MULTI_OPTIONS_VALUE_ID = "PRODUCT_MULTI_OPTIONS_VALUE_ID";
    public static final String PRODUCT_OPTION_PRICE = "PRODUCT_OPTION_PRICE";


    public static final String cart_id = "cart_id";
    public static final String pro_id = "pro_id";
    public static final String pro_name = "pro_name";
    public static final String pro_category = "pro_category";
    public static final String pro_subcat = "pro_subcat";
    public static final String pro_price = "pro_price";
    public static final String pro_stock = "pro_stock";
    public static final String pro_weight = "pro_weight";
    public static final String pro_image = "pro_image";

    public static final String storeId = "storeId";
    public static final String storeName = "storeName";

    public static final String pro_description = "pro_description";
    public static final String pro_offer = "pro_offer";
    public static final String pro_status = "pro_status";


    //Default Constructor
    //Murali
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase_PATH = context.getFilesDir().getParentFile().getPath()
                + "/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CART_TABLE_SAMPLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_SAMPLE);
            sqLiteDatabase.execSQL(CREATE_CART_TABLE_SAMPLE);
        }
    }

    private static final String CREATE_CART_TABLE_SAMPLE
            = "CREATE TABLE " + TABLE_CART_SAMPLE + "("
            + cart_id + " INTEGER PRIMARY KEY,"
            + pro_id + " VARCHAR ,"
            + pro_name + " VARCHAR ,"
            + pro_price + " VARCHAR ,"
            + storeId + " VARCHAR ,"
            + storeName + " VARCHAR ,"
            + CART_PRODUCT_QUANTITY + " VARCHAR " + ")";

    private static void openDataBase() throws SQLException {
        try {
            String myPath = sqLiteDatabase_PATH + DATABASE_NAME;
            sqLiteDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int Cart_Count() {
        int count = 0;
        openDataBase();
        try {
            String query = "select count(*) as count from TABLE_CART";
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(cursor.getColumnIndex("count"));
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sqLiteDatabase.close();
        return count;
    }

    public void insertCart(Product product, int quantity) {
        openDataBase();


        ContentValues values = new ContentValues();

        values.put(pro_id, product.getId());
        values.put(pro_name, product.getName());
        values.put(pro_price, product.getAmount());
        values.put(storeId, product.getStoreId());
        values.put(storeName, product.getStorename());
        values.put(CART_PRODUCT_QUANTITY, quantity);

        sqLiteDatabase.insert(TABLE_CART_SAMPLE, null, values);

        sqLiteDatabase.close();
    }

    public boolean updateQuantity(String id, String quantity) {
        openDataBase();

        String query = "UPDATE " + TABLE_CART_SAMPLE + " SET " + CART_PRODUCT_QUANTITY + " = " + quantity + " WHERE " + pro_id + " = " + id;
        sqLiteDatabase.execSQL(query);

        sqLiteDatabase.close();

        return true;
    }

    public boolean updateCartQuantity(String id, String quantity, String stock) {
        openDataBase();

        String query = "UPDATE " + TABLE_CART_SAMPLE + " SET " + CART_PRODUCT_QUANTITY + " = " + quantity + " WHERE " + DataBaseHandler.pro_id + " = " + id;
        sqLiteDatabase.execSQL(query);

        sqLiteDatabase.close();

        return true;
    }

    public boolean removeProduct(String product_id) {
        openDataBase();
        sqLiteDatabase.execSQL("DELETE FROM TABLE_CART_SAMPLE WHERE pro_id = '" + product_id + "'");
        sqLiteDatabase.close();
        return true;
    }

    public int productCount(String ProductId) {
        int count = 0;
        openDataBase();
        try {
            String query = "SELECT " + CART_PRODUCT_QUANTITY + " as count FROM " + TABLE_CART_SAMPLE
                    + " WHERE " + pro_id + "='" + ProductId + "'";
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(cursor.getColumnIndex("count"));
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sqLiteDatabase.close();
        return count;
    }

    public ArrayList<Cart> getCartDetails() {
        ArrayList<Cart> cartList = new ArrayList<>();

        openDataBase();
        String selectQuery = "SELECT * FROM " + TABLE_CART_SAMPLE;
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Cart cart = new Cart();

                    cart.setCartId(cursor.getLong(0));
                    cart.setId(cursor.getLong(1));
                    cart.setName(cursor.getString(2));
                    cart.setAmount(cursor.getInt(3));
                    cart.setStoreId(cursor.getLong(4));
                    cart.setStorename(cursor.getString(5));
                    cart.setQuantity(cursor.getLong(6));
                    cartList.add(cart);

                } while (cursor.moveToNext());
            }
        }
        sqLiteDatabase.close();
        return cartList;
    }


    public int getCartCount() {
        int count = 0;
        openDataBase();
        try {
            String query = "select count(*) as count from " + TABLE_CART_SAMPLE;
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(cursor.getColumnIndex("count"));
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sqLiteDatabase.close();
        return count;
    }

    public boolean removeFromCart(String cart_id) {
        openDataBase();
        sqLiteDatabase.execSQL("DELETE FROM TABLE_CART_SAMPLE WHERE cart_id = '" + cart_id + "'");
        sqLiteDatabase.close();
        return true;

    }

    public boolean deleteCart() {
        openDataBase();
        sqLiteDatabase.execSQL("DELETE FROM TABLE_CART_SAMPLE");
        sqLiteDatabase.close();
        return true;
    }

    public int getTotal() {
        int total = 0;
        openDataBase();
        try {
            String query = "select sum(CART_PRODUCT_QUANTITY * pro_price) as total from " + TABLE_CART_SAMPLE;
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                total = cursor.getInt(cursor.getColumnIndex("total"));
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sqLiteDatabase.close();
        return total;
    }
}


