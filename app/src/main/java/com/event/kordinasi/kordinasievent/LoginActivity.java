package com.event.kordinasi.kordinasievent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.muzakki.ahmad.lib.Constant;
import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.lib.InternetConnection;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private EditText mEmailField;
    private EditText mPasswordField;
    private ProgressDialog mProgressDialog;
    private InternetConnection ic=new InternetConnection(this){
        @Override
        protected void onSuccess(JSONObject result) {
            try {
                if(result.getBoolean("success")){
                    loginSukses(result.getJSONObject("data"));
                }else{
                    loginGagal(result.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    };

    private void loginSukses(JSONObject result) {
        hideProgressDialog();
        try {
            Helper.setPref(this,"logged_in",true);
            Helper.setPref(this,"username",result.getString("username"));
            Helper.setPref(this,"nama",result.getString("nama"));
            Helper.setPref(this,"tipe_user",result.getString("tipe_user"));
            startActivity(new Intent(this,MainActivity.class));
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loginGagal(String msg) {
        hideProgressDialog();
        showErrorMessage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponent();
    }

    private void initComponent() {
        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);
        mPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login(null);
                    handled = true;
                }
                return handled;
            }
        });
    }

    public void login(View view){
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        if(!validateForm()) return;

        String username = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        showProgressDialog();
        Bundle param = new Bundle();
        param.putString("username",username);
        param.putString("password",password);
        ic.post(Constant.URL_LOGIN,param);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void showErrorMessage(){
        new AlertDialog.Builder(this)
                .setTitle("Login Gagal")
                .setMessage("Maaf email atau password yang anda masukkan salah.")
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Logging in...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

}

