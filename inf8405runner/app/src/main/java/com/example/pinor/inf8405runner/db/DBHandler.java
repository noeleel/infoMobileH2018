package com.example.pinor.inf8405runner.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pinor on 2018-04-05.
 */

public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "runningInfo";
    // Contacts table name
    private static final String TABLE_RESULTS = "results";
    // Shops Table Columns names

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SH_ADDR = "shop_address";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE" + TABLE_RESULTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_SH_ADDR + "TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
// Creating tables again
        onCreate(db);
    }
}

// http://mobilesiri.com/android-sqlite-database-tutorial-using-android-studio/