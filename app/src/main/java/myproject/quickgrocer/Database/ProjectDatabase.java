package myproject.quickgrocer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import myproject.quickgrocer.Constants;

import static myproject.quickgrocer.Constants.adminOrder_col_category;
import static myproject.quickgrocer.Constants.adminOrder_col_id;
import static myproject.quickgrocer.Constants.adminOrder_col_image;
import static myproject.quickgrocer.Constants.adminOrder_col_itemName;
import static myproject.quickgrocer.Constants.adminOrder_col_price;
import static myproject.quickgrocer.Constants.adminOrder_col_qty;
import static myproject.quickgrocer.Constants.adminOrder_col_sub_category;
import static myproject.quickgrocer.Constants.adminOrder_col_weight;
import static myproject.quickgrocer.Constants.adminOrder_tableName;
import static myproject.quickgrocer.Constants.adminOrder_userName;
import static myproject.quickgrocer.Constants.cart_col_category;
import static myproject.quickgrocer.Constants.cart_col_id;
import static myproject.quickgrocer.Constants.cart_col_image;
import static myproject.quickgrocer.Constants.cart_col_itemName;
import static myproject.quickgrocer.Constants.cart_col_price;
import static myproject.quickgrocer.Constants.cart_col_quan;
import static myproject.quickgrocer.Constants.cart_col_sub_category;
import static myproject.quickgrocer.Constants.cart_col_weight;
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
import static myproject.quickgrocer.Constants.order_custName;
import static myproject.quickgrocer.Constants.order_tableName;
import static myproject.quickgrocer.Constants.user_col_email;
import static myproject.quickgrocer.Constants.user_col_id;
import static myproject.quickgrocer.Constants.user_col_password;
import static myproject.quickgrocer.Constants.user_col_useraddress;
import static myproject.quickgrocer.Constants.user_col_username;
import static myproject.quickgrocer.Constants.user_col_userphoneno;
import static myproject.quickgrocer.Constants.user_tableName;

public class ProjectDatabase extends SQLiteOpenHelper {


    public ProjectDatabase(Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Constants.user_tableName + "(" +
                user_col_id + " integer primary key autoincrement, " +
                user_col_email + " text, " +
                user_col_username + " text, " +
                user_col_userphoneno + " text, " +
                user_col_useraddress + " text, " +
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
                cart_col_quan + " text)"
        );
        db.execSQL("create table " + order_tableName + "(" +
                cart_col_id + " integer primary key autoincrement, " +
                cart_col_itemName + " text, " +
                cart_col_category + " text, " +
                cart_col_sub_category + " text, " +
                cart_col_price + " text, " +
                cart_col_image + " text, " +
                cart_col_weight + " text, " +
                cart_col_quan + " text, " +
                order_custName + " text)"
        );
        db.execSQL("create table " + adminOrder_tableName + "(" +
                adminOrder_col_id + " integer primary key autoincrement, " +
                adminOrder_col_itemName + " text, " +
                adminOrder_col_category + " text, " +
                adminOrder_col_sub_category + " text, " +
                adminOrder_col_price + " text, " +
                adminOrder_col_image + " text, " +
                adminOrder_col_qty + " text, " +
                adminOrder_userName + " text, " +
                adminOrder_col_weight + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);
        Log.e("Database", "onUpgrade");

    }

    //    start Login or Register
    public long addUser(String email, String userName, String password, int phoneno, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(user_col_email, email);
        contentValues.put(user_col_username, userName);
        contentValues.put(user_col_password, password);
        contentValues.put(user_col_userphoneno, phoneno);
        contentValues.put(user_col_useraddress, address);
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

    public long addFood(String ItemName, String Category, String SubCategory, double Price, String weight, String image) {
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

    public long insertCart(String FoodName, String Category, String subCategory, double Price, String image, String weight, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(cart_col_itemName, FoodName);
        contentValues.put(cart_col_category, Category);
        contentValues.put(cart_col_sub_category, subCategory);
        contentValues.put(cart_col_price, Price);
        contentValues.put(cart_col_image, image);
        contentValues.put(cart_col_weight, weight);
        contentValues.put(cart_col_quan, qty);
        long res = db.insert(cart_tableName, null, contentValues);
        db.close();
        Log.e("Cart res", String.valueOf(res));
        return res;
    }

    public long confirmOrder(String FoodName, String Category, String subCategory, double Price,
                             String image, String weight, int qty, String CustName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(cart_col_itemName, FoodName);
        contentValues.put(cart_col_category, Category);
        contentValues.put(cart_col_sub_category, subCategory);
        contentValues.put(cart_col_price, Price);
        contentValues.put(cart_col_image, image);
        contentValues.put(cart_col_weight, weight);
        contentValues.put(cart_col_quan, qty);
        contentValues.put(order_custName, CustName);
//        contentValues.put(user_col_userphoneno, phoneNo);
//        contentValues.put(user_col_useraddress, Address);

        long res = db.insert(order_tableName, null, contentValues);
        db.close();
        Log.e("Cart res", String.valueOf(res));
        return res;
    }

    public long insertAdminOrder(String FoodName, String Category, String SubCategory,
                                 double Price, String image, int qty, String CustName, String Weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(adminOrder_col_itemName, FoodName);
        contentValues.put(adminOrder_col_category, Category);
        contentValues.put(adminOrder_col_sub_category, SubCategory);
        contentValues.put(adminOrder_col_price, Price);
        contentValues.put(adminOrder_col_image, image);
        contentValues.put(adminOrder_col_qty, qty);
        contentValues.put(adminOrder_userName, CustName);
//        contentValues.put(user_col_userphoneno, mobile);
//        contentValues.put(user_col_useraddress, address);
        contentValues.put(adminOrder_col_weight, Weight);

        long res = db.insert(adminOrder_tableName, null, contentValues);
        db.close();
        Log.e("Cart res", String.valueOf(res));
        return res;
    }
}

