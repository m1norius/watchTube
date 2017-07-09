package com.minorius.watchertube.dbtube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.minorius.watchertube.ViewElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by minorius on 03.07.2017.
 */

public class SQLTubeHelper extends SQLiteOpenHelper {

    public static final String TABLE_1 = "proHiTech";
    public static final String TABLE_2 = "mobileDevices";
    public static final String TABLE_3 = "MKS";

    private static final String DB_NAME = "mainDb2.db";

    public SQLTubeHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table proHiTech ("
                + "id integer primary key autoincrement,"
                + "title text, "
                + "description text, "
                + "imageName text, "
                + "duration text, "
                + "videoName text "+ ");");

        db.execSQL("create table mobileDevices ("
                + "id integer primary key autoincrement,"
                + "title text, "
                + "description text, "
                + "imageName text, "
                + "duration text, "
                + "videoName text "+ ");");

        db.execSQL("create table MKS ("
                + "id integer primary key autoincrement,"
                + "title text, "
                + "description text, "
                + "imageName text, "
                + "duration text, "
                + "videoName text "+ ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase getConnection(){
        return this.getWritableDatabase();
    }

    public long insertToDb(SQLiteDatabase db, String table, String title, String description, String imageName, String duration, String videoName){
        ContentValues contentValues = new ContentValues();

        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("imageName", imageName);
        contentValues.put("duration", duration);
        contentValues.put("videoName", videoName);

        long rowID = db.insert(table, null, contentValues);
        db.close();
        return rowID;
    }

    public HashMap<String, ViewElement> getAllDataFromDb(final SQLiteDatabase db, final String table){

        final HashMap<String, ViewElement> viewElements = new HashMap<>();

        Cursor c = db.query(table, null, null, null, null, null, null);
        if (c.moveToFirst()) {

            int title = c.getColumnIndex("title");
            int description = c.getColumnIndex("description");
            int imageName = c.getColumnIndex("imageName");
            int duration = c.getColumnIndex("duration");
            int videoName = c.getColumnIndex("videoName");

            do {
                viewElements.put(c.getString(imageName), new ViewElement(c.getString(title), c.getString(description), c.getString(videoName), c.getString(imageName), c.getString(duration)));
            } while (c.moveToNext());
        } else
            Log.d("SQLTubeHelper_LOG", "0 rows");
        c.close();

        return viewElements;
    }
}
