package com.muzakki.ahmad.material.detail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.material.R;
import com.muzakki.ahmad.material.form.Field;
import com.muzakki.ahmad.material.form.Fields;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by jeki on 6/13/16.
 */
public abstract class Detail extends LinearLayout {
    private final String id;
    private final DetailActivity ctx;

    public Detail(DetailActivity context, String id) {
        super(context);
        this.ctx = context;
        this.id = id;
    }

    public abstract void render();

    protected abstract String getTitle(Bundle data);
    protected abstract String getSubtitle(Bundle data);
    protected abstract Bitmap getImage(Bundle data);

    public String getIdData() {
        return id;
    }

    protected String getPickerValue(Field field){
        throw new NotImplementedException("picker "+field.getName()+" not implemented");
    }

    protected void initComponent(Fields fields, Bundle data) {
        setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);

        // init value
        for(Field field: fields){
            String name = field.getName();
            String val = data.getString(name);
            field.setValue(val);
        }
        ctx.setTitleSubtitle(getTitle(data),getSubtitle(data));
        //ctx.setCoverImage(getImage(data));

        ListIterator<Field> iterator = fields.listIterator();

        Fields cardGroup = new Fields();
        ArrayList<Fields> listCardGroup = new ArrayList<>();
        while (iterator.hasNext()){
            Field field = iterator.next();

            if(field.getType()!= Field.Type.SEPARATOR){
                cardGroup.add(field);
            }else{
                listCardGroup.add(cardGroup);
                cardGroup = new Fields();
            }
        }
        listCardGroup.add(cardGroup);

        for(Fields group: listCardGroup){
            CardView cv = new CardView(ctx);
            LayoutParams lpcard = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lpcard.setMargins(getDp(10),0,getDp(10),getDp(15));
            cv.setLayoutParams(lpcard);
            cv.setRadius(getDp(10));

            LinearLayout wrap = new LinearLayout(ctx);
            wrap.setOrientation(VERTICAL);
            LayoutParams lpwrap = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lpwrap.setMargins(getDp(20),getDp(20),getDp(20),getDp(20));
            wrap.setLayoutParams(lpwrap);

            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(getDp(10),getDp(10),getDp(10),getDp(10));
            for(Field field: group) {
                TextView tv = new TextView(new ContextThemeWrapper(ctx, R.style.TextMedium), null, 0);
                tv.setLayoutParams(lp);
                field.setRequired(false);
                String value = getValue(field);

                tv.setText(field.getTitle()+" : "+value);
                wrap.addView(tv);
            }

            cv.addView(wrap);
            addView(cv);
        }
    }


    private String getValue(Field field){
        String value = field.getValue();
        switch (field.getType()) {
            case DATE:
                return Helper.decodeDate(value);
            case PICKER:
                return getPickerValue(field);
            default: return value;
        }
    }

    private int getDp(int dp){
        return Helper.getPxFromDp(dp,ctx.getResources());
    }

}
