package myproject.quickgrocer.User;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.util.ArrayList;
import java.util.List;

import myproject.quickgrocer.Constants;
import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.Models.Cart;
import myproject.quickgrocer.R;

public class Checkout extends AppCompatActivity {
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    ArrayList<Cart> cartList;
    Cart cart;
    TextView totalPrice;
    Button checkout;
    int SubTotal, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        recyclerView = findViewById(R.id.recyvlerView);
        projectDatabase = new ProjectDatabase(Checkout.this);
        cartList = new ArrayList<>();


        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cartAdapter = new CartAdapter(Checkout.this, readcartList());
        recyclerView.setAdapter(cartAdapter);

        totalPrice = findViewById(R.id.totalPrice);
        checkout = findViewById(R.id.confirm);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Checkout.this);
                builder.setTitle("Confirm Order");
                builder.setMessage("Click OK to confirm Order");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Checkout.this, UserDashboardActivity.class));
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Checkout.this, UserDashboardActivity.class));
                        //Truncate the cart Data
                    }
                });
                builder.show();

            }
        });
    }

    public void setAdapter() {
        cartList.clear();
        cartAdapter = new CartAdapter(Checkout.this, readcartList());
        recyclerView.setAdapter(cartAdapter);
    }

    private ArrayList<Cart> readcartList() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select id, Name, Category, SubCategory, Price, Image, " +
                "Weight, Quantity  From Cart", new String[]{});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                final String Name = cursor.getString(1);
                String Category = cursor.getString(2);
                String subCategory = cursor.getString(3);
                double Price = cursor.getDouble(4);
                String Image = cursor.getString(5);
                double Weight = cursor.getDouble(6);
                int Quantity = cursor.getInt(7);
                Log.e("Cart List", Image + "--" + Name + "- " + Price);

                cart = new Cart();
                cart.setId(id);
                cart.setName(Name);
                cart.setCategory(Category);
                cart.setSubCategory(subCategory);
                cart.setPrice(Price);
                cart.setQty(Quantity);
                cart.setWeight(Weight);
                cart.setImg(Image);
                cartList.add(cart);
//                Log.e("List", String.valueOf(cartList));

            } while (cursor.moveToNext());
        }

        cursor.close();
        // db.close();

        return cartList;
    }

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        Context context;
        private List<Cart> cartList;
        private int id, qty;
        double price, weight;
        String name, category, subCategory, imageItem;

        public CartAdapter(Context context, ArrayList<Cart> cartList) {
            this.context = context;
            this.cartList = cartList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_checkout_list, parent, false);

            return new CartAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            holder.itemView.requestLayout();
            id = cartList.get(position).getId();
            name = cartList.get(position).getName();
            category = cartList.get(position).getCategory();
            subCategory = cartList.get(position).getSubCategory();
            price = cartList.get(position).getPrice();
            weight = cartList.get(position).getWeight();
            qty = cartList.get(position).getQty();
            imageItem = cartList.get(position).getImg();
            Log.e("Cart id", id + "-" + name + price);

            holder.itemName.setText(name);
            holder.itemCategory.setText(category);
            holder.itemPrice.setText(String.valueOf(price));
            holder.itemSubCategory.setText(subCategory);
            holder.weight.setText(String.valueOf(weight));
            Log.e("cartQty", String.valueOf(qty));
            //holder.itemQtyText.setText(String.valueOf(qty));

            if (imageItem.length() > 0) {
                String uri = "@drawable/" + imageItem;
                Log.e("image", uri);
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                holder.icon.setImageDrawable(res);
            } else {
                holder.icon.setImageResource(R.mipmap.ic_launcher);

            }

           /* holder.pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qty++;
                    cart.setQty(qty);
                }
            });
            holder.neg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qty--;
                    cart.setQty(qty);
                }
            });*/
            // updateCart();
            db = projectDatabase.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Constants.cart_col_id, id);
            values.put(Constants.cart_col_itemName, name);
            values.put(Constants.cart_col_category, category);
            values.put(Constants.cart_col_sub_category, subCategory);
            values.put(Constants.cart_col_price, price);
            values.put(Constants.cart_col_image, imageItem);
            values.put(Constants.cart_col_quan, qty);
            values.put(Constants.cart_sub_total, SubTotal);

            db.update(Constants.cart_tableName, values, "id = ?", new String[]{String.valueOf(id)});
            db.close();

            calculatePrice(id);
            calculateTotal();
            setAdapter();


            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db = projectDatabase.getWritableDatabase();
                    db.delete("Cart", "id = ?", new String[]{String.valueOf(id)});
                    setAdapter();

                }
            });
        }


        @Override
        public int getItemCount() {
            return cartList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView icon, delete;
            TextView itemName, itemPrice, itemCategory, itemSubCategory, weight;
            ElegantNumberButton itemQtyText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.itemImage);
                itemName = itemView.findViewById(R.id.itemname);
                itemPrice = itemView.findViewById(R.id.iprice);
                itemCategory = itemView.findViewById(R.id.icategory);
                itemSubCategory = itemView.findViewById(R.id.subcategory);
                delete = itemView.findViewById(R.id.delitem);
                itemQtyText = itemView.findViewById(R.id.number_button);

            }
        }

        private void calculateTotal() {
            db = projectDatabase.getReadableDatabase();
            Cursor cursorTotal = db.rawQuery("SELECT SUM(" + Constants.cart_sub_total + ") as Total FROM " + Constants.cart_tableName, null);
            if (cursorTotal.moveToFirst()) {
                int total = cursorTotal.getInt(cursorTotal.getColumnIndex("Total"));
                Log.e("Total", String.valueOf(total));
                totalPrice.setText("Rs. " + String.valueOf(total));
            }
            cursorTotal.close();
            db.close();
        }

        private void calculatePrice(int id) {
            db = projectDatabase.getReadableDatabase();
            try {
                Cursor curSubTotal = db.rawQuery("Select " + Constants.cart_col_quan + " * " + Constants.cart_col_price + " as SubTotal From " + Constants.cart_tableName, null);
                if (curSubTotal.moveToFirst()) {
                    SubTotal = curSubTotal.getInt(curSubTotal.getColumnIndex("SubTotal"));
                    Log.e("SubTotal", String.valueOf(SubTotal));
                    //cart.setSubTotal(SubTotal);

                    db = projectDatabase.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Constants.cart_sub_total, SubTotal);
                    db.update(Constants.cart_tableName, values, "id = ?", new String[]{String.valueOf(id)});
                    db.close();
                }
                curSubTotal.close();

                //db.close();
            } catch (Exception e) {
                Log.e("Excep", e.toString());
            }
        }

        private void updateCart(int id, int qty) {
            db = projectDatabase.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Constants.cart_col_id, id);
            //values.put(Constants.cart_col_fName, cart.getName());
            //values.put(Constants.cart_col_category, cart.getCategory());
            //values.put(Constants.cart_col_price, cart.getPrice());
            values.put(Constants.cart_col_quan, qty);
            //values.put(Constants.cart_col_subTotal, cart.getSubTotal());

            db.update(Constants.cart_tableName, values, "id = ?", new String[]{String.valueOf(id)});
            db.close();
            calculatePrice(id);
            calculateTotal();
            notifyDataSetChanged();
            setAdapter();
        }


    }

}