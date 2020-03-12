package myproject.quickgrocer.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import myproject.quickgrocer.Admin.AddItem;
import myproject.quickgrocer.Admin.AdminDashboard;
import myproject.quickgrocer.Constants;
import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.MainActivity;
import myproject.quickgrocer.R;

import static myproject.quickgrocer.Constants.item_col_category;
import static myproject.quickgrocer.Constants.item_col_sub_category;
import static myproject.quickgrocer.Constants.item_tableName;

public class SubCategoryList extends Fragment {

    String getCat;
    RecyclerView recyclerView;
    ProjectDatabase projectDatabase;
    ArrayList<String> subCategoryList;
    SubCategoryAdapter subCategoryAdapter;
    SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_list, container, false);
        getCat = getArguments().getString("Category");

        recyclerView = root.findViewById(R.id.recyvlerView);
        projectDatabase = new ProjectDatabase(getContext());
        subCategoryList = new ArrayList<>();

        recyclerView = root.findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        subCategoryAdapter = new SubCategoryAdapter(getContext(), readSubCategories());
        recyclerView.setAdapter(subCategoryAdapter);
        return root;
    }
    

    private ArrayList<String> readSubCategories() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select DISTINCT " + Constants.item_col_sub_category +
                " From " + item_tableName + " Where " + item_col_category + " =?", new String[]{getCat});

        if (cursor.moveToFirst()) {
            do {
                String SubCategory = cursor.getString(0);
                //Log.e("Sub Category List", SubCategory);
                subCategoryList.add(SubCategory);
                //Log.e("List", subCategoryList.toString());

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return subCategoryList;

    }

   /* @Override
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
    }*/


    public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {
        private List<String> subCatList;
        Context context;

        public SubCategoryAdapter(Context context, ArrayList<String> subCatList) {
            this.context = context;
            this.subCatList = subCatList;
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
            final String subCat = subCatList.get(position);
            holder.textView.setText(subCat);
            if (subCat.equals("Cold Drinks")) {
                holder.imageView.setImageResource(R.drawable.colddrinks);
            } else if (subCat.equals("Tea")) {
                holder.imageView.setImageResource(R.drawable.tea);
            } else if (subCat.equals("Sharbat")) {
                holder.imageView.setImageResource(R.drawable.sharbat);
            } else if (subCat.equals("Minral Water")) {
                holder.imageView.setImageResource(R.drawable.minralwater);
            } else if (subCat.equals("Juices")) {
                holder.imageView.setImageResource(R.drawable.juices);
            } else if (subCat.equals("Enery Drinks")) {
                holder.imageView.setImageResource(R.drawable.energy);
            } else if (subCat.equals("Floor,Bath Cleaning")) {
                holder.imageView.setImageResource(R.drawable.floor);
            } else if (subCat.equals("Laundry")) {
                holder.imageView.setImageResource(R.drawable.laundry);
            } else if (subCat.equals("Kitchen Cleaning")) {
                holder.imageView.setImageResource(R.drawable.kitchencleaning);
            } else if (subCat.equals("Air Fresheners")) {
                holder.imageView.setImageResource(R.drawable.airfreshener);
            } else if (subCat.equals("Repellents Mosquito")) {
                holder.imageView.setImageResource(R.drawable.mosquito);
            } else if (subCat.equals("Milk")) {
                holder.imageView.setImageResource(R.drawable.milk);
            } else if (subCat.equals("Bread")) {
                holder.imageView.setImageResource(R.drawable.bread);
            } else if (subCat.equals("Eggs")) {
                holder.imageView.setImageResource(R.drawable.eggs);
            } else {
                holder.imageView.setImageResource(R.mipmap.ic_launcher);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    UserItemList ldf = new UserItemList ();
                    Bundle args = new Bundle();
                    args.putString("Category", getCat);
                    args.putString("SubCategory", subCat);
                    ldf.setArguments(args);

//Inflate the fragment
                    getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, ldf).addToBackStack(null).commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return subCatList.size();
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
