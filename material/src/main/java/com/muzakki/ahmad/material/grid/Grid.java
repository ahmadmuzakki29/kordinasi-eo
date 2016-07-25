package com.muzakki.ahmad.material.grid;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.muzakki.ahmad.material.R;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by jeki on 6/16/16.
 */
public abstract class Grid extends FlowLayout{
    private final Activity act;
    private final Listener listener;
    private View emptyView;
    private View timeOutView;


    public Grid(Activity act,Listener listener) {
        super(act);
        this.act = act;
        this.listener = listener;

        initComponent();
    }

    private void initComponent() {

        setLayoutParams(new FlowLayout.LayoutParams(FlowLayout.LayoutParams.MATCH_PARENT,
                FlowLayout.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.FILL_HORIZONTAL);

        RelativeLayout.LayoutParams lp =new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);

        emptyView = initEmptyView();
        emptyView.setLayoutParams(lp);

        timeOutView = initTimeoutView();
        timeOutView.setLayoutParams(lp);
    }

    protected void hideInfo(){
        timeOutView.setVisibility(GONE);
        emptyView.setVisibility(GONE);
    }

    abstract void render();
    abstract void refresh();
    public abstract void parseData(JSONArray result);
    abstract void onItemClick(Bundle data);
    public abstract int getItemCount();
    protected abstract void onDrawItem(GridView gv,Bundle data);
    protected abstract ArrayList<Bundle> getData();

    private void onItemClick(int i){
        Bundle b = getData().get(i);
        onItemClick(b);
    }

    protected View initEmptyView(){
        return act.getLayoutInflater().inflate(R.layout.inc_notfound, null);
    }

    protected View initTimeoutView(){
        return act.getLayoutInflater().inflate(R.layout.inc_timeout, null);
    }

    public View getEmptyView() {
        return emptyView;
    }

    public View getTimeOutView() {
        return timeOutView;
    }

    protected GridInternetConnection getGridInternetConnection(GridActivity act, Grid grid){
        return new GridInternetConnection(act,grid);
    }

    protected GridView getGridView(Grid parent,int position){
        return new GridView(parent,position);
    }


    protected void drawItem(ArrayList<Bundle> data){
        hideInfo();

        int i = 0;
        for(Bundle d: data){
            GridView rv = getGridView(this,i++);
            onDrawItem(rv,d);
            addView(rv.getView());
        }
    }

    public void notifyEmpty(){
        listener.setLoading(false);
        getEmptyView().setVisibility(VISIBLE);
    }

    public View.OnClickListener getOnClickListener(int position){
        return new OnGridItemClickListener(position);
    }

    private class OnGridItemClickListener implements View.OnClickListener{
        private final int position;

        OnGridItemClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            onItemClick(position);
        }
    }

    public interface Listener{
        void setLoading(boolean loading);
    }
}
