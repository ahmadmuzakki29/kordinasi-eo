package com.event.kordinasi.kordinasievent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.muzakki.ahmad.lib.Constant;
import com.muzakki.ahmad.material.form.Field;
import com.muzakki.ahmad.material.form.Fields;
import com.muzakki.ahmad.material.form.Form;
import com.muzakki.ahmad.material.form.FormActivity;
import com.muzakki.ahmad.material.form.FormInternetConnection;

public class EventFormActivity extends FormActivity {

    private Fields fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fields = new Fields();

        Field foto = new Field("foto", Field.Type.IMAGE);
        foto.setOrientation(Field.Orientation.SQUARE);
        fields.add(foto);
        fields.add(new Field("nama", Field.Type.TEXT));

        fields.add(new Field("tanggal", Field.Type.DATE));
        fields.add(new Field("tempat", Field.Type.TEXT));
        fields.add(new Field("guest_star", Field.Type.TEXT));


        render();
    }

    @Override
    protected Form getForm() {
        return new EventForm(this,fields, Form.SaveType.BOTH,getAction(),this);
    }

    private class EventForm extends Form{

        public EventForm(AppCompatActivity act, Fields fields, SaveType saveType, Action action, Listener listener) {
            super(act, fields, saveType, action, listener);
        }

        @Override
        protected FormInternetConnection getInternetConnection(Bundle b, FormInternetConnection.Listener listener) {
            return new FormInternetConnection(getContext(),b,listener){
                @Override
                protected String getInsertUrl() {
                    return Constant.HOST+"/insert/event";
                }


                @Override
                protected String getDeleteUrl(String id) {
                    return Constant.HOST+"/delete/event/"+id;
                }
            };
        }

        @Override
        protected String getLocalTable() {
            return "event";
        }
    }


}
