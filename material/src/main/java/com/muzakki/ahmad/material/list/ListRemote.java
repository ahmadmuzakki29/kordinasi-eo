package com.muzakki.ahmad.material.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.lib.InternetConnection;
import com.muzakki.ahmad.material.R;
import com.muzakki.ahmad.material.model.DataCacheModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jeki on 6/3/16.
 */
public abstract class ListRemote extends List {
    private final int LIMIT = 25;
    private final Activity act;
    private final DataCacheModel model;
    private final Listener listener;
    private ArrayList<Bundle> data = new ArrayList<>();

    private boolean loadingState,bottom = false;
    InternetConnection ic;
    InternetConnection icRender;
    private boolean render = false;

    public ListRemote(Activity act, Listener listener) {
        super(act,listener);
        this.act = act;
        this.listener = listener;
        ic = getListInternetConnection(act,this);
        icRender = getListInternetConnectionRender(act,this);
        model = new DataCacheModel(act,getCacheTable());
    }

    @Override
    public void render(){
        hideInfo();
        ic.disconnect();
        render = true;
        listener.setLoading(true);
        data = model.getData();
        setAdapter(new Adapter(this,data));
        fetchData();
    }

    @Override
    public void refresh() {
        render();
        // empty model cache here
    }

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
        if(data.size()>0) data.remove(data.size() - 1); //remove loading bottom
        if(render) data.clear();

        try {
            for (int i = 0; i < result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                data.add(Helper.jsonToBundle(obj));
            }
        }catch(JSONException e){e.printStackTrace();}

        if(render){
            model.insertData(data); // save to cache
            scrollToTop();
        }

        getAdapter().notifyDataSetChanged();
        doLoading(false);

        setOnScrollListener(new MyEndlessListener(  (LinearLayoutManager) getLayoutManager()));
        render = false;
    }

    private void doLoading(final boolean state){
        loadingState= state;

        if (state) {
            data.add(null);
        }else{
            listener.setLoading(state);
        }

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void clearCache(){
        model.emptyData();
    }

    protected abstract Bundle getParam();

    protected abstract String getUrl();


    @Override
    public void onListClick(Bundle data) {
        Log.i("jeki",data.toString());
    }



    private ViewHolder getRowLoading(ViewGroup parent) {
        View v = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_loading, parent, false);
        return new ViewHolder(v){};
    }

    public static class Adapter extends ListAdapter {
        private final ArrayList<Bundle> data;
        private final ListRemote ctx;

        public Adapter(ListRemote ctx, ArrayList<Bundle> data){
            super(ctx,data);
            this.ctx = ctx;
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==0) {
                return ctx.getViewHolder(parent);
            }
            return ctx.getRowLoading(parent);
        }

        @Override
        public int getItemViewType(int position) {
            boolean end = position==data.size()-1;
            if(ctx.isLoading() && end){
                return 1;
            }else{
                return 0;
            }
        }

    }

    protected ListInternetConnection getListInternetConnection(Activity act, List list) {
        return new ListInternetConnection(act,list);
    }

    protected ListInternetConnectionRender getListInternetConnectionRender(Activity act, List list){
        return new ListInternetConnectionRender(act,list);
    }

    // INTERNET CONNECTION

    public static class ListInternetConnectionRender extends ListInternetConnection{
        private final ListRemote list;

        ListInternetConnectionRender(Activity act, List list){
            super(act,list,false);
            this.list = (ListRemote) list;
        }

        @Override
        protected void onTimeout() {
            super.onTimeout();
            list.getListener().setLoading(false);
            list.setRender(false);
        }
    }

    public Listener getListener() {
        return listener;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    private class MyEndlessListener extends EndlessRecyclerOnScrollListener {


        public MyEndlessListener(LinearLayoutManager linearLayoutManager) {
            super(linearLayoutManager);
        }

        @Override
        public void onLoadMore(int current_page) {
            loadMore();
        }
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

    public Boolean isLoading(){
        return loadingState;
    }

}
