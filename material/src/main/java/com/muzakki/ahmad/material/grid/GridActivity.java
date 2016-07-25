package com.muzakki.ahmad.material.grid;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.muzakki.ahmad.material.R;

import org.apache.commons.lang3.NotImplementedException;

/**
 * Created by jeki on 6/16/16.
 */
public abstract class GridActivity extends AppCompatActivity implements Grid.Listener,
        SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipe;
    private InteractiveScrollView scroll;
    private RelativeLayout info;
    private Grid grid;
    private RelativeLayout wrap;
    private View loading;
    protected boolean isSearchable = false;
    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_grid);
        swipe = (SwipeRefreshLayout) findViewById(R.id.grid_swipe);
        scroll = (InteractiveScrollView) findViewById(R.id.grid_scroll);
        wrap = (RelativeLayout) findViewById(R.id.grid_wrap);
        info = (RelativeLayout) findViewById(R.id.grid_info);
        loading = findViewById(R.id.loading);

        swipe.setOnRefreshListener(this);
        int[] colors = new int[]{R.color.primary,android.R.color.holo_red_light,android.R.color.holo_blue_light};
        swipe.setColorSchemeResources(colors);


        grid = getGrid();

        wrap.addView(grid);
        info.addView(grid.getEmptyView());
        info.addView(grid.getTimeOutView());
    }

    protected void render(){
        grid.render();
    }

    public InteractiveScrollView getScrollView(){
        return scroll;
    }

    public void setLoading(final boolean loading){
        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(loading);
            }
        });
    }

    public void setLoadingBottom(boolean isLoading){
        loading.setVisibility(isLoading?View.VISIBLE:View.GONE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        if(isSearchable) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_search, menu);

            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            // Assumes current activity is the searchable activity
            searchView.setQueryHint("Cari...");
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            SearchListener sl = new SearchListener();
            searchView.setOnQueryTextListener(sl);

            MenuItem menuSearch = menu.findItem(R.id.menu_search);
            MenuItemCompat.setOnActionExpandListener(menuSearch, sl);
            return true;
        }
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return false;
    }

    private class SearchListener implements SearchView.OnQueryTextListener,
            MenuItemCompat.OnActionExpandListener{

        @Override
        public boolean onQueryTextSubmit(String query) {
            onKeywordChange(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            onKeywordChange("");
            return true;
        }
    }

    protected void onKeywordChange(String keyword) {
        throw new NotImplementedException("onkeywordchange not implemented : "+keyword);
    }

    protected abstract Grid getGrid();

    @Override
    public void onRefresh() {
        grid.refresh();
    }

    public void scrollToTop(){
        scroll.fullScroll(ScrollView.FOCUS_UP);
    }
}
