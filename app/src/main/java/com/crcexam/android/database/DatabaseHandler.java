package com.crcexam.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "CrcExam";

    // Contacts table name
    private static final String TABLE_FEED = "feed";
    private static final String TABLE_SAMPLE_QUIZ = "sample_quiz";
    private static final String TABLE_MISSED_QUESTION = "missed_question";


    private static final String KEY_ID_QUIZ = "id";
    private static final String KEY_IS_COMPLETED = "is_completed";
    private static final String KEY_QUESTION_TYPE = "question_type";
    private static final String KEY_QUESTIONS = "questions";
    private static final String KEY_CURRECT_ANS = "currect_ans";
    private static final String KEY_YOUR_ANS = "your_ans";
    private static final String KEY_EXPLAINATION = "explaination";
    private static final String KEY_IS_MISSED = "is_missed";


    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_TIME = "time";
    private static final String KEY_FEED_TYPE = "feed_type";
    private static final String KEY_FEED_ID = "feed_ids";
    private static final String KEY_URL = "url";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FEED + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT," + KEY_TIME + " TEXT,"
                + KEY_FEED_TYPE + " TEXT," + KEY_FEED_ID + " TEXT,"
                + KEY_URL + " TEXT" + ")";

        String CREATE_QUESTION_TABLE = "CREATE TABLE " + TABLE_SAMPLE_QUIZ + "("
                + KEY_ID_QUIZ + " INTEGER PRIMARY KEY," + KEY_IS_COMPLETED + " TEXT,"
                + KEY_QUESTION_TYPE + " TEXT," + KEY_QUESTIONS + " TEXT," + KEY_CURRECT_ANS + " TEXT," + KEY_YOUR_ANS + " TEXT," + KEY_IS_MISSED + " TEXT," + KEY_EXPLAINATION + " TEXT"
                + ")";
        String CREATE_MISSED_QUESTION_TABLE = "CREATE TABLE " + TABLE_MISSED_QUESTION + "("
                + KEY_ID_QUIZ + " INTEGER PRIMARY KEY," + KEY_QUESTIONS + " TEXT"
                + ")";
        db.execSQL(CREATE_MISSED_QUESTION_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_QUESTION_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAMPLE_QUIZ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MISSED_QUESTION);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addFeedLogs(String latitude, String longitude, String time, String type, String ids, String url) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, latitude); // Contact Name
        values.put(KEY_LONGITUDE, longitude); // Contact Phone
        values.put(KEY_TIME, time); // Contact Phone
        values.put(KEY_FEED_TYPE, type); // Contact Phone
        values.put(KEY_FEED_ID, ids); // Contact Phone
        values.put(KEY_URL, url); // Contact Phone

        // Inserting Row
        db.insert(TABLE_FEED, null, values);
        db.close(); // Closing database connection
    }


    public void addQuestions(String is_completed, String type, String question, String your_ans, String currect_ans, String expl, String is_missed) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IS_COMPLETED, is_completed); // Contact Name
        values.put(KEY_QUESTION_TYPE, type); // Contact Phone
        values.put(KEY_QUESTIONS, question); // Contact Phone
        values.put(KEY_CURRECT_ANS, currect_ans); // Contact Phone
        values.put(KEY_YOUR_ANS, your_ans); // Contact Phone
        values.put(KEY_IS_MISSED, is_missed); // Contact Phone
        values.put(KEY_EXPLAINATION, expl); // Contact Phone
        // Inserting Row
        db.insert(TABLE_SAMPLE_QUIZ, null, values);
        db.close(); // Closing database connection
    }

    public void addMissedQuestions(String question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUESTIONS, question); // Contact Phone
        // Inserting Row
        db.insert(TABLE_MISSED_QUESTION, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<JSONObject> getAllQuestions() {
        ArrayList<JSONObject> lstFeedLogs = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SAMPLE_QUIZ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.putOpt("id", "" + Integer.parseInt(cursor.getString(0)));
                    jsonObject.putOpt("is_completed", "" + cursor.getString(1));
                    jsonObject.putOpt("question_type", "" + cursor.getString(2));
                    JSONObject jsonObject1 = new JSONObject(cursor.getString(3));

                    jsonObject.putOpt("questions", jsonObject1);
                    jsonObject.putOpt(KEY_CURRECT_ANS, "" + cursor.getString(4));
                    jsonObject.putOpt(KEY_YOUR_ANS, "" + cursor.getString(5));
                    jsonObject.putOpt(KEY_IS_MISSED, "" + cursor.getString(6));
                    jsonObject.putOpt(KEY_EXPLAINATION, "" + cursor.getString(7));

                    Log.e("question from db ", jsonObject + "");
                    lstFeedLogs.add(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return lstFeedLogs;
    }

    public ArrayList<JSONObject> getAllMissedQuestionsList() {
        ArrayList<JSONObject> lstFeedLogs = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SAMPLE_QUIZ + " WHERE is_missed='true'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.putOpt("id", "" + Integer.parseInt(cursor.getString(0)));
                    jsonObject.putOpt("is_completed", "" + cursor.getString(1));
                    jsonObject.putOpt("question_type", "" + cursor.getString(2));
                    JSONObject jsonObject1 = new JSONObject(cursor.getString(3));

                    jsonObject.putOpt("questions", jsonObject1);
                    jsonObject.putOpt(KEY_CURRECT_ANS, "" + cursor.getString(4));
                    jsonObject.putOpt(KEY_YOUR_ANS, "" + cursor.getString(5));
                    jsonObject.putOpt(KEY_IS_MISSED, "" + cursor.getString(6));
                    jsonObject.putOpt(KEY_EXPLAINATION, "" + cursor.getString(7));

                    Log.e("question from db ", jsonObject + "");
                    lstFeedLogs.add(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
       // cursor.close();
       // db.close();

        return lstFeedLogs;
    }


    public ArrayList<JSONObject> getAllMissedQuestions() {
        ArrayList<JSONObject> lsMissedQst = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MISSED_QUESTION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.putOpt("id", "" + Integer.parseInt(cursor.getString(0)));
                    jsonObject.putOpt("questions", "" + cursor.getString(1));
                    Log.e("mi question from db ", jsonObject + "");
                    lsMissedQst.add(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lsMissedQst;
    }


    // Deleting single contact
    public void removeOrderFromDB(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEED, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateQuestionData(String id, String is_completed, String your_ans, String is_missed) {
        Log.e("your_ans  bef ", your_ans + "");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_COMPLETED, is_completed); // Contact Name
        values.put(KEY_YOUR_ANS, your_ans); // Contact Phone
        values.put(KEY_IS_MISSED, is_missed); // Contact Phone
        db.update(TABLE_SAMPLE_QUIZ, values, "id=" + id, null);
        db.close();
        //  Log.e("dfgfdg  aft ", getAllMissedQuestions().size() + "");
    }

    public void deleteMissingQuestion(String id) {

        Log.e("dfgfdg  bef ", getAllMissedQuestions().size() + "");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MISSED_QUESTION, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        Log.e("dfgfdg  aft ", getAllMissedQuestions().size() + "");
    }

    public void clearAllQuestion() {

        Log.e("clearAllQuestion  bef ", getAllQuestions().size() + "");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SAMPLE_QUIZ);

        db.close();
        Log.e("clearAllQuestion  aft ", getAllQuestions().size() + "");
    }
}
