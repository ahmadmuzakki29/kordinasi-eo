package com.event.kordinasi.kordinasievent;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.muzakki.ahmad.lib.Helper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String nama = Helper.getPrefString(this,"nama");
        String tipe = Helper.getPrefString(this,"tipe_user");
        ((TextView)findViewById(R.id.txtNama)).setText(nama);
        ((TextView)findViewById(R.id.txtTipe)).setText(tipe);
    }

    public void user(View v){
        startActivity(new Intent(this,UserActivity.class));
    }

    public void event(View v){
        startActivity(new Intent(this,EventActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apa anda yakin untuk keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Helper.clearPref(MainActivity.this);
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Tidak",null)
                .show();
        return true;
    }
}