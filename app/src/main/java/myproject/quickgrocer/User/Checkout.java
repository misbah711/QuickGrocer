package myproject.quickgrocer.User;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.util.ArrayList;
import java.util.List;

import myproject.quickgrocer.Constants;
import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.MainActivity;
import myproject.quickgrocer.Models.Cart;
import myproject.quickgrocer.R;

public class Checkout extends Fragment {
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    ArrayList<Cart> cartList;
    Cart cart;
    TextView totalPrice;
    Button checkout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_checkout, container, false);
        recyclerView = root.findViewById(R.id.recyvlerView);
        projectDatabase = new ProjectDatabase(getContext());
        cartList = new ArrayList<>();


        recyclerView = root.findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cartAdapter = new CartAdapter(getContext(), readcartList());
        recyclerView.setAdapter(cartAdapter);

        totalPrice = root.findViewById(R.id.totalPrice);
        checkout = root.findViewById(R.id.confirm);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirm Order");
                builder.setMessage("Click OK to confirm Order");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db = projectDatabase.getReadableDatabase();
                        Cursor cursor = db.rawQuery("Select id, ItemName, Category, SubCategory, Price, Image, Weight, Quantity From Cart", new String[]{});

                        if (cursor.moveToFirst()) {
                            do {
                                int id = cursor.getInt(0);
                                String Name = cursor.getString(1);
                                String Category = cursor.getString(2);
                                String SubCategory = cursor.getString(3);
                                double Price = cursor.getDouble(4);
                                String Image = cursor.getString(5);
                                String Weight = cursor.getString(6);
                                int Quantity = cursor.getInt(7);


                                long res = projectDatabase.confirmOrder(Name, Category, SubCategory,
                                        Price, Image, Weight, Quantity, UserDashboardActivity.user);
                                Log.e("App Confirm", String.valueOf(res));
                                Log.e("App Name", Name);

                                db = projectDatabase.getWritableDatabase();
                                db.execSQL("delete from " + Constants.cart_tableName);
                                db.close();


                            } while (cursor.moveToNext());
                        }

                        cursor.close();

                        Toast.makeText(getContext(), "Your Order is Confirmed", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getContext(), UserDashboardActivity.class));
                        getActivity().finish();
                        sendNotification();
                /*        projectDatabase.confirmOrder();
                        Toast.makeText(getContext(), "Order has been Confirmed", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getContext(), UserDashboardActivity.class));*/
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getContext(), UserDashboardActivity.class));
                        db = projectDatabase.getWritableDatabase();
                        db.execSQL("delete from " + Constants.cart_tableName);
                        db.close();
                    }
                });
                builder.show();

            }
        });
        return root;
    }


    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Order Confirm")
                .setContentText("Your Order is Confirm");

        notificationManager.notify(notificationId, mBuilder.build());
    }

    public void setAdapter() {
        cartList.clear();
        cartAdapter = new CartAdapter(getContext(), readcartList());
        recyclerView.setAdapter(cartAdapter);
    }

    private ArrayList<Cart> readcartList() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select id, ItemName, Category, SubCategory, Price, Image, Weight, Quantity From Cart", new String[]{});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                final String Name = cursor.getString(1);
                String Category = cursor.getString(2);
                String subCategory = cursor.getString(3);
                double Price = cursor.getDouble(4);
                String Image = cursor.getString(5);
                String Weight = cursor.getString(6);
                int Quantity = cursor.getInt(7);
                // Log.e("Cart List", Image + "--" + Name + "- " + Price);

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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.checkout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.checkoout:
                startActivity(new Intent(this, Checkout.class));
                return true;
            case R.id.logout:
                startActivity(new Intent(this, MainActivity.class));
                getContext().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        Context context;
        private List<Cart> cartList;
        private int cartId, qty;
        double price;
        String name, weight, category, subCategory, imageItem;

        public CartAdapter(Context context, ArrayList<Cart> cartList) {
            this.context = context;
            this.cartList = cartList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_checkout_list, parent, false);

            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            holder.itemView.requestLayout();
            cartId = cartList.get(position).getId();
            name = cartList.get(position).getName();
            category = cartList.get(position).getCategory();
            subCategory = cartList.get(position).getSubCategory();
            price = cartList.get(position).getPrice();
            weight = cartList.get(position).getWeight();
            qty = cartList.get(position).getQty();
            imageItem = cartList.get(position).getImg();
            // Log.e("Cart ids", String.valueOf(cartId));

            holder.itemName.setText(name);
            holder.itemPrice.setText("Rs. " + String.valueOf(price));
            holder.weight.setText(String.valueOf(weight));
//            Log.e("cartQty", String.valueOf(qty));

            if (imageItem.length() > 0) {
                String uri = "@drawable/" + imageItem;
                Log.e("image", uri);
                int imageResource = getResources().getIdentifier(uri, null, getContext().getPackageName());
                try {
                    Drawable res = getResources().getDrawable(imageResource);
                    holder.icon.setImageDrawable(res);
                } catch (Resources.NotFoundException e) {
                    holder.icon.setImageResource(R.mipmap.ic_launcher);

                }
            }

            //    Log.e("Cart cartQty", String.valueOf(cart));
            calculateTotal();
            holder.itemQtyText.setNumber(String.valueOf(qty));
            holder.itemQtyText.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                    cart = cartList.get(position);
                    cartId = cartList.get(position).getId();
                    cart.setQty(newValue);
                    updateCart();

                    Log.e("Pos", String.valueOf(position) + "--" + cartId);
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db = projectDatabase.getWritableDatabase();
                    db.delete(Constants.cart_tableName, "id = ?", new String[]{String.valueOf(cartId)});
                    calculateTotal();
                    notifyDataSetChanged();
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
            TextView itemName, itemPrice, weight;
            ElegantNumberButton itemQtyText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.itemImage);
                itemName = itemView.findViewById(R.id.itemname);
                itemPrice = itemView.findViewById(R.id.iprice);
                delete = itemView.findViewById(R.id.delitem);
                itemQtyText = itemView.findViewById(R.id.number_button);
                weight = itemView.findViewById(R.id.fweight);

            }
        }

        private void calculateTotal() {
            db = projectDatabase.getReadableDatabase();
            Cursor cursorTotal = db.rawQuery("SELECT SUM(" + Constants.cart_col_quan + " * " + Constants.cart_col_price + ") as Total FROM " + Constants.cart_tableName, null);
            if (cursorTotal.moveToFirst()) {
                int total = cursorTotal.getInt(cursorTotal.getColumnIndex("Total"));
                // Log.e("Total", String.valueOf(total));
                totalPrice.setText("Rs. " + total);
            }
            cursorTotal.close();
            db.close();
        }


        private void updateCart() {
            db = projectDatabase.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Constants.cart_col_quan, cart.getQty());
            try {
                String query = String.format("UPDATE Cart SET Quantity= %s WHERE id = %d", cart.getQty(), cart.getId());
                db.execSQL(query);
                db.close();
            } catch (Exception e) {
                Log.e("Qu Exce", e.toString());
            }
            //db.update(Constants.cart_tableName, values, "id = ?", new String[]{String.valueOf(id)});

            calculateTotal();
            notifyDataSetChanged();
            setAdapter();
        }


    }

}