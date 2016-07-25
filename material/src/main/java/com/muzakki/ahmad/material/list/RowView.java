package com.muzakki.ahmad.material.list;

import android.view.ViewGroup;

import com.muzakki.ahmad.material.ItemView;
import com.muzakki.ahmad.material.R;

/**
 * Created by jeki on 6/7/16.
 * this is item for grid or list
 */
public class RowView extends ItemView {

    public RowView(ViewGroup parent) {
        this(parent, R.layout.layout_list_item);
    }

    public RowView(ViewGroup parent, int layout_row) {
        super(parent,layout_row);
    }
}
