package com.muzakki.ahmad.material.form;

import android.app.Activity;

import com.muzakki.ahmad.material.R;

import java.util.ArrayList;

/**
 * Created by jeki on 6/2/16.
 */
public class Fields extends ArrayList<Field> {
    public Field getField(String name){
        for(Field field: this){
            if(name.equals(field.getName())){
                return field;
            }
        }
        return null;
    }

    public static Fields getProfileFields(Activity ctx){
        Fields profileFields = new Fields();

        Field foto_profil = new Field("foto_profile", Field.Type.IMAGE);
        foto_profil.setOrientation(Field.Orientation.SQUARE);
        profileFields.add(foto_profil);

        Field nik = new Field("nik", Field.Type.NUMBER);
        nik.setBackground(R.drawable.textbox);
        nik.setDrawable(R.drawable.nik);
        nik.setTitle("NIK");
        profileFields.add(nik);

        Field nama = new Field("nama", Field.Type.TEXT);
        nama.setDrawable(R.drawable.user);
        nama.setBackground(R.drawable.textbox);
        nama.setRequired(true);
        profileFields.add(nama);

        Field tgl_lahir = new Field("tgl_lahir", Field.Type.DATE);
        tgl_lahir.setRequired(true);
        tgl_lahir.setTitle("Tanggal Lahir");
        tgl_lahir.setBackground(R.drawable.textbox);
        tgl_lahir.setDrawable(R.drawable.birthday);
        profileFields.add(tgl_lahir);

        Field jenis_kelamin = new Field("jenis_kelamin",Field.Type.RADIO);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Laki-laki"));
        items.add(new Item("Perempuan"));
        jenis_kelamin.setItems(items);
        profileFields.add(jenis_kelamin);

        profileFields.add(new Field(null, Field.Type.SEPARATOR));

        Field alamat = new Field("alamat", Field.Type.TEXTAREA);
        alamat.setRequired(true);
        profileFields.add(alamat);

        Field kota = new Field("kota", Field.Type.PICKER);
        kota.setRequired(true);
        profileFields.add(kota);

        Field provinsi = new Field("provinsi", Field.Type.PICKER);
        provinsi.setRequired(true);
        profileFields.add(provinsi);

        profileFields.add(new Field(null, Field.Type.SEPARATOR));

        Field gol_darah = new Field("gol_darah", Field.Type.SPINNER);
        gol_darah.setTitle("Golongan Darah");
        ArrayList<Item> itemsDarah = new ArrayList<>();
        String[] golDarahArray = ctx.getResources().getStringArray(R.array.gol_darah);
        for(String gol: golDarahArray) itemsDarah.add(new Item(gol));
        gol_darah.setItems(itemsDarah);
        profileFields.add(gol_darah);

        Field foto_ktp = new Field("foto_ktp", Field.Type.IMAGE);
        foto_ktp.setOrientation(Field.Orientation.LANDSCAPE);
        profileFields.add(foto_ktp);

        return profileFields;
    }

}
