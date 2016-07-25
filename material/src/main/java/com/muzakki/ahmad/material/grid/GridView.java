package com.muzakki.ahmad.material.grid;

import android.view.Gravity;

import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.material.ItemView;
import com.muzakki.ahmad.material.R;

import org.apmem.tools.layouts.FlowLayout;

/**
 * Created by jeki on 6/7/16.
 * this is item for grid or list
 */
public class GridView extends ItemView {

    private final Grid parent;
    private final int position;

    public GridView(Grid parent,int position) {
        this(parent,position, R.layout.layout_grid_item);
    }

    public GridView(Grid parent, int position, int layout){
        super(parent,layout);
        this.parent = parent;
        this.position = position;
        initComponent();
    }

    private void initComponent() {
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(getDp(150),
                FlowLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,getDp(15));
        lp.setGravity(Gravity.CENTER_HORIZONTAL);
        getView().setLayoutParams(lp);
        getView().setOnClickListener(parent.getOnClickListener(position));
    }

    private int getDp(int i) {
        return Helper.getPxFromDp(i,parent.getResources());
    }

}
