package com.muzakki.ahmad.lib.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;
import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.lib.R;

public abstract class FirstActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
    }

    @Override
    protected void onStart() {
        super.onStart();
        String iid = FirebaseInstanceId.getInstance().getId();
        Helper.setIID(this,iid);
        initAuth();
    }

    private void initAuth() {
        Auth auth = Auth.getInstance(this);
        User user = auth.getCurrentUser();
        if (user == null) {
            Intent in = new Intent(this, LoginActivity.class);
            startActivityForResult(in,1);
        } else {
            startMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==Activity.RESULT_OK){
            startMainActivity();
        }else{
            finish();
        }
    }

    private void startMainActivity(){
        Intent in = new Intent(this, getMainActivity());
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
        finish();
    }

    abstract protected Class getMainActivity();

    protected int getContentView() {
        return R.layout.activity_first;
    }
}
