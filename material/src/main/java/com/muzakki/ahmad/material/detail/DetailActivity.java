package com.muzakki.ahmad.material.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.muzakki.ahmad.material.R;

import org.apache.commons.lang3.NotImplementedException;


/**
 * Created by jeki on 6/9/16.
 */
public abstract class DetailActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {
    private boolean isHideToolbarView = false;
    protected final int START_ACTIVITY_EDIT = 1;

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private HeaderView toolbarHeaderView;
    private HeaderView floatHeaderView;
    private ImageView image;
    private Detail detail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarHeaderView = (HeaderView) findViewById(R.id.toolbar_header_view);
        floatHeaderView = (HeaderView) findViewById(R.id.float_header_view);
        image = (ImageView) findViewById(R.id.image);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle(" ");

        appBarLayout.addOnOffsetChangedListener(this);

    }

    protected void setTitleSubtitle(String title,String subtitle){
        toolbarHeaderView.bindTo(title, subtitle);
        floatHeaderView.bindTo(title, subtitle);
    }

    protected void setCoverImage(Bitmap bm){
        image.setImageBitmap(bm);
    }

    protected void setDetail(Detail detail){
        ViewGroup parent = (ViewGroup) findViewById(R.id.scroll);
        this.detail = detail;
        parent.addView(detail);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }else if(id==R.id.menu_edit){
            onMenuEdit();
        }else if(id==R.id.menu_delete){
            onMenuDelete();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==START_ACTIVITY_EDIT&&resultCode==RESULT_OK){
            detail.render();
            setResult(RESULT_OK);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onMenuDelete(){
        throw new NotImplementedException("on delete click not implemented");
    }

    protected void onMenuEdit(){
        throw new NotImplementedException("on edit click not implemented");
    }
}
