package com.thesis.historya;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {

    private Button login;
    private Button clear;
    private EditText etUsername;
    private EditText etPassword;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase sqlDb;
    private Cursor cursor;
    private String[] columns;
    private boolean matchedUsername = false;
    private String username = "";
    private String password = "";
    private TextView tvSignup;
    GameUtils gameUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        gameUtils = new GameUtils(this);
        login = (Button) findViewById(R.id.btnLogin);
        clear = (Button) findViewById(R.id.btnClear);

        etUsername = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        login.setOnClickListener(this);
        dbHelper = new DatabaseHelper(this);

        tvSignup = (TextView) findViewById(R.id.tvSignUp);
        clear.setOnClickListener(this);
        tvSignup.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sqlDb = dbHelper.getWritableDatabase();
        columns = new String[] {" _id", DatabaseHelper.COL_USER_NAME, DatabaseHelper.COL_PASSWORD };

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
        case R.id.btnLogin:
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            String searchQuery = "SELECT * FROM " + DatabaseHelper.TABLE_NAME2;
            cursor = sqlDb.rawQuery(searchQuery, null);
            int usernameIndex = cursor.getColumnIndex(DatabaseHelper.COL_USER_NAME);
            if (cursor.moveToFirst()) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    if (etUsername.getText().toString().equals(cursor.getString(usernameIndex))) {
                        matchedUsername = true;
                        gameUtils.storeUsername(username);
                    }
                }
            }
            if (matchedUsername) {
                Toast.makeText(getBaseContext(), "Welcome!", Toast.LENGTH_LONG).show();
                matchedUsername = false;
                Login.this.startActivity(new Intent(Login.this, MainMenuActivity.class));
                Login.this.finish();
            } else {
                Toast.makeText(getBaseContext(), "The username and/or password you entered is not in our database. Please create a new account by clicking Signup.", Toast.LENGTH_LONG).show();
            }
            break;
        case R.id.tvSignUp:
            Login.this.startActivity(new Intent(Login.this, Signup.class));
            break;
        case R.id.btnClear:
            etUsername.setText("");
            etPassword.setText("");
            etUsername.requestFocus();
            break;
        }
    }
}
