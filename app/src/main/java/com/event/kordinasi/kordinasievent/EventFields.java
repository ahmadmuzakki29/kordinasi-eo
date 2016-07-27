package com.event.kordinasi.kordinasievent;

import com.muzakki.ahmad.material.form.Field;
import com.muzakki.ahmad.material.form.Fields;

/**
 * Created by jeki on 7/27/16.
 */
public class EventFields extends Fields {
    EventFields(){
        Field foto = new Field("foto", Field.Type.IMAGE);
        foto.setOrientation(Field.Orientation.SQUARE);
        add(foto);
        add(new Field("nama", Field.Type.TEXT));

        add(new Field("tanggal", Field.Type.DATE));
        add(new Field("tempat", Field.Type.TEXT));
        add(new Field("guest_star", Field.Type.TEXT));
    }
}
