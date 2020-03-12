package myproject.quickgrocer.Admin;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.MainActivity;
import myproject.quickgrocer.R;

import static myproject.quickgrocer.Constants.order_custName;
import static myproject.quickgrocer.Constants.order_tableName;

public class ListbyName extends AppCompatActivity {
    RecyclerView recyclerView;
    ByListApter byListApter;
    TextView textView;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    public static String user;
    ArrayList<String> userlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        recyclerView = findViewById(R.id.recyvlerView);


        projectDatabase = new ProjectDatabase(ListbyName.this);
        userlist = new ArrayList<>();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        byListApter = new ByListApter(ListbyName.this, readusername());
        recyclerView.setAdapter(byListApter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.logout:
                startActivity(new Intent(this, MainActivity.class));
                ListbyName.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private ArrayList<String> readusername() {
        db = projectDatabase.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select DISTINCT " + order_custName + " From " + order_tableName, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                //Log.e("Category List", Category);
                userlist.add(username);
                // Log.e("List", categoryList.toString());

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userlist;

    }

    public class ByListApter extends RecyclerView.Adapter<ByListApter.ViewHolder> {
        private List<String> userList;
        Context context;

        public ByListApter(Context context, ArrayList<String> userList) {
            this.context = context;
            this.userList = userList;
        }


        @NonNull
        @Override
        public ByListApter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_list_items, parent, false);
            return new ByListApter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ByListApter.ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final String username = userList.get(position);
            holder.textView.setText(username);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, categ, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ListbyName.this, OrderList.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                textView = itemView.findViewById(R.id.textView);
            }
        }
    }
}
