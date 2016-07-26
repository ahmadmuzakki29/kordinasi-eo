package com.muzakki.ahmad.material.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jeki on 7/12/16.
 */
public class Database extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "kordinasi.db"; // change this
    private static final String[] TABLES = new String[]{
            "create table user(id integer primary key,username text," +
                    "nama text, tipe_user text)",
            "create table event(id integer primary key, nama text, tanggal text," +
                    "tempat text, guest_star text, foto text,created_by text)"
    };


    private void resetTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("drop table event");
        sqLiteDatabase.execSQL(TABLES[1]);
    }

    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(String tb: TABLES){
            sqLiteDatabase.execSQL(tb);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        resetTable(sqLiteDatabase);
        Log.i("jeki","upgrade");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetTable(db);
    }

    @NonNull
    protected ArrayList<Bundle> getBundle(Cursor c){
        ArrayList<Bundle> result = new ArrayList<>();
        if(c.getCount()>0) {
            result = new ArrayList<>();
            while (c.moveToNext()) {
                Bundle row = new Bundle();
                for(int col=0;col<c.getColumnCount();col++){
                    row.putString(c.getColumnName(col),c.getString(col));
                }
                result.add(row);
            }
        }
        return result;
    }

}
