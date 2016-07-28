package com.muzakki.ahmad.material.form;

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

    public void removeField(String nama){
        Field field= getField(nama);
        remove(field);
    }
}
