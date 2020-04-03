package myproject.quickgrocer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import myproject.quickgrocer.Admin.AdminDashboard;
import myproject.quickgrocer.Database.ProjectDatabase;
import myproject.quickgrocer.User.NavActivity;
import myproject.quickgrocer.User.SubCategoryList;
import myproject.quickgrocer.User.UserHome;

public class MainActivity extends AppCompatActivity {

    Button login;
    String strUsername, strPassword;
    EditText username, password;
    Button register;
    public static String sendUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectDatabase projectDatabase = new ProjectDatabase(MainActivity.this);
                strUsername = username.getText().toString();
                strPassword = password.getText().toString();
                Log.e(strUsername + " -- ", strPassword);
                boolean res = projectDatabase.checkUser(strUsername, strPassword);
                if (res) {
                    Toast.makeText(MainActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, NavActivity.class);
                    startActivity(intent);
                    sendUser = strUsername;

                    /*getSupportFragmentManager().beginTransaction()
                            .add(R.id.nav_host_fragment, new UserHome()).commit();*/

                } else if (strUsername.contains("admin") && strPassword.contains("678")) {
                    startActivity(new Intent(MainActivity.this, AdminDashboard.class));
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Login Error");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }


            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
