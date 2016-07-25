package com.muzakki.ahmad.material.form;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.muzakki.ahmad.material.R;

import java.util.HashMap;

/**
 * Created by jeki on 6/2/16.
 */
public abstract class FormActivity extends AppCompatActivity
        implements Form.Listener{

    private Form form;
    private Form.Action action = Form.Action.ADD;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_form);

        ActionBar ab = getSupportActionBar();
        if(ab!=null){
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if(extras.getString("title")!=null){
            setTitle(extras.getString("title"));
        }
        action = (Form.Action) extras.getSerializable("action");

        progress = new ProgressDialog(this);
        progress.setMessage("Mohon Tunggu...");
    }

    public Form.Action getAction() {
        return action;
    }

    protected void setTitle(String title){
        ActionBar ab = getSupportActionBar();
        if(ab!=null && title!=null){
            ab.setTitle(title);
        }
    }


    protected void render(){
        Bundle extras = getIntent().getExtras();
        form = getForm();
        form.setModel(getModel());
        if(action!=null && action== Form.Action.EDIT){
            form.setDataId(extras.getString("id"));
            form.initData();
        }
        form.renderFields();

        ScrollView parent = (ScrollView) findViewById(R.id.container);
        parent.addView(form);
    }

    protected abstract Form getForm();

    protected FormModel getModel(){
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            form.onResult(requestCode,data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }else{ // do saving
            return form.save();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        form.notifyOrientationChanged();
    }

    protected HashMap<String,View> getViews(){
        return form.getViews();
    }

    public void onSaveSuccess(String id){
        setLoading(false);
        Intent in = new Intent();
        in.putExtra("id",id);
        setResult(RESULT_OK, in);
        if(action== Form.Action.ADD) {
            finish();
        }else{
            Toast.makeText(this,"Data Berhasil Disimpan",Toast.LENGTH_LONG).show();
        }
    }

    public void setLoading(boolean b) {
        if(b){
            progress.show();
        }else{
            progress.hide();
        }
    }

}
