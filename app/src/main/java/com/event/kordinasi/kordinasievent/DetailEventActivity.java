package com.event.kordinasi.kordinasievent;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.muzakki.ahmad.lib.Constant;
import com.muzakki.ahmad.material.detail.Detail;
import com.muzakki.ahmad.material.detail.DetailActivity;
import com.muzakki.ahmad.material.detail.DetailLocal;
import com.muzakki.ahmad.material.detail.DetailModel;
import com.muzakki.ahmad.material.detail.DetailTabActivity;
import com.muzakki.ahmad.material.form.Fields;
import com.muzakki.ahmad.material.form.Form;
import com.muzakki.ahmad.material.list.List;
import com.muzakki.ahmad.material.list.ListLocal;
import com.muzakki.ahmad.material.list.ListModel;
import com.muzakki.ahmad.material.list.ListViewHolder;
import com.muzakki.ahmad.material.list.RowView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DetailEventActivity extends DetailTabActivity implements List.Listener,
        SwipeRefreshLayout.OnRefreshListener {

    private String id;
    private Fields fields;
    private JobList list;
    private SwipeRefreshLayout swiper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        fields = new EventFields();
        fields.removeField("foto");
        swiper = new SwipeRefreshLayout(this);
        swiper.setOnRefreshListener(this);
        render();
    }

    @Override
    protected Detail getDetail() {
        return new EventDetail(this,id,fields);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add_job){
            Intent in = new Intent(this,JobFormActivity.class);
            in.putExtra("id_event",id);
            in.putExtra("action", Form.Action.ADD);
            in.putExtra("title","Tambah Job");
            startActivityForResult(in,1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 && resultCode==RESULT_OK){ // tambah job
            list.render();
        }
    }

    @Override
    protected ArrayList<String> getTabs() {
        ArrayList<String> tabs = super.getTabs();
        tabs.add("Job");
        return tabs;
    }

    @Override
    protected View getTabView(int i) {
        switch (i){
            case 1: return getList();
            default:return super.getTabView(i); // default detail
        }
    }

    private View getList(){
        if(list==null){
            list = new JobList(this,this);
            list.render();
            swiper.addView(list);
        }
        return swiper;
    }

    @Override
    public void setLoading(final boolean loading) {
        swiper.post(new Runnable() {
            @Override
            public void run() {
                swiper.setRefreshing(loading);
            }
        });
    }

    @Override
    public void onRefresh() {
        list.refresh();
    }

    private class EventDetail extends DetailLocal{

        public EventDetail(DetailActivity ctx, String id,Fields fields) {
            super(ctx, id,fields);
        }

        @Override
        protected DetailModel getLocalModel() {
            return new DetailModel(DetailEventActivity.this,"event");
        }

        @Override
        protected String getTitle(Bundle data) {
            return data.getString("nama");
        }

        @Override
        protected String getSubtitle(Bundle data) {
            return data.getString("tempat");
        }

        @Override
        protected Bitmap getImage(Bundle data) {
            new LoadImage().execute(data.getString("foto"));
            return BitmapFactory.decodeResource(getResources(),R.drawable.picture);
        }
    }

    private class LoadImage extends AsyncTask<String,Object,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                return Glide.
                        with(DetailEventActivity.this).
                        load(strings[0]).
                        asBitmap().
                        into(300, 300). // Width and height
                        get();

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap==null){
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.picture);
            }
            setCoverImage(bitmap);
        }
    }

    private class JobList extends ListLocal{

        public JobList(Activity act, Listener listener) {
            super(act, listener);
        }

        @Override
        protected Bundle getParam(){
            Bundle b = new Bundle();
            b.putString("event",id);
            return b;
        }

        @Override
        protected String getUrl() {
            return Constant.HOST+"/get/job?event="+id;
        }

        @Override
        protected ViewHolder getViewHolder(RowView rv) {
            return new ListViewHolder(rv) {
                @Override
                public void onBindView(Bundle row) {
                    getTitle().setText(row.getString("nama"));
                    ((TextView)getDescription()).setText(row.getString("tugas"));
                    try {
                        JSONObject status = new JSONObject(row.getString("status"));
                        if(status.getBoolean("selesai")){
                            getIcon().setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.check));
                        }else{
                            getIcon().setVisibility(GONE);
                        }
                    } catch (JSONException e) {
                        getIcon().setVisibility(GONE);
                        e.printStackTrace();
                    }

                }
            };
        }

        @Override
        protected RowView getRowView(ViewGroup parent) {
            return new RowView(parent,R.layout.row_job);
        }

        @Override
        protected ListModel getListModel() {
            return new ListModel(getContext(),getTableName()){
                @Override
                public ArrayList<Bundle> getData() {
                    SQLiteDatabase db = getReadableDatabase();
                    Cursor c = db.rawQuery("select * from "+getTable()+" " +
                            "where event=? order by id",new String[]{id});

                    try{
                        return getBundle(c);
                    }finally {
                        db.close();
                        c.close();
                    }
                }
            };
        }

        @Override
        public void onListClick(Bundle data) {
            Intent in = new Intent(DetailEventActivity.this,JobFormActivity.class);
            in.putExtra("id",data.getString("id"));
            in.putExtra("id_event",id);
            in.putExtra("action", Form.Action.EDIT);
            in.putExtra("title","Edit Job");
            startActivityForResult(in,1);
        }

        @Override
        protected String getTableName() {
            return "job";
        }
    }
}
