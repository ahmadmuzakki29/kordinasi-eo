package com.muzakki.ahmad.material.detail;

import android.os.Bundle;

import com.muzakki.ahmad.material.form.Fields;


/**
 * Created by jeki on 6/9/16.
 */
public abstract class DetailLocal extends Detail{
    private final Fields fields;
    private final DetailActivity ctx;


    public DetailLocal(DetailActivity ctx,String id, Fields fields){
        super(ctx,id);
        this.ctx = ctx;
        this.fields = fields;
    }

    @Override
    public void render() {
        removeAllViews();
        initValue(getIdData());
    }

    private void initValue(String id){
        DetailModel model = getLocalModel();
        Bundle data = model.getDetail(id);
        ctx.setTitleSubtitle(getTitle(data),getSubtitle(data));
        ctx.setCoverImage(getImage(data));
        initComponent(fields,data);
    }


    protected abstract DetailModel getLocalModel();

}
