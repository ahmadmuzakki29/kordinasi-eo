package com.muzakki.ahmad.material.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jeki on 6/8/16.
 */
public class ImageCacheModel extends Database {
    String tableName = "image_cache";
    public ImageCacheModel(Context context) {
        super(context);
    }

    public String getImageName(String url){
        SQLiteDatabase db = getReadableDatabase();
        String query = "select path from "+tableName+" where url=?";
        Cursor c = db.rawQuery(query, new String[]{url});

        try{
            return getBundle(c).get(0).getString("path");
        }catch (Exception e){
            return null;
        }finally {
            c.close();
            db.close();
        }
    }

    public void saveImage(String url,String path){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("url",url);
        cv.put("path",path);

        try{
            db.insert(tableName,null,cv);
        }catch (Exception e){
            db.update(tableName,cv,"url=?",new String[]{"url"});
        }finally {
            db.close();
        }
    }
}
