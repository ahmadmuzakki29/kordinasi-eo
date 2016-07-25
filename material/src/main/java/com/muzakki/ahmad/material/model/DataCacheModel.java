package com.muzakki.ahmad.material.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by jeki on 6/15/16.
 */
public class DataCacheModel extends Database {
    private final String table;
    private String CREATOR = "";
    public DataCacheModel(Context context, String table) {
        super(context);
        this.table = table;
        CREATOR = "create table if not exists "+table+" (data text)";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(CREATOR);
    }

    public void insertData(ArrayList<Bundle> data){
        if(data.isEmpty()) return;
        emptyData();
        SQLiteDatabase db = getWritableDatabase();
        String query = "insert into "+table+" values";
        String[] values = new String[data.size()];
        int i=0;
        for(Bundle d: data){
            query += "(?),";
            values[i++] = encodeData(d);
        }
        query = query.substring(0,query.length()-1);
        db.execSQL(query,values);
        db.close();
    }

    public ArrayList<Bundle> getData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + table, null);
        ArrayList<Bundle> data = getBundle(c);
        ArrayList<Bundle> result = new ArrayList<>();
        for(Bundle d:data){
            String tmp = d.getString("data");
            result.add(decodeData(tmp));
        }
        try{
            return result;
        }finally {
            c.close();
            db.close();
        }
    }

    private String encodeData(Bundle data){
        String result = "";
        for(String key: data.keySet()){
            result += key+"|"+data.getString(key)+";";
        }
        result = result.substring(0,result.length()-1);
        return result;
    }

    private Bundle decodeData(String data){
        Bundle result = new Bundle();
        for(String d: data.split(";")){
            String[] t = d.split("\\|");
            result.putString(t[0],t[1]);
        }
        return result;
    }

    public void emptyData(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from "+ table);
        db.close();
    }
}
