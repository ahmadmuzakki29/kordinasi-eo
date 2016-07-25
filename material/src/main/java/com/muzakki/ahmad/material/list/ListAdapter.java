package com.muzakki.ahmad.material.list;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by jeki on 6/14/16.
 */
public class ListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final ArrayList<Bundle> data;
    private final List ctx;

    public ListAdapter(List ctx, ArrayList<Bundle> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = ctx.getViewHolder(parent);
        vh.itemView.setOnClickListener(ctx);
        return vh;
    }

    public Bundle getRow(int i) {
        return data.get(i);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) != 0) return;

        bindViewHolder(getRow(position), holder);
    }

    protected void bindViewHolder(Bundle row, ViewHolder holder) {
        if (holder instanceof ListViewHolder) {
            ListViewHolder newHOlder = (ListViewHolder) holder;
            newHOlder.onBindView(row);
        }
    }
}
