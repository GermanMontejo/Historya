package com.thesis.historya;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "HistoryaDB";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME2 = "USER_ACCOUNTS_TABLE";
    public static final String COL_USER_NAME = "USERNAME";
    public static final String COL_PASSWORD = "PASSWORD";
    public static final String COL_SCORE = "SCORE";
    public static final String TABLE_NAME1 = "HISTORYA_TABLE";
    public static final String TABLE_NAME3 = "RIDDLES_TABLE";
    public static final String COL_USER = "USERNAME";
    public static final String COL_RIDDLE = "RIDDLE";
    public static final String COL_ANSWER = "ANSWER";
    public static final String COL_ANSWERED = "ANSWERED";

    public static final String CREATE_SQL2 = "CREATE TABLE " + TABLE_NAME2 + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USER_NAME + " TEXT, " + COL_PASSWORD + " TEXT);";
    public static final String CREATE_SQL1 = "CREATE TABLE " + TABLE_NAME1 + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USER_NAME + " TEXT, " +COL_SCORE + " INTEGER);";
    public static final String CREATE_SQL3 = "CREATE TABLE " + TABLE_NAME3 + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USER + " TEXT, " + COL_RIDDLE + " TEXT, " + COL_ANSWER + " TEXT, " + COL_ANSWERED + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    int heroScore = 0;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL2);
        db.execSQL(CREATE_SQL1);
        db.execSQL(CREATE_SQL3);
    }

    public void insertRecordForUsersAccount(SQLiteDatabase sqlDb, String tableName, String userName, String password) {
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NAME, userName);
        cv.put(COL_PASSWORD, password);
        sqlDb.insert(tableName, null, cv);
        //sqlDb.close();
    }

    public void insertRecordForHistoryaScore(SQLiteDatabase sqlDb, String tableName, String userName, int score) {
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NAME, userName);
        cv.put(COL_SCORE, score);
        sqlDb.insert(tableName, null, cv);
        //sqlDb.close();
    }

    public void insertRecordForRiddles(SQLiteDatabase sqlDb, String tableName, String username, Context context) {

        ContentValues cv = new ContentValues();
        cv.put(COL_USER, username);
        for(String riddle : context.getResources().getStringArray(R.array.riddles)) {
            cv.put(COL_RIDDLE, riddle);
        }

        for(String answer : context.getResources().getStringArray(R.array.answers)) {
            cv.put(COL_RIDDLE, answer);
            cv.put(COL_ANSWERED, "false");
        }

        sqlDb.insert(tableName, null, cv);
        //sqlDb.close();
    }

    public void updateScore(SQLiteDatabase sqlDb, String tableName, String username, int heroScore) {
        ContentValues values = new ContentValues();
        values.put(COL_SCORE, heroScore);
        sqlDb.update(tableName, values, COL_USER_NAME + "='" +username + "'", null);
        // sqlDb.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
    }

    public int retrieveScore(SQLiteDatabase sqlDb, Context context, String username) {
        String query = "SELECT * FROM " + TABLE_NAME1 + " WHERE " + COL_USER_NAME + " ='" + username + "'";
        Cursor cursor = sqlDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            heroScore = cursor.getInt(2);
            Toast.makeText(context, "Score is: " + heroScore, Toast.LENGTH_LONG).show();
        }
        return heroScore;
    }

    public List<Integer> retrieveScores(SQLiteDatabase sqlDb, Context context) {
        List<Integer> heroScores = new ArrayList<Integer>();
        String query = "SELECT * FROM " + TABLE_NAME1;
        Cursor cursor = sqlDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                heroScores.add(cursor.getInt(2));
            }
        }
        return heroScores;
    }

    public Map<String, String> retrieveRiddleAndAnswer(SQLiteDatabase sqlDb, int randomRowNumber) {
        Map<String, String> riddlesAndAnswers = new HashMap<String, String>();
        String query = "SELECT * FROM " + TABLE_NAME3;
        Cursor cursor = sqlDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (!Boolean.valueOf(cursor.getString(4))) {
                    riddlesAndAnswers.put(cursor.getString(2), cursor.getString(3));
                }
            }
        }
        return riddlesAndAnswers;
    }

    public List<String> retrieveUsernames(SQLiteDatabase sqlDb, Context context) {
        List<String> usernames = new ArrayList<String>();
        String query = "SELECT * FROM " + TABLE_NAME1;
        Cursor cursor = sqlDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                usernames.add(cursor.getString(1));
            }
        }
        return usernames;
    }



}
