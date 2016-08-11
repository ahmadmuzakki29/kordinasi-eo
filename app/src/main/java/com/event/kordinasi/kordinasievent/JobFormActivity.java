package com.event.kordinasi.kordinasievent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.muzakki.ahmad.lib.Constant;
import com.muzakki.ahmad.material.form.DeleteDialog;
import com.muzakki.ahmad.material.form.Field;
import com.muzakki.ahmad.material.form.Fields;
import com.muzakki.ahmad.material.form.Form;
import com.muzakki.ahmad.material.form.FormActivity;
import com.muzakki.ahmad.material.form.FormInternetConnection;
import com.muzakki.ahmad.material.form.Item;

import java.util.ArrayList;

public class JobFormActivity extends FormActivity implements DeleteDialog.Listener {

    private Fields fields;
    private String id_event;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id_event = getIntent().getStringExtra("id_event");
        fields = new Fields();

        if(getAction()== Form.Action.EDIT) {
            Field status = new Field("status", Field.Type.CHECKBOX);
            ArrayList<Item> items = new ArrayList<>();
            items.add(new Item("selesai"));
            status.setItems(items);
            fields.add(status);
        }

        Field nama = new Field("nama", Field.Type.TEXT);
        nama.setTitle("Nama Job");
        fields.add(nama);
        fields.add(new Field("tugas", Field.Type.TEXTAREA));
        fields.add(new Field("komentar", Field.Type.TEXTAREA));

        render();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getAction()== Form.Action.EDIT){
            getMenuInflater().inflate(R.menu.save_delete,menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(getAction()== Form.Action.EDIT) {
            if(item.getItemId()==R.id.menu_save){
                return form.save();
            }else if(item.getItemId()==R.id.menu_delete) {
               DeleteDialog delete = new DeleteDialog(this, form.getDataId(), form.getSaveType(), this) {
                   @Override
                   protected String getTable() {
                       return "user";
                   }

                   @Override
                   protected String getDeleteUrl(String id) {
                       return Constant.HOST + "/delete/user/" + id;
                   }
               };
               delete.showDialog("User");
               return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Form getForm() {
        if(form==null) form = new JobForm(this,fields, Form.SaveType.BOTH, getAction(),this);
        return form;
    }

    @Override
    public void onDeleteSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    private class JobForm extends Form{

        public JobForm(AppCompatActivity act, Fields fields, SaveType saveType, Action action, Listener listener) {
            super(act, fields, saveType, action, listener);
        }

        @Override
        protected FormInternetConnection getInternetConnection(Bundle b, FormInternetConnection.Listener listener) {
            return new FormInternetConnection(getContext(),b,listener){
                @Override
                protected String getInsertUrl() {
                    return Constant.HOST+"/insert/job?event="+id_event;
                }

                @Override
                protected String getUpdateUrl(String id) {
                    return Constant.HOST+"/update/job/"+id+"?event="+id_event;
                }
            };
        }

        @Override
        protected String getLocalTable() {
            return "job";
        }
    }
}
