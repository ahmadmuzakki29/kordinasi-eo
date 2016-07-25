package com.muzakki.ahmad.material.form;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.muzakki.ahmad.material.model.Database;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jeki on 6/3/16.
 */
public class FormModel extends Database {

    private final String table;
    private Bundle data;

    public FormModel(Context context, String table) {
        super(context);
        this.table = table;
    }

    public String insert(String id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues val = new ContentValues();

        if (id != null) data.putString("id", id);

        for (String e : data.keySet()) {
            val.put(e, data.getString(e));
        }

        val.put("doccreate", getTimeStamp());
        long newid = db.insert(table, null, val);
        db.close();
        return String.valueOf(newid);
    }

    public String update(String id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues val = new ContentValues();

        if (id != null) data.putString("id", id);

        for (String e : data.keySet()) {
            val.put(e, data.getString(e));
        }
        db.update(table,val,"id=?",new String[]{id});
        db.close();
        return id;
    }

    public void delete(String id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table,"id=?",new String[]{id});
        db.close();
    }

    public String getTimeStamp(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public Bundle getData() {
        return data;
    }

    public Bundle select(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + table + " where id=?", new String[]{id});
        try {
            return getBundle(c).get(0);
        }catch (Exception ex){
            return null;
        }
    }

    public void setData(Bundle data){
        this.data = data;
    }
}
