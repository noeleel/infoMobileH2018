package com.example.pinor.inf8405runner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pinor on 2018-04-05.
 */

public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "runningInfo";

    private static final String TABLE_RESULTS = "results";
    private static final String ID_COL_NAME = "id";
    private static final String TIME_COL_NAME = "time";
    private static final String DISTANCE_COL_NAME = "distance";

    private static final String TABLE_DEVICE_INFO = "device_info";
    private static final String ID_DI_COL_NAME = "id";
    private static final String BATTERY_COL_NAME = "battery_level";
    private static final String TXDEPART_COL_NAME = "tx_depart";
    private static final String RXDEPART_COL_NAME = "rx_depart";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RESULTS_TABLE = "CREATE TABLE " + TABLE_RESULTS + " (";
        CREATE_RESULTS_TABLE += ID_COL_NAME + " INTEGER PRIMARY KEY, ";
        CREATE_RESULTS_TABLE += TIME_COL_NAME + " TEXT, ";
        CREATE_RESULTS_TABLE += DISTANCE_COL_NAME + " TEXT" + ")";
        Log.d("DB",CREATE_RESULTS_TABLE);
        db.execSQL(CREATE_RESULTS_TABLE);

        String CREATE_DEVICE_TABLE = "CREATE TABLE " + TABLE_DEVICE_INFO + " (";
        CREATE_DEVICE_TABLE += ID_DI_COL_NAME + " INTEGER PRIMARY KEY, ";
        CREATE_DEVICE_TABLE += BATTERY_COL_NAME + " INTEGER, ";
        CREATE_DEVICE_TABLE += TXDEPART_COL_NAME + " INTEGER, ";
        CREATE_DEVICE_TABLE += RXDEPART_COL_NAME + " INTEGER" + ")";
        Log.d("DB",CREATE_DEVICE_TABLE);
        db.execSQL(CREATE_DEVICE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE_INFO);
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

    public void insertDeviceInfo(DeviceInfo deviceInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(BATTERY_COL_NAME, deviceInfo.get_batteryLevel());
        row.put(TXDEPART_COL_NAME, deviceInfo.get_txDepart());
        row.put(RXDEPART_COL_NAME, deviceInfo.get_rxDepart());

        db.insert(TABLE_DEVICE_INFO, null, row);
        db.close();
    }

    public void clearAllDeviceInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteSQL = "DELETE FROM " + TABLE_DEVICE_INFO;
        db.execSQL(deleteSQL);
    }

    public DeviceInfo getDeviceInfo() {
        // List<DeviceInfo> deviceInfoList = new ArrayList<DeviceInfo>();

        String selectQuery = "SELECT * FROM " + TABLE_DEVICE_INFO;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        DeviceInfo info = new DeviceInfo();

        if (cursor.moveToFirst()) {
            //do {

            info.set_id(cursor.getInt(0));
            info.set_batteryLevel(cursor.getInt(1));
            info.set_txDepart(cursor.getInt(2));
            info.set_rxDepart(cursor.getInt(3));

            //deviceInfoList.add(info);
            //} while (cursor.moveToNext());
        }
        return info;
    }

    public void updateDeviceInfo(DeviceInfo info) {
        clearAllDeviceInfo();
        insertDeviceInfo(info);
    }
}

// http://mobilesiri.com/android-sqlite-database-tutorial-using-android-studio/