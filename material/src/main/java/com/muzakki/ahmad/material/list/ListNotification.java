package com.muzakki.ahmad.material.list;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.muzakki.ahmad.lib.services.NotificationReceiver;
import com.muzakki.ahmad.material.model.NotificationModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jeki on 7/13/16.
 */
public abstract class ListNotification extends ListLocal {
    private final Activity act;
    private MyReceiver receiver;
    private ArrayList<Bundle> data;
    private ListModel model;

    public ListNotification(Activity act, Listener listener) {
        super(act, listener);
        this.act = act;
    }

    public void render(){
        getEmptyView().setVisibility(GONE);
        hideInfo();
        model  = getListModel();
        ArrayList<Bundle> data = model.getData();
        setListData(data);
    }

    @Override
    public void refresh() {
        fetchData();
    }

    @Override
    protected ListInternetConnection getListInternetConnection(Activity act, List list) {
        return new mListIC(act, list);
    }


    private class mListIC extends ListInternetConnection{

        private final ListNotification list;

        public mListIC(Activity act, List list) {
            super(act, list);
            this.list = (ListNotification) list;
        }

        @Override
        protected void onSuccess(JSONObject result) {
            try {
                Integer count = result.getInt("count");
                Log.i("jeki","count"+count);
                if(count==0){
                    list.stopLoading();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected ListModel getListModel() {
        if(model==null) model = new NotificationModel(getContext(),getTableName());
        return model;
    }

    public void stopLoading(){
        listener.setLoading(false);
    }

    @Override
    public void setListData(ArrayList<Bundle> data) {
        super.setListData(data);
        this.data = data;
    }

    public void registerReceiver(String tag){
        receiver = new MyReceiver();
        tag += ".FOREGROUND";
        act.registerReceiver(receiver,new IntentFilter(tag));
    }

    public void unregisterReceiver(){
        act.unregisterReceiver(receiver);
    }

    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle received = getResultExtras(true); // get extras from background receiver
            setResult(NotificationReceiver.ACCEPTED_FOREGROUND,null,received);

            NotificationModel model = (NotificationModel) getListModel();

            Bundle row_data = model.insertData(received);

            if(data==null) data = new ArrayList<>();
            data.add(0,row_data);
            getAdapter().notifyDataSetChanged();
            hideInfo();
            stopLoading();
        }
    }
}
