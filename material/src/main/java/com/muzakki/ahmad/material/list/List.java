package com.muzakki.ahmad.material.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.muzakki.ahmad.material.R;

import org.json.JSONArray;

/**
 * Created by jeki on 6/7/16.
 */
public abstract class List extends RecyclerView implements View.OnClickListener{
    private final Activity act;
    private final Listener listener;
    private View emptyView, timeOutView;
    private LinearLayoutManager lm;

    public List(Activity activity,Listener listener) {
        super(activity);
        this.act = activity;
        this.listener = listener;
        initComponent();
    }

    private void initComponent() {
        setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        lm = new LinearLayoutManager(act);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(lm);
        setHasFixedSize(true);


        RelativeLayout.LayoutParams lpCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lpCenter.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);

        emptyView = initEmptyView();
        emptyView.setLayoutParams(lpCenter);
        emptyView.setVisibility(View.GONE);

        timeOutView = initTimeoutView();
        timeOutView.setLayoutParams(lpCenter);
        timeOutView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int pos = getChildAdapterPosition(view);
        if(pos==-1) return;
        onListClick(getAdapter().getRow(pos));
    }

    public abstract void render();
    public abstract void parseData(JSONArray result);
    protected abstract void onListClick(Bundle data);
    public abstract void refresh();
    protected abstract ViewHolder getViewHolder(RowView rv);

    public ViewHolder getViewHolder(ViewGroup parent){
        return getViewHolder(getRowView(parent));
    }



    final public View getEmptyView(){
        return emptyView;
    }

    final public View getTimeOutView(){
        return timeOutView;
    }

    protected void hideInfo(){
        timeOutView.setVisibility(GONE);
        emptyView.setVisibility(GONE);
    }

    protected View initEmptyView(){
        View v = act.getLayoutInflater().inflate(R.layout.inc_notfound, null);
        return v;
    }

    protected View initTimeoutView(){
        View v = act.getLayoutInflater().inflate(R.layout.inc_timeout, null);
        RelativeLayout.LayoutParams lp =new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        v.setLayoutParams(lp);
        v.setVisibility(View.GONE);
        return v;
    }

    protected RowView getRowView(ViewGroup parent){
        return new RowView(parent);
    }

    public ListAdapter getAdapter() {
        return (ListAdapter) super.getAdapter();
    }

    public void notifyEmpty(){
        listener.setLoading(false);
        getEmptyView().setVisibility(VISIBLE);
    }

    public void scrollToTop(){
        lm.scrollToPositionWithOffset(0, 0);
    }

    public interface Listener{
        void setLoading(boolean loading);
    }
}
