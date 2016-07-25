package com.event.kordinasi.kordinasievent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.muzakki.ahmad.lib.Constant;
import com.muzakki.ahmad.material.form.Form;
import com.muzakki.ahmad.material.list.List;
import com.muzakki.ahmad.material.list.ListActivity;
import com.muzakki.ahmad.material.list.ListLocal;
import com.muzakki.ahmad.material.list.ListViewHolder;
import com.muzakki.ahmad.material.list.RowView;

public class UserActivity extends ListActivity {
    UserList list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        render();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent in = new Intent(this, UserFormActivity.class);
        Bundle ex = new Bundle();
        ex.putString("title","Tambah User");
        ex.putSerializable("action", Form.Action.ADD);
        in.putExtras(ex);
        startActivityForResult(in,1);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        list.render();
    }

    @Override
    protected String getTitleList() {
        return "User";
    }

    @Override
    protected List getList() {
        if(list==null) list = new UserList(this,this);
        return list;
    }

    private class UserList extends ListLocal{

        public UserList(Activity act, Listener listener) {
            super(act, listener);
        }

        @Override
        protected Bundle getParam() {
            return null;
        }

        @Override
        protected String getUrl() {
            return Constant.URL_USER_LIST;
        }

        @Override
        protected ViewHolder getViewHolder(RowView rv) {
            return new UserViewHolder(rv);
        }

        @Override
        protected RowView getRowView(ViewGroup parent) {
            return new RowView(parent,R.layout.user_row);
        }

        @Override
        protected String getTableName() {
            return "user";
        }

        @Override
        public void onListClick(Bundle data) {
            Intent in = new Intent(UserActivity.this, UserFormActivity.class);
            Bundle ex = new Bundle();
            ex.putString("title","Edit User");
            ex.putSerializable("action", Form.Action.EDIT);
            ex.putString("id",data.getString("id"));
            in.putExtras(ex);
            startActivityForResult(in,1);
        }
    }

    private class UserViewHolder extends ListViewHolder{

        public UserViewHolder(RowView rowview) {
            super(rowview);
        }

        @Override
        public void onBindView(Bundle row) {
            getTitle().setText(row.getString("nama"));
            ((TextView)getDescription()).setText(row.getString("tipe_user"));
        }
    }
}
