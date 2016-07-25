package com.muzakki.ahmad.material.list;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jeki on 6/3/16.
 */
public abstract class ListLocal extends List{
    private final Activity act;
    private final ListInternetConnection ic;
    protected final Listener listener;

    private ListModel model;

    public ListLocal(Activity act,Listener listener) {
        super(act,listener);
        this.act = act;
        this.listener = listener;
        ic = getListInternetConnection(act,this);
    }

    public void render(){
        getEmptyView().setVisibility(GONE);
        hideInfo();
        model  = getListModel();
        ArrayList<Bundle> data = model.getData();
        if(!data.isEmpty()) {
            setListData(data);
        }else{
            fetchData();
        }
    }

    @Override
    public void refresh() {
        model.emptyData();
        render();
    }

    protected ListInternetConnection getListInternetConnection(Activity act, List list) {
        return new ListInternetConnection(act,list);
    }

    protected void fetchData(){
        listener.setLoading(true);
        String url = getUrl();
        Bundle param = getParam();

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideInfo();
            }
        });

        ic.get(url,param);
    }

    public void setListData(ArrayList<Bundle> data){ // called when ready
        if(data.isEmpty()) notifyEmpty();
        listener.setLoading(false);
        setAdapter(new ListAdapter(this,data));
    }

    protected ListModel getListModel(){
        if(model==null) model = new ListModel(act,getTableName());
        return model;
    }

    protected String getTableName(){
        throw new NotImplementedException("implement table name!");
    }


    protected abstract Bundle getParam();

    protected abstract String getUrl();


    public void parseData(JSONArray result){
        if(result==null){
            notifyEmpty();
            return;
        }

        try {
            ArrayList<JSONObject> arrayObject = new ArrayList<>();
            for (int i = 0; i < result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                arrayObject.add(obj);
            }
            model.insertData(arrayObject);
        }catch(JSONException e){e.printStackTrace();}

        ArrayList<Bundle> data = model.getData();
        setListData(data);
    }

    @Override
    public void onListClick(Bundle data) {
        Log.i("jeki",data.toString());
    }

}
