package com.example.personalhealthmonitor.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Database extends SQLiteOpenHelper {

    private static Database instance;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "reports";
    private static final String KEY_ID = "id";

    private static final String TABLE_REPORT = "reports";
    private static final String KEY_DATE = "date";
    private static final String KEY_BODY_TEMPERATURE = "bodyTemperature";
    private static final String KEY_GLYCEMIC_INDEX = "glicemicIndex";
    private static final String KEY_DIASTOLIC_PRESSURE = "diastolicPressure";
    private static final String KEY_SYSTOLIC_PRESSURE = "sistolicPressure";
    private static final String KEY_PERSONAL_COMMENT = "personalComment";
    private static final String KEY_RATING = "rating";

    private static final String TABLE_THRESHOLD = "thresholds";
    private static final String KEY_TYPE = "type";
    private static final String KEY_MAX = "maximum";
    private static final String KEY_MIN = "minimum";
    private static final String KEY_MONITORED = "monitored";

    private static final String TABLE_SETTINGS = "settings";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_HOUR = "hour";

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Creo le tabelle
        db.execSQL("CREATE TABLE " + TABLE_REPORT + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DATE + " LONG,"
                + KEY_BODY_TEMPERATURE + " DOUBLE,"
                + KEY_GLYCEMIC_INDEX + " INT,"
                + KEY_DIASTOLIC_PRESSURE + " INT,"
                + KEY_SYSTOLIC_PRESSURE + " INT,"
                + KEY_PERSONAL_COMMENT + " TEXT,"
                + KEY_RATING + " INT"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_THRESHOLD + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TYPE + " TEXT,"
                + KEY_MAX + " DOUBLE,"
                + KEY_MIN + " DOUBLE,"
                + KEY_MONITORED + " INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_SETTINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NOTIFICATION + " INTEGER,"
                + KEY_HOUR + " DOUBLE"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_THRESHOLD);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(sqLiteDatabase);
    }

    public static Database getDB(Context context) {
        if (instance == null) instance = new Database(context);
        return instance;
    }

    public void addReport(Report report) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, report.getDate());
        values.put(KEY_BODY_TEMPERATURE, report.getBodyTemperature());
        values.put(KEY_GLYCEMIC_INDEX, report.getGlicemicIndex());
        values.put(KEY_SYSTOLIC_PRESSURE, report.getSystolicPressure());
        values.put(KEY_DIASTOLIC_PRESSURE, report.getDiastolicPressure());
        values.put(KEY_PERSONAL_COMMENT, report.getComment());
        values.put(KEY_RATING, report.getRating());

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            db.insertOrThrow(TABLE_REPORT, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add report to database");
        } finally {
            db.endTransaction();
        }
    }

    public void addThreshold(Threshold threshold) {
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, threshold.getType());
        values.put(KEY_MIN, threshold.getMin());
        values.put(KEY_MAX, threshold.getMax());
        values.put(KEY_MONITORED, threshold.isMonitored());

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            db.insertOrThrow(TABLE_THRESHOLD, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add report to database");
        } finally {
            db.endTransaction();
        }
    }

    public void addSettings(Settings settings){
        ContentValues values = new ContentValues();
        values.put(KEY_NOTIFICATION, settings.getNotifications());
        values.put(KEY_HOUR, settings.getHour());

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            db.insertOrThrow(TABLE_SETTINGS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add report to database");
        } finally {
            db.endTransaction();
        }
    }

    public List<Report> getReports(long fromDate, long toDate) {

        List<Report> reports = new ArrayList<>();

        String POSTS_SELECT_QUERY = "SELECT * FROM " + TABLE_REPORT + " WHERE " + KEY_DATE + " >= " + "'" + fromDate + "' AND " + KEY_DATE + " <=" +
                " " + "'" + toDate + "'" + " ORDER BY " + KEY_DATE;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    reports.add(
                            new Report(
                                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                                    cursor.getLong(cursor.getColumnIndex(KEY_DATE)),
                                    cursor.getDouble(cursor.getColumnIndex(KEY_BODY_TEMPERATURE)),
                                    cursor.getInt(cursor.getColumnIndex(KEY_SYSTOLIC_PRESSURE)),
                                    cursor.getInt(cursor.getColumnIndex(KEY_DIASTOLIC_PRESSURE)),
                                    cursor.getInt(cursor.getColumnIndex(KEY_GLYCEMIC_INDEX)),
                                    cursor.getString(cursor.getColumnIndex(KEY_PERSONAL_COMMENT)),
                                    cursor.getInt(cursor.getColumnIndex(KEY_RATING))
                            )
                    );
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get reports from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }

        return reports;
    }

    public List<Threshold> getThresholds() {

        List<Threshold> thresholds = new ArrayList<>();

        String POSTS_SELECT_QUERY = "SELECT * FROM " + TABLE_THRESHOLD;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    thresholds.add(
                            new Threshold(
                                    cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                                    cursor.getDouble(cursor.getColumnIndex(KEY_MIN)),
                                    cursor.getDouble(cursor.getColumnIndex(KEY_MAX)),
                                    cursor.getInt(cursor.getColumnIndex(KEY_MONITORED)) == 1
                            )
                    );
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get reports from database");
        } finally {
            if (!cursor.isClosed()) cursor.close();
        }

        return thresholds;
    }

    public Settings getSettings(){

        Settings settings = null;
        String POSTS_SELECT_QUERY = "SELECT * FROM " + TABLE_SETTINGS;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                settings = new Settings(
                    cursor.getInt(cursor.getColumnIndex(KEY_NOTIFICATION)) == 1,
                    cursor.getInt(cursor.getColumnIndex(KEY_HOUR))
                );
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get reports from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }

        return settings;
    }

    public void editSettings(Settings settings) {

        ContentValues values = new ContentValues();
        values.put(KEY_NOTIFICATION, settings.getNotifications());
        values.put(KEY_HOUR, settings.getHour());

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_SETTINGS, values, null, null);
    }

    public void removeReport(Report report) {
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TABLE_REPORT, KEY_ID + "=?", new String[]{String.valueOf(report.getId())});
    }

    public void editReport(Report report) {

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, report.getDate());
        values.put(KEY_BODY_TEMPERATURE, report.getBodyTemperature());
        values.put(KEY_GLYCEMIC_INDEX, report.getGlicemicIndex());
        values.put(KEY_SYSTOLIC_PRESSURE, report.getSystolicPressure());
        values.put(KEY_DIASTOLIC_PRESSURE, report.getDiastolicPressure());
        values.put(KEY_PERSONAL_COMMENT, report.getComment());
        values.put(KEY_RATING, report.getRating());

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_REPORT, values, KEY_ID + "=?", new String[]{String.valueOf(report.getId())});
    }

    public void editThreshold(Threshold threshold) {

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, threshold.getType());
        values.put(KEY_MIN, threshold.getMin());
        values.put(KEY_MAX, threshold.getMax());
        values.put(KEY_MONITORED, threshold.isMonitored());

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_THRESHOLD, values, KEY_TYPE + "=?", new String[]{threshold.getType()});
    }
}
