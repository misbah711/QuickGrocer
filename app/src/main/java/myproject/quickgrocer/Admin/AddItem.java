package myproject.quickgrocer.Admin;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import myproject.quickgrocer.Constants;
import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.R;

public class AddItem extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner category;
    EditText name, price, editTextImage, subCate, weight;
    Button add_item, clear, update;
    String strCat, strName, strImage, strSubCat, strWeight;
    double strPrice;
    ProjectDatabase projectDatabase;
    public static String Grocery = "Grocery";
    public static String Bevarages = "Bevarages";
    public static String Bakery = "Bakery and Dairy";
    public static String HomeCare = "Home Care";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        projectDatabase = new ProjectDatabase(AddItem.this);
        category = findViewById(R.id.item_category);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        editTextImage = findViewById(R.id.foodImage);
        add_item = findViewById(R.id.add_item);
        update = findViewById(R.id.update_item);
        clear = findViewById(R.id.clear);
        weight = findViewById(R.id.weight);
        subCate = findViewById(R.id.item_subCategory);

        populateSpinner();
        updateValues();

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearValues();
            }
        });

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName = name.getText().toString();
                strPrice = Double.parseDouble(price.getText().toString());
                strImage = editTextImage.getText().toString().toLowerCase();
                strSubCat = subCate.getText().toString();
                strWeight = weight.getText().toString();
                if (strImage.equals("")){
                    strImage = "ic_launcher";
                }
                if (!strName.isEmpty() && strPrice > 0 && !strSubCat.isEmpty()) {
                    try {
                        long val = projectDatabase.addFood(strName, strCat, strSubCat, strPrice, strWeight, strImage);
                        if (val > 0) {
                            Toast.makeText(AddItem.this, "Items Added", Toast.LENGTH_SHORT).show();
                            clearValues();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddItem.this);
                            builder.setTitle("Error");
                            builder.setMessage("Some Error Found");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }


                    } catch (Exception e) {
                        Log.e("Register Exception", e.getMessage());
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddItem.this);
                        builder.setTitle("Error");
                        builder.setMessage(e.getMessage());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                } else
                    Toast.makeText(AddItem.this, "Enter Some Values", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateValues() {
        final int getId = getIntent().getIntExtra(Constants.item_col_id, -1);
        final String getFName = getIntent().getStringExtra(Constants.item_col_itemName);
        final String getFCat = getIntent().getStringExtra(Constants.item_col_category);
        final String getFSubCat = getIntent().getStringExtra(Constants.item_col_sub_category);
        final double getFPrice = getIntent().getDoubleExtra(Constants.item_col_price, -1);
        final String getFImage = getIntent().getStringExtra(Constants.item_col_image);
        Log.e("Receive Data", getId + "-" + getFName + "-" + getFCat + "-" + getFPrice);

        if (getId == -1 && getFName == null && getFCat == null && getFPrice == -1) {

            return;
        } else {
            update.setVisibility(View.VISIBLE);
            add_item.setVisibility(View.GONE);
            name.setText(getFName);
            subCate.setText(getFSubCat);
            price.setText(String.valueOf(getFPrice));
            editTextImage.setText(getFImage);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        strName = name.getText().toString();
                        strPrice = Double.parseDouble(price.getText().toString());
                        strImage = editTextImage.getText().toString();
                        strSubCat = subCate.getText().toString();
                        if (strImage.equals("")){
                            strImage = "ic_launcher";
                        }
                        SQLiteDatabase db = projectDatabase.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(Constants.item_col_id, getId);
                        values.put(Constants.item_col_itemName, strName);
                        values.put(Constants.item_col_category, strCat);
                        values.put(Constants.item_col_sub_category, strSubCat);
                        values.put(Constants.item_col_price, strPrice);
                        values.put(Constants.item_col_weight, strWeight);
                        values.put(Constants.item_col_image, strImage);

                        db.update(Constants.item_tableName, values, "id = ?", new String[]{String.valueOf(getId)});
                        db.close();
                        Toast.makeText(AddItem.this, "Values Updated", Toast.LENGTH_SHORT).show();
                        clearValues();
                    } catch (Exception ex) {
                        Log.e("Update Exception", ex.toString());
                    }
                }
            });
        }
    }

    private void clearValues() {
        name.setText("");
        price.setText("");
        editTextImage.setText("");
        subCate.setText("");
        weight.setText("");
        name.setHint("Enter Name");
        price.setHint("Enter Price");
        editTextImage.setHint("Enter Image Name");
        subCate.setHint("Enter Sub Category");
        weight.setHint("Enter Weight");



    }

    private void populateSpinner() {
        category.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<>();
        categories.add(Grocery);
        categories.add(Bevarages);
        categories.add(Bakery);
        categories.add(HomeCare);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        strCat = parent.getItemAtPosition(position).toString();
        /*if (strCat.contains(grocery)){
            subCategory.setOnItemSelectedListener(this);
            List<String> sub_categories = new ArrayList<>();
            sub_categories.add("Oil & Gee");
            sub_categories.add("Daal, Rice & Floor");
            sub_categories.add("Baking & Desserts");
            sub_categories.add("Sauces & Olives");
            ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sub_categories);
            subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subCategory.setAdapter(subAdapter);
          //  Log.e("Sub", subAdapter.getItem(subAdapter.getPosition()).toString());
        }
        if (strCat.contains(bevarge)){
            subCategory.setOnItemSelectedListener(this);
            List<String> sub_categories = new ArrayList<>();
            sub_categories.add("Cold Drinks");
            sub_categories.add("Juices");
            sub_categories.add("Tea");
            sub_categories.add("Mineral Water");
            ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sub_categories);
            subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subCategory.setAdapter(subAdapter);
        }
        if (strCat.contains(bakery)){
            subCategory.setOnItemSelectedListener(this);
            List<String> sub_categories = new ArrayList<>();
            sub_categories.add("Milk");
            sub_categories.add("Breads");
            sub_categories.add("Eggs");
            sub_categories.add("Cream & Butter");
            ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sub_categories);
            subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subCategory.setAdapter(subAdapter);
        }
        if (strCat.contains(homeCare)){
            subCategory.setOnItemSelectedListener(this);
            List<String> sub_categories = new ArrayList<>();
            sub_categories.add("Floor & Bath Cleaning");
            sub_categories.add("Laundry");
            sub_categories.add("Kitchen Cleaning ");
            sub_categories.add("Tissue Papers");
            ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sub_categories);
            subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subCategory.setAdapter(subAdapter);
        }*/


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
