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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import myproject.quickgrocer.Constants;
import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.Models.ItemModel;
import myproject.quickgrocer.R;

public class OrderList extends AppCompatActivity {
    RecyclerView recyclerView;
    ProjectDatabase projectDatabase;
    private ArrayList<ItemModel> itemList;
    ItemModel itemModel;
    public static OrderListAdapter orderListAdapter;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        projectDatabase = new ProjectDatabase(this);
        itemList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        orderListAdapter = new OrderListAdapter(this, readAllData());
        recyclerView.setAdapter(orderListAdapter);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ItemList.this, AddItem.class));
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderListAdapter.notifyDataSetChanged();
    }


    private ArrayList<ItemModel> readAllData() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select " + Constants.item_col_id + ", " + Constants.item_col_itemName + ", " + Constants.item_col_category
                + ", " + Constants.item_col_sub_category + ", " + Constants.item_col_price + ", " + Constants.item_col_image + " From " + Constants.item_tableName, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String FoodName = cursor.getString(1);
                String Category = cursor.getString(2);
                String SubCategory = cursor.getString(3);
                double Price = cursor.getDouble(4);
                String Image = cursor.getString(5);

                itemModel = new ItemModel();
                itemModel.setId(id);
                itemModel.setItemName(FoodName);
                itemModel.setItemCategory(Category);
                itemModel.setItemSubCategory(SubCategory);
                itemModel.setItemPrice((int) Price);
                itemModel.setItemImage(Image);
                itemList.add(itemModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return itemList;

    }

    private class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
        Context context;
        private List<ItemModel> foodList;

        public OrderListAdapter(Context context, ArrayList<ItemModel> itemModel) {
            this.context = context;
            this.foodList = itemModel;
        }

        @NonNull
        @Override
        public OrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.food_listitems, parent, false);

            return new OrderListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderListAdapter.ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final int id = foodList.get(position).getId();
            final String name = foodList.get(position).getItemName();
            final String category = foodList.get(position).getItemCategory();
            final String subCategory = foodList.get(position).getItemSubCategory();
            final double price = foodList.get(position).getItemPrice();
            final String imageFood = foodList.get(position).getItemImage();
            Log.e("Data", imageFood + "-" + name + price + subCategory);
            holder.edit.setVisibility(View.GONE);
            holder.Name.setText(name);
            holder.Category.setText(category);
            holder.SubCategory.setText(subCategory);
            holder.Price.setText(String.valueOf(price));

            if (imageFood.length() > 1) {
                String uri = "@drawable/" + imageFood;
                Log.e("image", uri);
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                holder.icon.setImageDrawable(res);
            } else {
                holder.icon.setImageResource(R.mipmap.ic_launcher);
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
                            db.delete(Constants.item_tableName, Constants.item_col_id + " = ?", new String[]{String.valueOf(id)});
                            Toast.makeText(context, "Item has been deleted", Toast.LENGTH_SHORT).show();
                            dialog.cancel();

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
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView icon, delete, edit;
            TextView Name, Price, Category, SubCategory;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.imageFood);
                Name = itemView.findViewById(R.id.foodName);
                Price = itemView.findViewById(R.id.fPrice);
                Category = itemView.findViewById(R.id.food_Category);
                SubCategory = itemView.findViewById(R.id.item_subCategory);
                delete = itemView.findViewById(R.id.delItem);
                edit = itemView.findViewById(R.id.editItem);


            }
        }
    }
}



