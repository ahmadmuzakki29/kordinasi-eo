package com.event.kordinasi.kordinasievent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.muzakki.ahmad.lib.Constant;
import com.muzakki.ahmad.material.form.Form;
import com.muzakki.ahmad.material.list.List;
import com.muzakki.ahmad.material.list.ListActivity;
import com.muzakki.ahmad.material.list.ListLocal;
import com.muzakki.ahmad.material.list.ListViewHolder;
import com.muzakki.ahmad.material.list.RowView;

public class EventActivity extends ListActivity {

    private ListLocal list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        render();
    }

    @Override
    protected String getTitleList() {
        return "Event";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent in = new Intent(this,EventFormActivity.class);
        Bundle b = new Bundle();
        b.putString("title","Tambah Event");
        b.putSerializable("action", Form.Action.ADD);
        in.putExtras(b);
        startActivityForResult(in,1);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        list.render();
    }

    @Override
    protected List getList() {
        if(list==null) list = new EventList(this,this);
        return list;
    }

    private class EventList extends ListLocal{

        public EventList(Activity act, Listener listener) {
            super(act, listener);
        }

        @Override
        protected String getTableName() {
            return "event";
        }

        @Override
        protected Bundle getParam() {
            return null;
        }

        @Override
        protected String getUrl() {
            return Constant.HOST+"/get/event";
        }

        @Override
        protected ViewHolder getViewHolder(RowView rv) {
            return new EventView(rv);
        }

        @Override
        public void onListClick(Bundle data) {
            Intent in = new Intent(EventActivity.this, DetailEventActivity.class);
            in.putExtra("id",data.getString("id"));
            startActivity(in);
        }
    }

    private class EventView extends ListViewHolder{

        public EventView(RowView rowview) {
            super(rowview);
        }

        @Override
        public void onBindView(Bundle row) {
            getTitle().setText(row.getString("nama"));
            ((TextView)getDescription()).setText(row.getString("tempat"));
            Glide.with(EventActivity.this)
                    .load(row.getString("foto"))
                    .asBitmap()
                    .into(getIcon());
        }
    }
}
