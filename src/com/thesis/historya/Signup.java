package com.thesis.historya;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends Activity implements OnClickListener {

    private Button signUp;
    private Button clear;
    private EditText etUsername;
    private EditText etPassword;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase sqlDb;
    private Cursor cursor;
    private String[] columns;
    private boolean matchedUsername = false;
    String username = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        signUp = (Button) findViewById(R.id.btnSignup);
        clear = (Button) findViewById(R.id.btnClear2);

        etUsername = (EditText) findViewById(R.id.etUserNameSignup);
        etPassword = (EditText) findViewById(R.id.etPasswordSignup);
        signUp.setOnClickListener(this);
        clear.setOnClickListener(this);
        dbHelper = new DatabaseHelper(this);
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
        case R.id.btnSignup:
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            cursor = sqlDb.query(DatabaseHelper.TABLE_NAME2, columns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    if (etUsername.getText().equals(cursor.getString(1))) {
                        matchedUsername = true;
                    }
                }
            }
            if (matchedUsername) {
                Toast.makeText(getBaseContext(), "Sorry, this username is already taken! Please use another username.", Toast.LENGTH_LONG).show();
                matchedUsername = false;
            } else {
                dbHelper.insertRecordForUsersAccount(sqlDb, DatabaseHelper.TABLE_NAME2, username, password);
                Toast.makeText(getBaseContext(), "Successfully created an account!", Toast.LENGTH_LONG).show();
            }
            break;
        case R.id.btnClear2:
            etUsername.setText("");
            etPassword.setText("");
            etUsername.requestFocus();
            break;
        }
    }
}
