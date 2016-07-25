package com.muzakki.ahmad.material.detail;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.muzakki.ahmad.material.model.Database;


/**
 * Created by jeki on 6/10/16.
 */
public class DetailModel extends Database {

    private final String table;

    public DetailModel(Context context, String table) {
        super(context);
        this.table = table;
    }

    public Bundle getDetail(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("select * from " + table + " where id=?", new String[]{id});

        try{
            return getBundle(result).get(0);
        }catch (Exception e){
            return null;
        }
    }
}
