package com.example.pinor.inf8405runner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pinor on 2018-04-05.
 */

public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "runningInfo";

    private static final String TABLE_RESULTS = "results";
    private static final String ID_COL_NAME = "id";
    private static final String TIME_COL_NAME = "time";
    private static final String DISTANCE_COL_NAME = "distance";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE" + TABLE_RESULTS + "(";
        CREATE_CONTACTS_TABLE += ID_COL_NAME + " INTEGER PRIMARY KEY,";
        CREATE_CONTACTS_TABLE += TIME_COL_NAME + " TEXT,";
        CREATE_CONTACTS_TABLE += DISTANCE_COL_NAME + "TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        onCreate(db);
    }

    public void insertResult(Result result) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(TIME_COL_NAME, result.get_time());
        row.put(DISTANCE_COL_NAME, result.get_distance());

        db.insert(TABLE_RESULTS, null, row);
        db.close();
    }

    public List<Result> getAllResults() {

        List<Result> resultsList = new ArrayList<Result>();

        String selectQuery = "SELECT * FROM " + TABLE_RESULTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Result result = new Result();
                result.set_id(cursor.getInt(0));
                result.set_time(cursor.getInt(1));
                result.set_distance(cursor.getInt(2));

                resultsList.add(result);
            } while (cursor.moveToNext());
        }
        return resultsList;
    }
}

// http://mobilesiri.com/android-sqlite-database-tutorial-using-android-studio/