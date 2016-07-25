package com.event.kordinasi.kordinasievent;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.muzakki.ahmad.lib.Constant;
import com.muzakki.ahmad.material.form.Field;
import com.muzakki.ahmad.material.form.Fields;
import com.muzakki.ahmad.material.form.Form;
import com.muzakki.ahmad.material.form.FormActivity;
import com.muzakki.ahmad.material.form.FormInternetConnection;
import com.muzakki.ahmad.material.form.Item;

import java.util.ArrayList;

public class UserFormActivity extends FormActivity {
    UserInternetConnection ic;
    UserForm form;
    Fields fields;
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
    protected Form getForm() {
        if(form==null) form = new UserForm(this,fields, Form.SaveType.BOTH, getAction(),this);
        return form;
    }

    private class UserForm extends Form{

        public UserForm(AppCompatActivity act, Fields fields, SaveType saveType, Action action, Listener listener) {
            super(act, fields, saveType, action, listener);
        }

        @Override
        protected FormInternetConnection getInternetConnection(Bundle b, FormInternetConnection.Listener listener) {
            if(ic==null) ic = new UserInternetConnection(UserFormActivity.this,b,listener);
            return ic;
        }

        @Override
        public String getLocalTable() {
            return "user";
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
