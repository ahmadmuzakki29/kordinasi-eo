package com.muzakki.ahmad.material;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jeki on 6/7/16.
 * this is item for grid or list
 */
public class ItemView {

    private ImageView picture;
    private TextView title;
    private View description;
    private View view;
    private int layout ;


    public ItemView(ViewGroup parent, int layout){
        this.layout = layout;
        initComponent(parent);
    }

    private void initComponent(ViewGroup parent) {
        view = LayoutInflater.from(parent.getContext()).
                inflate(layout, parent, false);

        picture = (ImageView) view.findViewById(R.id.picture);
        title = (TextView) view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
    }

    public ImageView getPicture() {
        return picture;
    }

    public TextView getTitle() {
        return title;
    }

    public View getDescription() {
        return description;
    }

    public View getView() {
        return view;
    }
}
