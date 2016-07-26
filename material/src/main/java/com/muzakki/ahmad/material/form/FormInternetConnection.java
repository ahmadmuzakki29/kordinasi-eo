package com.muzakki.ahmad.material.form;

import android.content.Context;
import android.os.Bundle;

import com.muzakki.ahmad.lib.InternetConnection;

import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONObject;

/**
 * Created by jeki on 6/3/16.
 */
public class FormInternetConnection extends InternetConnection {
    private final Bundle data;
    private Listener listener;

    public FormInternetConnection(Context ctx, Bundle b, Listener listener) {
        super(ctx);
        this.data = b;
        this.listener = listener;
    }

    @Override
    protected void onSuccess(JSONObject result) {
        Bundle newData = new Bundle();
        if(data!=null){
            for(String key:data.keySet()){
                newData.putString(key,data.getBundle(key).getString("value"));
            }
        }
        listener.onServerSuccess(result, newData);
    }

    public void insert() {
        String url = getInsertUrl();
        postMultiPart(url, data);
    }

    public String getTable(){
        throw new UnsupportedOperationException("table");
    }

    protected String getInsertUrl(){
        throw new NotImplementedException("Insert Url not Implemented");
    }

    protected String getUpdateUrl(String id){
        throw new NotImplementedException("Update Url not Implemented");
    }

    public void update(String id) {
        String url = getUpdateUrl(id);
        postMultiPart(url, data);
    }

    protected String getDeleteUrl(String id){
        throw new UnsupportedOperationException("Delete Url not Implemented");
    }

    public void delete(String id){
        String url = getDeleteUrl(id);
        get(url);
    }

    @Override
    protected void onTimeout() {
        listener.onTimeout();
    }

    public interface Listener {
        void onTimeout();
        void onServerSuccess(JSONObject result, Bundle data);
    }
}
