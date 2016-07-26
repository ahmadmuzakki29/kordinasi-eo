package com.event.kordinasi.kordinasievent;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.muzakki.ahmad.lib.Constant;
import com.muzakki.ahmad.material.form.DeleteDialog;
import com.muzakki.ahmad.material.form.Field;
import com.muzakki.ahmad.material.form.Fields;
import com.muzakki.ahmad.material.form.Form;
import com.muzakki.ahmad.material.form.FormActivity;
import com.muzakki.ahmad.material.form.FormInternetConnection;
import com.muzakki.ahmad.material.form.FormModel;
import com.muzakki.ahmad.material.form.Item;

import java.util.ArrayList;

public class UserFormActivity extends FormActivity implements DeleteDialog.Listener {
    Fields fields;
    private UserForm form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fields = new Fields();
        Field username = new Field("username", Field.Type.TEXT);
        fields.add(username);

        Field password = new Field("password",Field.Type.PASSWORD);
        fields.add(password);

        Field nama = new Field("nama", Field.Type.TEXT);
        fields.add(nama);

        Field tipe = new Field("tipe_user",Field.Type.RADIO);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Admin"));
        items.add(new Item("Anggota"));
        tipe.setItems(items);
        fields.add(tipe);

        render();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("jeki",getAction().toString());
        if(getAction()== Form.Action.ADD) {
            return super.onCreateOptionsMenu(menu);
        }else{
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.user_edit,menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(getAction()== Form.Action.ADD) {
            return super.onOptionsItemSelected(item);
        }else {
            if(item.getItemId()==R.id.menu_save){
                return super.onOptionsItemSelected(item);
            }else{
                DeleteDialog delete = new DeleteDialog(this, form.getDataId(), form.getSaveType(), this){
                    @Override
                    protected String getTable() {
                        return "user";
                    }

                    @Override
                    protected String getDeleteUrl(String id) {
                        return Constant.HOST+"/delete/user/"+id;
                    }
                };
                delete.showDialog("User");
                return true;
            }
        }
    }

    @Override
    protected Form getForm() {
        if(form==null) form = new UserForm(this, fields, Form.SaveType.BOTH, getAction(), this);
        return form;
    }

    @Override
    public void onDeleteSuccess() {
        finish();
    }

    private class UserForm extends Form{

        public UserForm(AppCompatActivity act, Fields fields, SaveType saveType, Action action, Listener listener) {
            super(act, fields, saveType, action, listener);
        }

        @Override
        protected FormInternetConnection getInternetConnection(Bundle b, FormInternetConnection.Listener listener) {
            return new UserInternetConnection(UserFormActivity.this,b,listener);
        }

        @Override
        protected FormModel getFormModel() {
            return new UserFormModel(UserFormActivity.this,"user");
        }
    }

    private class UserFormModel extends FormModel{

        public UserFormModel(Context context, String table) {
            super(context, table);
        }

        @Override
        public void setData(Bundle data) {
            data.remove("password");
            super.setData(data);
        }

    }

    private class UserInternetConnection extends FormInternetConnection{

        public UserInternetConnection(Context ctx, Bundle b, Listener listener) {
            super(ctx, b, listener);
        }

        @Override
        protected String getInsertUrl() {
            return Constant.URL_USER_ADD;
        }

        @Override
        protected String getUpdateUrl(String id) {
            return Constant.HOST+"/update/user/"+id;
        }
    }
}
