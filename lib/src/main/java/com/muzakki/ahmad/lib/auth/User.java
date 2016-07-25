package com.muzakki.ahmad.lib.auth;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jeki on 7/12/16.
 */
public class User {

    public static final String PREF_USER = "com.muzakki.ahmad.Auth.User";
    private static User user;
    private String username,displayName,photoUrl;

    User(Context ctx){
        SharedPreferences sharedPref = ctx.getSharedPreferences(
                PREF_USER, Context.MODE_PRIVATE);
        username = sharedPref.getString("username",null);
        displayName = sharedPref.getString("displayName",null);
        photoUrl = sharedPref.getString("photoUrl",null);
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }


    public static User getInstance(Context ctx){
        if(! isAvailable(ctx)) return null;
        if(user==null) user = new User(ctx);
        return user;
    }

    private static boolean isAvailable(Context ctx){
        SharedPreferences sharedPref = ctx.getSharedPreferences(
                PREF_USER, Context.MODE_PRIVATE);
        return sharedPref.getString("username",null)!=null;
    }

    public static void setUser(Context ctx,String username,String displayName,String photoUrl){
        SharedPreferences sharedPref = ctx.getSharedPreferences(
                PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username",username);
        editor.putString("displayName",displayName);
        editor.putString("photoUrl",photoUrl);
        editor.apply();
    }

    public void signOut(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(
                PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
