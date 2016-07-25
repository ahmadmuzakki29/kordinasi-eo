package com.muzakki.ahmad.material.form;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jeki on 5/31/16.
 */
public class Item implements Parcelable {
    private String kode;
    private String value;

    public Item(String value) {
        this.value = value;
    }

    public Item(String kode, String value) {
        this.kode = kode;
        this.value = value;
    }

    public String getKode() {
        return kode;
    }

    public String getValue() {
        return value;
    }

    protected Item(Parcel in) {
        kode = in.readString();
        value = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kode);
        dest.writeString(value);
    }

    @SuppressWarnings("unused")
    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}