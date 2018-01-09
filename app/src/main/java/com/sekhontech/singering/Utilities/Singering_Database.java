package com.sekhontech.singering.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sekhontech.singering.Activities.Login;
import com.sekhontech.singering.R;

/**
 * Created by ST_003 on 17-06-2016.
 */
public class Singering_Database extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Singering_DB2";
    // TABLE FOR FRIENDLIST
    private static final String TABLE_NAME_COUNT = "tablecount";
    private static final String PLAYLIST_ID1 = "playlist_id";
    private static final String PLAYLIST_NAME = "name";
    private static final String PLAYLIST_IMAGE = "image";
    private static final String TABLE_NAME_FOLLOW = "followtable";
    private static final String FOLLOW_ID = "follow_id";
    private static final String FOLLOW_STATUS = "follow_status";
    private static final String TABLE_NAME_LIKE = "liketable";
    private static final String TRACK_ID = "track_id";
    private static final String LIKE_STATUS = "like_status";
    SQLiteDatabase db;
    Cursor cursor;

    public Singering_Database(Context context) {
        super(context, Login.path + context.getString(R.string.app_name), null,
                DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_SELANS_TABLE = "CREATE TABLE " + TABLE_NAME_LIKE + "("
                + TRACK_ID + " TEXT , " + LIKE_STATUS + " TEXT " + ")";
        db.execSQL(CREATE_SELANS_TABLE);

        String CREATE_FOLLOW_TABLE = "CREATE TABLE " + TABLE_NAME_FOLLOW + "("
                + FOLLOW_ID + " TEXT , " + FOLLOW_STATUS + " TEXT " + ")";
        db.execSQL(CREATE_FOLLOW_TABLE);

        //   String CREATE_TRACK_COUNT ="CREATE TABLE " + TABLE_NAME_COUNT + "("
        //          + PLAYLIST_ID1 + " TEXT , " + PLAYLIST_NAME + " TEXT , " + PLAYLIST_IMAGE + " TEXT " + ")";
        //  db.execSQL(CREATE_TRACK_COUNT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME_LIKE);

        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME_FOLLOW);

        //  db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME_COUNT);
        // onCreate(db);
    }


    public void insertFollow(String Follow_id, String check) {
        db = this.getWritableDatabase();
        ContentValues cv1 = new ContentValues();
        cv1.put(FOLLOW_ID, Follow_id);
        cv1.put(FOLLOW_STATUS, check);

        db.insert(TABLE_NAME_FOLLOW, null, cv1);
        System.out.println("----data inserted followers----");
        db.close();
    }


    public String get_Follow(String Follow_id) {
        String check = "";
        db = this.getReadableDatabase();
        String sq121 = "SELECT * from followtable where follow_id='" + Follow_id + "'";
        Cursor cursor1 = db.rawQuery(sq121, null);
        if (cursor1.moveToFirst()) {
            do {
                check = cursor1.getString(1);
            } while (cursor1.moveToNext());
        }
        db.close();
        return check;
    }

    public void deleteFollow() {
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME_FOLLOW, null, null);
    }

    public void updateFollow(String Follow_id, String check) {
        db = this.getWritableDatabase();
        ContentValues cv1 = new ContentValues();
        // cv.put(NOTI_ID,noti.getId());

        cv1.put(FOLLOW_STATUS, check);

        db.update(TABLE_NAME_FOLLOW, cv1, "follow_id=" + Follow_id, null);
        System.out.println("----data inserted----");
        db.close();
    }


    public void insertLikes(String Track_id, String check) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        // cv.put(NOTI_ID,noti.getId());
        cv.put(TRACK_ID, Track_id);
        cv.put(LIKE_STATUS, check);

        db.insert(TABLE_NAME_LIKE, null, cv);
        System.out.println("----data inserted likes----");
        db.close();
    }


    public String get_like(String track_id) {
        String check = "";
        db = this.getReadableDatabase();
        String sql12 = "SELECT * from liketable where track_id='" + track_id + "'";
        Cursor cursor = db.rawQuery(sql12, null);
        if (cursor.moveToFirst()) {
            do {
                check = cursor.getString(1);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        // return contact list
        return check;
    }


    public void deleteLikes() {
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME_LIKE, null, null);
    }

    public boolean tableexist(String tablename) {
        try {

        } catch (SQLiteCantOpenDatabaseException e) {
        }

        db = this.getReadableDatabase();
        String sql12 = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tablename + "'";
        cursor = db.rawQuery(sql12, null);
        if (cursor.getCount() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void updateLikes(String Track_id, String check) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        // cv.put(NOTI_ID,noti.getId());
        cv.put(LIKE_STATUS, check);
        db.update(TABLE_NAME_LIKE, cv, "track_id=" + Track_id, null);
        System.out.println("----data inserted----");
        db.close();
    }


}
