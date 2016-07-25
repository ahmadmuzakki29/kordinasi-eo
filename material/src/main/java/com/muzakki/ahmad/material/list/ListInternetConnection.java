package com.muzakki.ahmad.material.list;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.lib.InternetConnection;

import org.json.JSONObject;

/**
 * Created by jeki on 6/8/16.
 */
public class ListInternetConnection extends InternetConnection {

    private final Activity act;
    private List list;

    public ListInternetConnection(Activity act,List list) {
        this(act,list,true);
    }

    public ListInternetConnection(Activity act, List list, boolean persistent) {
        super(act,persistent);
        this.list = list;
        this.act = act;
    }

    @Override
    protected void onSuccess(JSONObject result) {
        list.parseData(Helper.getJSONData(result));
    }

    @Override
    protected void onTimeout() {
        if (list.getAdapter().getItemCount() == 0) {
            list.getTimeOutView().setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(act,"Koneksi Gagal",Toast.LENGTH_LONG).show();
        }
    }
}
