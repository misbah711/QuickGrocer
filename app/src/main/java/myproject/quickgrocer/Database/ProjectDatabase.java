package myproject.quickgrocer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import myproject.quickgrocer.Constants;

import static myproject.quickgrocer.Constants.cart_col_category;
import static myproject.quickgrocer.Constants.cart_col_id;
import static myproject.quickgrocer.Constants.cart_col_image;
import static myproject.quickgrocer.Constants.cart_col_itemName;
import static myproject.quickgrocer.Constants.cart_col_price;
import static myproject.quickgrocer.Constants.cart_col_quan;
import static myproject.quickgrocer.Constants.cart_col_sub_category;
import static myproject.quickgrocer.Constants.cart_col_weight;
import static myproject.quickgrocer.Constants.cart_sub_total;
import static myproject.quickgrocer.Constants.cart_tableName;
import static myproject.quickgrocer.Constants.databaseName;
import static myproject.quickgrocer.Constants.item_col_category;
import static myproject.quickgrocer.Constants.item_col_id;
import static myproject.quickgrocer.Constants.item_col_image;
import static myproject.quickgrocer.Constants.item_col_itemName;
import static myproject.quickgrocer.Constants.item_col_price;
import static myproject.quickgrocer.Constants.item_col_sub_category;
import static myproject.quickgrocer.Constants.item_col_weight;
import static myproject.quickgrocer.Constants.item_tableName;
import static myproject.quickgrocer.Constants.user_col_email;
import static myproject.quickgrocer.Constants.user_col_fullName;
import static myproject.quickgrocer.Constants.user_col_id;
import static myproject.quickgrocer.Constants.user_col_password;
import static myproject.quickgrocer.Constants.user_col_username;
import static myproject.quickgrocer.Constants.user_tableName;

public class ProjectDatabase extends SQLiteOpenHelper {


    public ProjectDatabase(Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Constants.user_tableName + "(" +
                user_col_id + " integer primary key autoincrement, " +
                user_col_fullName + " text, " +
                user_col_email + " text, " +
                user_col_username + " text, " +
                user_col_password + " text)"
        );
        db.execSQL("create table " + item_tableName + "(" +
                item_col_id + " integer primary key autoincrement, " +
                item_col_itemName + " text, " +
                item_col_category + " text, " +
                item_col_sub_category + " text, " +
                item_col_price + " text, " +
                item_col_weight + " text, " +
                item_col_image + " text)"
        );
        db.execSQL("create table " + cart_tableName + "(" +
                cart_col_id + " integer primary key autoincrement, " +
                cart_col_itemName + " text, " +
                cart_col_category + " text, " +
                cart_col_sub_category + " text, " +
                cart_col_price + " text, " +
                cart_col_image + " text, " +
                cart_col_weight + " text, " +
                cart_col_quan + " text, " +
                cart_sub_total + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);
        Log.e("Database", "onUpgrade");

    }

    //    start Login or Register
    public long addUser(String fullName, String email, String userName, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(user_col_fullName, fullName);
        contentValues.put(user_col_email, email);
        contentValues.put(user_col_username, userName);
        contentValues.put(user_col_password, password);
        long res = db.insert(user_tableName, null, contentValues);
        db.close();
        Log.e("addUser res", String.valueOf(res));
        return res;
    }

    public boolean checkUser(String user, String pass) {
        String[] column = {user_col_id};
        SQLiteDatabase db = getReadableDatabase();
        String selection = user_col_username + "=?" + " and " + user_col_password + "=?";
        String[] args = {user, pass};
        Cursor cursor = db.query(user_tableName, column, selection, args, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        Log.e("checkUser cursor", String.valueOf(cursor));

        if (count > 0) {
            return true;
        } else
            return false;
    }

    public long addFood(String ItemName, String Category, String SubCategory, double Price, double weight, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(item_col_itemName, ItemName);
        contentValues.put(item_col_category, Category);
        contentValues.put(item_col_sub_category, SubCategory);
        contentValues.put(item_col_price, Price);
        contentValues.put(item_col_weight, weight);
        contentValues.put(item_col_image, image);
        long res = db.insert(item_tableName, null, contentValues);
        db.close();
        Log.e("Food res", String.valueOf(res));
        return res;
    }

    public long insertCart(String FoodName, String Category, String subCategory, double Price, String image, double weight, int qty, double subTotal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(cart_col_itemName, FoodName);
        contentValues.put(cart_col_category, Category);
        contentValues.put(cart_col_sub_category, subCategory);
        contentValues.put(cart_col_price, Price);
        contentValues.put(cart_col_image, image);
        contentValues.put(cart_col_weight, weight);
        contentValues.put(cart_col_quan, qty);
        contentValues.put(cart_sub_total, subTotal);
        long res = db.insert(cart_tableName, null, contentValues);
        db.close();
        Log.e("Cart res", String.valueOf(res));
        return res;
    }


}
