package com.example.pinor.inf8405runner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
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

    private static final String TABLE_RUNS = "runs";
    private static final String RU_ID_COL_NAME = "id";
    private static final String RU_DATE_COL_NAME = "date";

    private static final String TABLE_LOCATIONS = "locations";
    private static final String L_ID_COL_NAME = "id";
    private static final String L_LATITUDE_COL_NAME = "latitude";
    private static final String L_LONGITUDE_COL_NAME = "longitude";
    private static final String L_RUN_ID_COL_NAME = "run_id";

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

        this.createTableRun(db);
        this.createTableLocations(db);
    }

    public void createTableRun(SQLiteDatabase db) {
        String CREATE_TABLE_RUNS = "CREATE TABLE " + TABLE_RUNS + " (";
        CREATE_TABLE_RUNS += RU_ID_COL_NAME + " INTEGER PRIMARY KEY, ";
        CREATE_TABLE_RUNS += RU_DATE_COL_NAME + " INTEGER" + ")";
        Log.d("DB",CREATE_TABLE_RUNS);
        db.execSQL(CREATE_TABLE_RUNS);
    }

    public void createTableLocations(SQLiteDatabase db) {
        String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + " (";
        CREATE_TABLE_LOCATIONS += L_ID_COL_NAME + " INTEGER PRIMARY KEY, ";
        CREATE_TABLE_LOCATIONS += L_LATITUDE_COL_NAME + " INTEGER, ";
        CREATE_TABLE_LOCATIONS += L_LONGITUDE_COL_NAME + " INTEGER, ";
        CREATE_TABLE_LOCATIONS += L_RUN_ID_COL_NAME + " INTEGER" + ")";
        Log.d("DB",CREATE_TABLE_LOCATIONS);
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUNS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    private boolean checkIfTableExists(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT DISTINCT name FROM sqlite_master WHERE name='" + tableName + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
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

    public void insertRun(Run run) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (!checkIfTableExists(TABLE_RUNS)) {
            createTableRun(db);
        }

        ContentValues row = new ContentValues();
        row.put(RU_DATE_COL_NAME, run.get_date().getTime());
        long runID = db.insert(TABLE_RUNS, null, row);

        for (int i = 0; i < run.get_Points().size(); i++) {
            insertLocation(runID, run.get_Points().get(i));
        }

        db.close();
    }

    public void insertLocation(long runID, Location location) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (!checkIfTableExists(TABLE_LOCATIONS)) {
            createTableLocations(db);
        }

        ContentValues row = new ContentValues();
        row.put(L_LATITUDE_COL_NAME, location.getLatitude());
        row.put(L_LONGITUDE_COL_NAME, location.getLongitude());
        row.put(L_RUN_ID_COL_NAME, runID);
        db.insert(TABLE_LOCATIONS, null, row);
        db.close();
    }

    public ArrayList<Run> getRunInstance() {
        String selectQuery = "SELECT * FROM " + TABLE_RUNS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Run> runs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Run run = new Run();
                run.set_id(cursor.getInt(0));

                Date date = new Date(cursor.getLong(1));
                run.set_date(date);

                runs.add(run);
            } while (cursor.moveToNext());
        }
        return runs;
    }

    public ArrayList<Location> getLocations(int runID) {
        String selectQuery = "SELECT * FROM " + TABLE_LOCATIONS;
        selectQuery += " WHERE " + L_RUN_ID_COL_NAME + "=" + runID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Location> locations = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Location location = new Location("");
                location.setLatitude(cursor.getDouble(1));
                location.setLongitude(cursor.getDouble(2));

                locations.add(location);
            } while (cursor.moveToNext());
        }
        return locations;
    }

    public void clearAllLocationsAndRuns() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteSQL = "DELETE FROM " + TABLE_RUNS;

        if (checkIfTableExists(TABLE_RUNS)) {
            db.execSQL(deleteSQL);
        }

        if (checkIfTableExists(TABLE_LOCATIONS)) {
            deleteSQL = "DELETE FROM " + TABLE_LOCATIONS;
            db.execSQL(deleteSQL);
        }
    }
}

// http://mobilesiri.com/android-sqlite-database-tutorial-using-android-studio/