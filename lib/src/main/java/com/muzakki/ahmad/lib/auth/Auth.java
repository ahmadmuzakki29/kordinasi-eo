package com.muzakki.ahmad.lib.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.lib.InternetConnection;
import com.muzakki.ahmad.lib.services.PersistentConnection;
import com.muzakki.ahmad.lib.Constant;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by jeki on 7/12/16.
 */
public class Auth{
    public static final String TOKEN_SAVED = "TOKEN_SAVED";
    private static final String IS_LOGIN = "IS_LOGIN";
    private static Auth auth;
    private final Context ctx;
    private Listener listener;
    private FirebaseInstanceId iid;

    private Auth(Context ctx){
        iid = FirebaseInstanceId.getInstance();
        this.ctx = ctx;
    }

    public String getIID() {
        return iid.getId();
    }

    public void signIn(String username, String password, Listener listener){
        this.listener = listener;
        Log.i("jeki",username+" : "+password);

        Bundle params = new Bundle();
        params.putString("username",username);
        params.putString("password",password);
        params.putString("instance_id",iid.getId());
        new LoginIC(ctx).post(Constant.URL_LOGIN,params);
    }

    private class LoginIC extends InternetConnection {
        LoginIC(Context ctx){
            super(ctx);
        }

        @Override
        protected void onSuccess(JSONObject result) {
            try {
                if(!result.getBoolean("success")){
                    listener.onLoginFailure();
                }else{
                    Auth.this.onSuccess(result.getJSONObject("data"));
                    listener.onLoginSuccess();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("jeki",result.toString());
                listener.onConnectionFailure();
            }
        }

        @Override
        protected void onTimeout() {
            listener.onConnectionFailure();
        }
    }

    public static Auth getInstance(Context ctx){
        if(auth==null) auth = new Auth(ctx);
        return auth;
    }

    public User getCurrentUser(){
        return User.getInstance(ctx);
    }

    private void onSuccess(JSONObject data) {
        try {
            User.setUser(ctx,data.getString("username"),data.getString("nama"),
                    data.getString("photoUrl"));
            setLogin(true);
            saveToken();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getToken(){
        if(!isTokenSaved()) saveToken();
        return iid.getToken();
    }

    private void setLogin(boolean is_login){
        Helper.setPref(ctx,IS_LOGIN,is_login);
    }

    private boolean isLogin(){
        return Helper.getPrefBoolean(ctx, IS_LOGIN);
    }

    public void saveToken(){
        Helper.setPref(ctx, TOKEN_SAVED,false);
        String token = iid.getToken();
        if(!isLogin()||token==null) return;

        Bundle params = new Bundle();
        params.putString("instance_id",iid.getId());
        params.putString("token",token);

        Intent in = new Intent(ctx, PersistentConnection.class);
        in.putExtra("method",InternetConnection.POST);
        in.putExtra("url",Constant.URL_TOKEN);
        in.putExtra("params",params);
        ctx.startService(in);
    }

    private boolean isTokenSaved(){
        return Helper.getPrefBoolean(ctx,TOKEN_SAVED);
    }

    public void signOut(){
        if(getCurrentUser()==null) return;
        Helper.clearPref(ctx);

        getCurrentUser().signOut(ctx);
    }

    public interface Listener{
        void onLoginSuccess();
        void onLoginFailure();
        void onConnectionFailure();
    }
}
