package com.muzakki.ahmad.material.grid;

import android.view.View;
import android.widget.Toast;

import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.lib.InternetConnection;

import org.json.JSONObject;

/**
 * Created by jeki on 6/8/16.
 */
public class GridInternetConnection extends InternetConnection {
    private final GridActivity act;
    private Grid grid;

    public GridInternetConnection(GridActivity act, Grid grid) {
        this(act,grid,true);
    }

    public GridInternetConnection(GridActivity act, Grid grid, boolean persistent){
        super(act,persistent);
        this.grid = grid;
        this.act = act;
    }

    @Override
    protected void onSuccess(JSONObject result) {
        grid.parseData(Helper.getJSONData(result));
    }

    @Override
    protected void onTimeout() {
        if (grid.getItemCount() == 0) {
            grid.getTimeOutView().setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(act,"Koneksi Gagal",Toast.LENGTH_LONG).show();
        }
    }
}
