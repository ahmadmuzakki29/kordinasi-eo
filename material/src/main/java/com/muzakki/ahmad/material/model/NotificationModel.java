package com.muzakki.ahmad.material.model;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.muzakki.ahmad.lib.Constant;
import com.muzakki.ahmad.lib.InternetConnection;
import com.muzakki.ahmad.lib.services.PersistentConnection;
import com.muzakki.ahmad.material.list.ListModel;

import java.util.ArrayList;

/**
 * Created by jeki on 7/14/16.
 */
public class NotificationModel extends ListModel {
    private final String table;
    private final Context ctx;

    public NotificationModel(Context context, String table) {
        super(context, table);
        this.table = table;
        this.ctx = context;
    }

    public ArrayList<Bundle> getData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from "+table+" order by datetime(time) desc",
                null);

        try{
            return getBundle(c);
        }finally {
            db.close();
            c.close();
        }
    }

    public Bundle insertData(Bundle received){
        Bundle data = new Bundle();
        data.putString("id",received.getString("id"));
        data.putString("time",received.getString("time"));

        data.putAll(received.getBundle("data"));

        SQLiteDatabase db = getWritableDatabase();
        ContentValues val = new ContentValues();
        for (String e : data.keySet()) {
            val.put(e, data.getString(e));
        }
        String id = data.getString("id");
        try {
            db.insert(table, null, val);
        }catch (Exception e){
            Log.d("jeki","id:"+id+" already there");
        }finally {
            db.close();
            ackMessage(id);
            return data;
        }
    }

    private void ackMessage(String id){
        Bundle params = new Bundle();
        params.putString("id",id);
        Intent in = new Intent(ctx, PersistentConnection.class);
        in.putExtra("url", Constant.URL_RECEIVED);
        in.putExtra("params",params);
        in.putExtra("method", InternetConnection.GET);

        ctx.startService(in);
    }
}
