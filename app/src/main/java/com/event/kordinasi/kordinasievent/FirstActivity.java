package com.event.kordinasi.kordinasievent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.muzakki.ahmad.lib.Helper;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Boolean loggedIn = Helper.getPrefBoolean(this, "logged_in");
        if(!loggedIn){
            startActivity(new Intent(this,LoginActivity.class));
        }else{
            startActivity(new Intent(this,MainActivity.class));
        }
        finish();
    }
}
