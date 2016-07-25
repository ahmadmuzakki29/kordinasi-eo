package com.muzakki.ahmad.material.grid;

import android.os.Bundle;
import android.util.Log;

import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.lib.InternetConnection;
import com.muzakki.ahmad.material.model.DataCacheModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by jeki on 6/12/16.
 */
public abstract class GridRemote extends Grid implements InteractiveScrollView.OnBottomReachedListener{
    private final int LIMIT = 10;
    private final GridActivity act;
    private ArrayList<Bundle> data = new ArrayList<>();
    private DataCacheModel model;
    InternetConnection ic;
    InternetConnection icRender;
    private boolean render = false;
    private boolean loadingState,bottom = false;

    public GridRemote(GridActivity act,Listener listener) {
        super(act,listener);
        this.act = act;
        ic = new GridInternetConnection(act,this);
        icRender = new GridInternetConnectionRender(act);
        model = new DataCacheModel(act,getCacheTable());
        act.getScrollView().setOnBottomReachedListener(this);
    }

    @Override
    public void render(){
        hideInfo();
        render = true;
        act.setLoading(true);
        data = model.getData();
        if(!data.isEmpty()) {
            drawItem(data);
        }
        fetchData();
    }

    @Override
    void refresh() {
        render();
    }

    @Override
    public ArrayList<Bundle> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clearCache(){
        model.emptyData();
        removeAllViews();
    }

    protected abstract Bundle getParam();

    protected abstract String getUrl();



    private void fetchData(){
        if(loadingState) return;
        int offset = render?0:data.size();
        String url = getUrl();
        Bundle param = getParam();
        param.putString("limit", String.valueOf(LIMIT));
        param.putString("offset", String.valueOf(offset));

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideInfo();
            }
        });

        if(render){
            icRender.get(url,param);
        }else{
            ic.get(url,param);
        }

        if(!render){
            doLoading(true);
        }
    }

    @Override
    synchronized public void parseData(JSONArray result){
        int len = result!=null?result.length():0;
        bottom = len<LIMIT;
        if(len==0){
            notifyEmpty();
            return;
        }
        if(render){
            data.clear();
            removeAllViews();
        }

        try {
            ArrayList<Bundle> newData = new ArrayList<>();
            for (int i = 0; i < result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                newData.add(Helper.jsonToBundle(obj));
            }
            drawItem(newData);
            data.addAll(newData);
        }catch(JSONException e){e.printStackTrace();}

        if(render){
            model.insertData(data); // save to cache
            act.scrollToTop();
        }

        doLoading(false);

        render = false;
    }


    @Override
    void onItemClick(Bundle data) {
        Log.i("jeki",data.toString());
    }


    private class GridInternetConnectionRender extends GridInternetConnection {
        public GridInternetConnectionRender(GridActivity ctx) {
            super(ctx,GridRemote.this,false);
        }

        @Override
        protected void onTimeout() {
            super.onTimeout();
            act.setLoading(false);
            render = false;
        }
    }


    private void doLoading(final boolean state){
        loadingState= state;
        if(!state){
            act.setLoading(state);
        }
        act.setLoadingBottom(state);
    }

    @Override
    public void onBottomReached() {
        loadMore();
    }

    synchronized private void loadMore(){
        if(!bottom && !loadingState && !render) {
            fetchData();
        }
    }

    private String getCacheTable(){
        String name = act.getClass().getName();
        String[] tmp = name.split("\\.");
        return tmp[tmp.length-1];
    }
}
