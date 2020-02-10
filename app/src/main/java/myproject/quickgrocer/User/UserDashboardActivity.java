package myproject.quickgrocer.User;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import myproject.quickgrocer.Admin.AddItem;
import myproject.quickgrocer.Admin.ItemList;
import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.R;

import java.util.ArrayList;
import java.util.List;

import static myproject.quickgrocer.Constants.item_col_category;
import static myproject.quickgrocer.Constants.item_tableName;

public class UserDashboardActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CategoriesAdapter categoriesAdapter;
    TextView textView;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    ArrayList<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        recyclerView = findViewById(R.id.recyvlerView);

        textView = findViewById(R.id.usernameText);
        String user = getIntent().getStringExtra("Username");
        textView.setText("Welcome\n" + user);
        projectDatabase = new ProjectDatabase(UserDashboardActivity.this);
        categoryList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        categoriesAdapter = new CategoriesAdapter(UserDashboardActivity.this, readCategories());
        recyclerView.setAdapter(categoriesAdapter);

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

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private ArrayList<String> readCategories() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select DISTINCT " + item_col_category + " From " + item_tableName, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                String Category = cursor.getString(0);
                Log.e("Category List", Category);
                categoryList.add(Category);
                Log.e("List", categoryList.toString());

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categoryList;

    }

    public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
        private List<String> catList;
        Context context;

        public CategoriesAdapter(Context context, ArrayList<String> cateList) {
            this.context = context;
            this.catList = cateList;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_list_items, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final String categ = catList.get(position);
            holder.textView.setText(categ);
            if (categ.equals(AddItem.Bevarages)){
                holder.imageView.setImageResource(R.drawable.bevarage);
            }
            else if (categ.equals(AddItem.Grocery)){
                holder.imageView.setImageResource(R.drawable.grocery);
            }
            else if (categ.equals(AddItem.Bakery)){
                holder.imageView.setImageResource(R.drawable.bakery);
            }
            else if (categ.equals(AddItem.HomeCare)){
                holder.imageView.setImageResource(R.drawable.homecare);
            }
            else {
                holder.imageView.setImageResource(R.mipmap.ic_launcher);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Toast.makeText(context, categ, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserDashboardActivity.this, SubCategoryList.class);
                    intent.putExtra("Category", categ);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return catList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                textView = itemView.findViewById(R.id.textView);
            }
        }
    }
}
