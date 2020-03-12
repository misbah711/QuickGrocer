package myproject.quickgrocer.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import myproject.quickgrocer.Constants;
import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.Models.ItemModel;
import myproject.quickgrocer.Models.OrderModel;
import myproject.quickgrocer.R;
import myproject.quickgrocer.User.Checkout;

public class OrderList extends AppCompatActivity {
    RecyclerView recyclerView;
    ProjectDatabase projectDatabase;
    private ArrayList<OrderModel> orderList;
    OrderModel orderModel;
    public static OrderListAdapter orderListAdapter;
    SQLiteDatabase db;
    String getUsername;

    TextView totalPrice;
    Button checkout;
    int total;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getUsername = getIntent().getStringExtra("username");
        Log.e("getUser", getUsername);

        projectDatabase = new ProjectDatabase(this);
        orderList = new ArrayList<>();
        clearTable();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        orderListAdapter = new OrderListAdapter(this, readAllData());
        recyclerView.setAdapter(orderListAdapter);
        totalPrice = findViewById(R.id.totalPrice);
        checkout = findViewById(R.id.confirm);
        checkout.setVisibility(View.GONE);
        calculateTotal();

    }

    public void setAdapter() {
        orderList.clear();
        orderListAdapter = new OrderListAdapter(this, readAllData());
        recyclerView.setAdapter(orderListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderListAdapter.notifyDataSetChanged();
    }


    private ArrayList<OrderModel> readAllData() {
        boolean distinct = true;
        total = 0;
        db = projectDatabase.getReadableDatabase();
        String selection = Constants.order_custName + " = ?";
        String[] column = {Constants.cart_col_id, Constants.cart_col_itemName, Constants.cart_col_category, Constants.cart_col_sub_category,
                Constants.cart_col_price, Constants.cart_col_image, Constants.cart_col_weight,
                Constants.cart_col_quan, Constants.order_custName, Constants.order_col_userphoneno, Constants.order_col_useraddress};
        String[] args = {getUsername};
        Cursor cursor = db.query(distinct, Constants.order_tableName, column, selection, args, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String FoodName = cursor.getString(1);
                String Category = cursor.getString(2);
                String SubCategory = cursor.getString(3);
                double Price = cursor.getDouble(4);
                String Image = cursor.getString(5);
                String weight = cursor.getString(6);
                int quan = cursor.getInt(7);
                String custName = cursor.getString(8);
                int custPhone = cursor.getInt(9);
                String custAddress = cursor.getString(10);

                long val = projectDatabase.insertAdminOrder(FoodName, Category, SubCategory, Price, Image, quan, custName, weight, custPhone, custAddress);
                Log.e("Val Added", String.valueOf(val));

                orderModel = new OrderModel();
                orderModel.setId(id);
                orderModel.setName(FoodName);
                orderModel.setCategory(Category);
                orderModel.setSubCategory(SubCategory);
                orderModel.setPrice(Price);
                orderModel.setImg(Image);
                orderModel.setQty(quan);
                orderModel.setWeight(weight);
                orderModel.setCustName(custName);
                orderModel.setPhoneNO(custPhone);
                orderModel.setAddress(custAddress);
                orderList.add(orderModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return orderList;

    }

    public void clearTable() {
        db = projectDatabase.getWritableDatabase();
        db.execSQL("delete from " + Constants.adminOrder_tableName);
        db.close();
        total = 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        db = projectDatabase.getWritableDatabase();
        db.execSQL("delete from " + Constants.adminOrder_tableName);
        db.close();
        total = 0;
        OrderList.this.finish();

    }


    private class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
        Context context;
        private List<OrderModel> orderLists;

        public OrderListAdapter(Context context, ArrayList<OrderModel> orderLists) {
            this.context = context;
            this.orderLists = orderLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.food_listitems, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final int id = orderList.get(position).getId();
            final String name = orderList.get(position).getName();
            final String category = orderList.get(position).getCategory();
            final String subCategory = orderList.get(position).getSubCategory();
            final double price = orderList.get(position).getPrice();
            final String imageFood = orderList.get(position).getImg();
            final String weight = orderList.get(position).getWeight();
            final int Quan = orderList.get(position).getQty();
            final String CustName = orderList.get(position).getCustName();
            final int phone = orderList.get(position).getPhoneNO();
            final String address = orderList.get(position).getAddress();

            Log.e("Data", imageFood + "-" + name + price + subCategory);
            holder.edit.setVisibility(View.GONE);
            holder.Name.setText(name);
            holder.Category.setText(category);
            holder.SubCategory.setText(subCategory);
            holder.Price.setText("Rs. " + String.valueOf(price));
            holder.weight.setText(weight);
            holder.quan.setText(String.valueOf(Quan));
            holder.phone.setText(String.valueOf(phone));
            holder.address.setText(address);

            calculateTotal();

            if (imageFood.length() > 0) {
                String uri = "@drawable/" + imageFood;
                Log.e("image", uri);
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                try {
                    Drawable res = getResources().getDrawable(imageResource);
                    holder.icon.setImageDrawable(res);
                } catch (Resources.NotFoundException e) {
                    holder.icon.setImageResource(R.mipmap.ic_launcher);

                }
            }


            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrderList.this);
                    builder.setTitle("Delete Item");
                    builder.setMessage("Are you sure to Delete this Item");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db = projectDatabase.getWritableDatabase();
                            db.delete(Constants.order_tableName, Constants.item_col_id + " = ?", new String[]{String.valueOf(id)});
                            Toast.makeText(context, "Item has been deleted", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            notifyDataSetChanged();

                            clearTable();
                            finish();
                            startActivity(getIntent());

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView icon, delete, edit;
            TextView Name, Price, Category, SubCategory, weight, quan, phone, address;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.imageFood);
                Name = itemView.findViewById(R.id.foodName);
                Price = itemView.findViewById(R.id.fPrice);
                Category = itemView.findViewById(R.id.food_Category);
                SubCategory = itemView.findViewById(R.id.item_subCategory);
                weight = itemView.findViewById(R.id.item_weight);
                quan = itemView.findViewById(R.id.item_quan);
                delete = itemView.findViewById(R.id.delItem);
                edit = itemView.findViewById(R.id.editItem);
                phone = itemView.findViewById(R.id.custPhone);
                address = itemView.findViewById(R.id.custAddress);


            }
        }
    }

    private void calculateTotal() {
        total = 0;
        db = projectDatabase.getReadableDatabase();
        Cursor cursorTotal = db.rawQuery("SELECT SUM(" + Constants.adminOrder_col_qty + " * " + Constants.adminOrder_col_price + ") as Total FROM "
                + Constants.adminOrder_tableName, null);
        if (cursorTotal.moveToFirst()) {
            total = cursorTotal.getInt(cursorTotal.getColumnIndex("Total"));
            // Log.e("Total", String.valueOf(total));
            totalPrice.setText("Rs. " + total);
        }
        cursorTotal.close();
        db.close();
    }
}



