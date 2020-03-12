package myproject.quickgrocer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import myproject.quickgrocer.Database.ProjectDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText user, pass, mail;
    Button register, login;
    String strUserName, strPassword, strEmail;
    ProjectDatabase projectDatabase;
    private AwesomeValidation awesomeValidation;
    CheckBox showPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user = findViewById(R.id.user);
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);

        register = findViewById(R.id.btnSignup);
        login = findViewById(R.id.btnLogin);
        showPass = findViewById(R.id.showPass);
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.user, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.usernameError);
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        awesomeValidation.addValidation(this, R.id.pass, regexPassword, R.string.err_password);
        awesomeValidation.addValidation(this, R.id.mail, Patterns.EMAIL_ADDRESS, R.string.emailError);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (awesomeValidation.validate()) {
                        projectDatabase = new ProjectDatabase(RegisterActivity.this);
                        strEmail = mail.getText().toString();
                        strUserName = user.getText().toString();
                        strPassword = pass.getText().toString();

                        Log.e("Register User", strEmail + " - " + strUserName + " - " + strPassword);
                        SQLiteDatabase db = projectDatabase.getReadableDatabase();
                        Cursor c = db.rawQuery("SELECT * FROM " + Constants.user_tableName + " where " +
                                Constants.user_col_username + " =? ", new String[]{strUserName});

                        Log.e("User Cursor", c.toString());
                        if (c.getCount() > 0) {
                            Toast.makeText(getApplicationContext(), "USER ALREADY EXITS", Toast.LENGTH_LONG).show();
                        } else {
                            long val = projectDatabase.addUser(strEmail, strUserName, strPassword);
                            if (val > 0) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setTitle("Error");
                                builder.setMessage("Registration Error");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Register Exception", e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));

            }
        });

    }

}
