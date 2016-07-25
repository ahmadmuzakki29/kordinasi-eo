package com.muzakki.ahmad.material.detail;

import android.os.Bundle;
import android.util.Log;

import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.lib.InternetConnection;
import com.muzakki.ahmad.material.form.Fields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jeki on 6/20/16.
 */
public abstract class DetailRemote extends Detail {
    private final Fields fields;
    private final DetailActivity ctx;
    private Bundle cache;
    private InternetConnection ic;

    public DetailRemote(DetailActivity ctx, String id){
        this(ctx,id,null);
    }

    public DetailRemote(DetailActivity ctx, String id, Fields fields){
        super(ctx,id);
        this.ctx = ctx;
        this.fields = fields;
        ic = new InternetConnection(ctx) {
            @Override
            protected void onSuccess(JSONObject result) {
                parseData(Helper.getJSONData(result));
            }
        };
    }

    public void render(Bundle cache){
        this.cache = cache;
    }

    @Override
    public void render() {
        removeAllViews();
        fetch();
    }

    private void fetch(){
        Log.i("jeki",getUrl());
        ic.get(getUrl());
    }

    private void parseData(JSONArray result) {
        try {
            JSONObject obj = result.getJSONObject(0);
            Bundle data = Helper.jsonToBundle(obj);
            initComponent(fields,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected abstract String getUrl();
}
