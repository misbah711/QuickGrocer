package myproject.quickgrocer.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import myproject.quickgrocer.Admin.AdminDashboard;
import myproject.quickgrocer.Constants;
import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.MainActivity;
import myproject.quickgrocer.Models.Cart;
import myproject.quickgrocer.Models.ItemModel;
import myproject.quickgrocer.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UserItemList extends AppCompatActivity {
    RecyclerView recyclerView;
    UserFoodListAdapter userFoodListAdapter;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    ArrayList<ItemModel> foodList;
    String getSubCat, getCat;
    ItemModel itemModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getCat = getIntent().getStringExtra("Category");
        getSubCat = getIntent().getStringExtra("SubCategory");

        recyclerView = findViewById(R.id.recyvlerView);

        projectDatabase = new ProjectDatabase(UserItemList.this);
        foodList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        userFoodListAdapter = new UserFoodListAdapter(UserItemList.this, readFoodList());
        recyclerView.setAdapter(userFoodListAdapter);
    }

    private ArrayList<ItemModel> readFoodList() {
        boolean distinct = true;
        db = projectDatabase.getReadableDatabase();
        String selection = Constants.item_col_sub_category + " = ?";
        String[] column = {Constants.item_col_itemName, Constants.item_col_price, Constants.item_col_weight, Constants.item_col_image};
        String[] args = {getSubCat};
        Cursor cursor = db.query(distinct, Constants.item_tableName, column, selection, args, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String FoodName = cursor.getString(0);
                double Price = cursor.getDouble(1);
                String weight = cursor.getString(2);
                String Image = cursor.getString(3);
                // Log.e(" Food List", FoodName + "- " + Price);

                itemModel = new ItemModel();
                itemModel.setItemName(FoodName);
                itemModel.setItemCategory(getCat);
                itemModel.setItemSubCategory(getSubCat);
                itemModel.setItemPrice((int) Price);
                itemModel.setWeight(weight);
                itemModel.setItemImage(Image);
                foodList.add(itemModel);
                // Log.e("List", foodList.toString());

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return foodList;
    }

    @Override
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
                UserItemList.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class UserFoodListAdapter extends RecyclerView.Adapter<UserFoodListAdapter.ViewHolder> {
        private List<ItemModel> foodList;
        Context context;

        public UserFoodListAdapter(Context context, ArrayList<ItemModel> foodList) {
            this.context = context;
            this.foodList = foodList;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_user_item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final String FoodName = foodList.get(position).getItemName();
            final double price = foodList.get(position).getItemPrice();
            final String image = foodList.get(position).getItemImage();
            final String weight = foodList.get(position).getWeight();

            holder.weight.setText(weight);
            holder.foodName.setText(FoodName);
            holder.price.setText("Rs. " + String.valueOf(price));
            // Log.e("Read Data", FoodName + price);

            if (image.length() > 0) {
                String uri = "@drawable/" + image;
                Log.e("image", uri);
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                try {
                    Drawable res = getResources().getDrawable(imageResource);
                    holder.imageView.setImageDrawable(res);
                } catch (Resources.NotFoundException e) {
                    holder.imageView.setImageResource(R.mipmap.ic_launcher);

                }
            }

            final Cart cart = new Cart();
            cart.setName(FoodName);
            cart.setCategory(getCat);
            cart.setSubCategory(getSubCat);
            cart.setPrice(price);
            cart.setWeight(weight);
            cart.setQty(1);
            cart.setImg(image);

            holder.cartImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        long res = projectDatabase.insertCart(FoodName, cart.getCategory(), cart.getSubCategory(), cart.getPrice(), cart.getImg(), cart.getWeight(), cart.getQty());
                        if (res > 0) {
                            Toast.makeText(UserItemList.this, "Item Added to your Cart", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserItemList.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView, cartImage;
            TextView foodName, price, weight;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageFood);
                foodName = itemView.findViewById(R.id.foodName);
                price = itemView.findViewById(R.id.fPrice);
                weight = itemView.findViewById(R.id.fWeight);
                cartImage = itemView.findViewById(R.id.cart);
            }
        }
    }
}
