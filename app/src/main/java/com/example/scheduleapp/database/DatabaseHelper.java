package com.example.scheduleapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ScheduleApp.db";
    private static final int DATABASE_VERSION = 1;

    // Tasks table
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_TASK_ID = "id";
    public static final String COLUMN_TASK_TITLE = "title";
    public static final String COLUMN_TASK_DESCRIPTION = "description";
    public static final String COLUMN_TASK_DATETIME = "datetime";
    public static final String COLUMN_TASK_STATUS = "status";

    // Notifications table
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COLUMN_NOTIFICATION_ID = "id";
    public static final String COLUMN_NOTIFICATION_MESSAGE = "message";
    public static final String COLUMN_NOTIFICATION_DATETIME = "datetime";

    // Create tasks table query
    private static final String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
            + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TASK_TITLE + " TEXT NOT NULL, "
            + COLUMN_TASK_DESCRIPTION + " TEXT, "
            + COLUMN_TASK_DATETIME + " TEXT NOT NULL, "
            + COLUMN_TASK_STATUS + " TEXT NOT NULL)";

    // Create notifications table query
    private static final String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
            + COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NOTIFICATION_MESSAGE + " TEXT NOT NULL, "
            + COLUMN_NOTIFICATION_DATETIME + " TEXT NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASKS_TABLE);
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

    // Task CRUD operations
    public long addTask(String title, String description, String datetime, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, title);
        values.put(COLUMN_TASK_DESCRIPTION, description);
        values.put(COLUMN_TASK_DATETIME, datetime);
        values.put(COLUMN_TASK_STATUS, status);
        return db.insert(TABLE_TASKS, null, values);
    }

    public Cursor getFutureTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS + 
                      " WHERE datetime(datetime) > datetime('now', 'localtime')" +
                      " ORDER BY datetime(datetime) ASC";
        return db.rawQuery(query, null);
    }

    public Cursor getPastTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS + 
                      " WHERE datetime(datetime) <= datetime('now', 'localtime')" +
                      " ORDER BY datetime(datetime) DESC";
        return db.rawQuery(query, null);
    }

    // Notification operations
    public long addNotification(String message, String datetime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_MESSAGE, message);
        values.put(COLUMN_NOTIFICATION_DATETIME, datetime);
        return db.insert(TABLE_NOTIFICATIONS, null, values);
    }

    public Cursor getAllNotifications() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NOTIFICATIONS, null, null, null, null, null, 
                       COLUMN_NOTIFICATION_DATETIME + " DESC");
    }
} 