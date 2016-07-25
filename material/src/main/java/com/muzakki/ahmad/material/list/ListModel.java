package com.muzakki.ahmad.material.list;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.muzakki.ahmad.material.model.Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jeki on 6/4/16.
 */
public class ListModel extends Database {
    private final String table;

    public ListModel(Context context, String table) {
        super(context);
        this.table = table;
    }

    public ArrayList<Bundle> getData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from "+table+" order by id",null);

        try{
            return getBundle(c);
        }finally {
            db.close();
            c.close();
        }
    }

    public void insertData(ArrayList<JSONObject> data){
        ArrayList<Bundle> value = parseData(data);
        SQLiteDatabase db = getWritableDatabase();

        for(Bundle d: value){
            ContentValues val = new ContentValues();
            for (String e : d.keySet()) {
                val.put(e, d.getString(e));
            }
            db.insert(table,null,val);
        }


        db.close();
    }

    protected ArrayList<Bundle> parseData(ArrayList<JSONObject> data){
        ArrayList<Bundle> result = new ArrayList<>();
        try {
            for(JSONObject obj: data){
                Bundle row = new Bundle();
                Iterator<String> iter = obj.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    row.putString(key,obj.getString(key));
                }
                result.add(row);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void emptyData(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from "+ table);
        db.close();
    }
}
