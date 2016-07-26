package com.muzakki.ahmad.material.form;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by jeki on 6/14/16.
 */
public class DeleteDialog implements FormInternetConnection.Listener {
    private final Context ctx;
    private final String id;
    private final ProgressDialog progress;
    private final Listener listener;
    private final Form.SaveType saveType;

    public DeleteDialog(Context ctx, String id, Form.SaveType type,Listener listener){
        this.ctx = ctx;
        this.id = id;
        this.saveType = type;
        progress = new ProgressDialog(ctx);
        progress.setMessage("Mohon Tunggu...");
        this.listener = listener;
    }

    public void showDialog(String konteks) {
        new AlertDialog.Builder(ctx)
                .setTitle("Hapus " + konteks + " ?")
                .setMessage("Apa anda yakin untuk menghapus " + konteks + " ini?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doDelete();
                    }
                })
                .setNegativeButton("Tidak", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void doDelete(){
        progress.show();

        switch (saveType){
            case BOTH:
            case SERVER:
                deleteToServer();
                break;
            case LOCAL:
                deleteToLocal();
                break;
        }
    }

    private void deleteToServer(){
        getInternetConnection().delete(id);
    }

    private void deleteToLocal(){
        getModel().delete(id);
    }

    protected FormInternetConnection getInternetConnection(){
        return new FormInternetConnection(ctx,null, this){
            @Override
            protected String getDeleteUrl(String id) {
                return DeleteDialog.this.getDeleteUrl(id);
            }
        };
    }

    protected String getDeleteUrl(String id) {
        throw new UnsupportedOperationException("delete url not implemented");
    }

    protected String getTable() {
        throw new UnsupportedOperationException("implement table name");
    }

    protected FormModel getModel(){
        return new FormModel(ctx,getTable());
    }

    @Override
    public void onTimeout() {
        progress.hide();
        Toast.makeText(ctx,"Sayangnya, Delete gagal...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServerSuccess(JSONObject result, Bundle data) {
        if(saveType== Form.SaveType.BOTH){
            deleteToLocal();
        }
        progress.hide();
        listener.onDeleteSuccess();
    }

    public interface Listener{
        void onDeleteSuccess();
    }
}
