package myproject.quickgrocer.User;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import myproject.quickgrocer.Admin.AddItem;
import myproject.quickgrocer.Constants;
import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.MainActivity;
import myproject.quickgrocer.R;

import static myproject.quickgrocer.Constants.item_col_category;
import static myproject.quickgrocer.Constants.item_tableName;

public class UserHome extends Fragment {
    RecyclerView recyclerView;
    CategoriesAdapter categoriesAdapter;
    TextView textView;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    public static String user;
    ArrayList<String> categoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_dashboard, container, false);
        recyclerView = root.findViewById(R.id.recyvlerView);

        textView = root.findViewById(R.id.usernameText);
        user = MainActivity.sendUser;
        textView.setText("Welcome\n" + user);
        projectDatabase = new ProjectDatabase(getContext());
        categoryList = new ArrayList<>();

        recyclerView = root.findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        categoriesAdapter = new CategoriesAdapter(getContext(), readCategories());
        recyclerView.setAdapter(categoriesAdapter);
        return root;
    }

    private ArrayList<String> readCategories() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select DISTINCT " + item_col_category + " From " + item_tableName, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                String Category = cursor.getString(0);
                categoryList.add(Category);

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
        public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_list_items, parent, false);
            return new CategoriesAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final String categ = catList.get(position);
            holder.textView.setText(categ);
            if (categ.equals(AddItem.Bevarages)) {
                holder.imageView.setImageResource(R.drawable.bevarage);
            } else if (categ.equals(AddItem.Grocery)) {
                holder.imageView.setImageResource(R.drawable.grocery);
            } else if (categ.equals(AddItem.Bakery)) {
                holder.imageView.setImageResource(R.drawable.bakery);
            } else if (categ.equals(AddItem.HomeCare)) {
                holder.imageView.setImageResource(R.drawable.homecare);
            } else {
                holder.imageView.setImageResource(R.mipmap.ic_launcher);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubCategoryList ldf = new SubCategoryList();
                    Bundle args = new Bundle();
                    args.putString("Category", categ);
                    ldf.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, ldf).addToBackStack(null).commit();
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
