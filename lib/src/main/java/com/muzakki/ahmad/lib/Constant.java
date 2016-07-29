package com.muzakki.ahmad.lib;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

/**
 * Created by jeki on 7/12/16.
 */
public class Constant {

    public static final int TIMEOUT = 5000;



    public static HashMap<String,String> getHeaders(){
        HashMap<String,String> headers = new HashMap<>();
        headers.put("Authorization","9k758L079oyksTHwjgxtXOzIMq49h64EMiOG2z9");
        return headers;
    }

    public static final String HOST = "http://aldrivr.tk";

    public static final String URL_LOGIN = HOST+"/login";

    public static final String URL_TOKEN = HOST+"/device/token";

    public static final String URL_RECEIVED = HOST+"/message/received";
    public static final String URL_USER_LIST = HOST + "/get/user";
    public static final String URL_USER_ADD = HOST + "/insert/user";


    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "kordinasi.db";
    public static final String[] TABLES = new String[]{
            "create table user(id integer primary key,username text," +
                    "nama text, tipe_user text)",
            "create table event(id integer primary key, nama text, tanggal text," +
                    "tempat text, guest_star text, foto text,created_by text)",
            "create table job(id integer primary key, event integer,nama text, tugas text," +
                    "komentar text,status text)"
    };

    public static void resetTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("drop table job");
        sqLiteDatabase.execSQL(TABLES[2]);
    }
}
