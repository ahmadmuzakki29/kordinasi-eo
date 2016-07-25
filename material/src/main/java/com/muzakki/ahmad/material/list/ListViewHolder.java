package com.muzakki.ahmad.material.list;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jeki on 6/7/16.
 */
public abstract class ListViewHolder extends RecyclerView.ViewHolder {

    private RowView rowView;

    public ListViewHolder(RowView rowview) {
        super(rowview.getView());
        this.rowView = rowview;
    }

    public View getView(){
        return rowView.getView();
    }

    public TextView getTitle() {
        return rowView.getTitle();
    }

    public ImageView getIcon() {
        return rowView.getPicture();
    }

    public View getDescription() {
        return rowView.getDescription();
    }

    public abstract void onBindView(Bundle row);
}
