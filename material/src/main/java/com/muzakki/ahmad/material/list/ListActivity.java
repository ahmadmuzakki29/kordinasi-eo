package com.muzakki.ahmad.material.list;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.muzakki.ahmad.material.R;

import org.apache.commons.lang3.NotImplementedException;

/**
 * Created by jeki on 6/3/16.
 */
public abstract class ListActivity extends AppCompatActivity implements List.Listener,
        SwipeRefreshLayout.OnRefreshListener {
    private List list;
    protected final int ADD_ACTIVITY = 1;
    protected final int DETAIL_ACTIVITY = 2;
    private SwipeRefreshLayout parent;
    private SearchView searchView;
    protected boolean isSearchable = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getTitleList());
        }
        parent = ((SwipeRefreshLayout) findViewById(R.id.list_wrap));
        int[] colors = new int[]{R.color.primary,android.R.color.holo_red_light,android.R.color.holo_blue_light};
        parent.setColorSchemeResources(colors);
        parent.setOnRefreshListener(this);

        list = getList();

        RelativeLayout info = ((RelativeLayout) findViewById(R.id.list_info));
        info.addView(list.getEmptyView());
        info.addView(list.getTimeOutView());

        parent.addView(list);
    }

    protected void render(){
        list.render();
    }

    protected abstract String getTitleList();

    protected abstract List getList();

    public void setLoading(final boolean loading){
        parent.post(new Runnable() {
            @Override
            public void run() {
                parent.setRefreshing(loading);
            }
        });
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

    protected void setQueryHint(String hint){
        searchView.setQueryHint(hint);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){ // ok  from add or detail
            list.render();
        }
    }

    @Override
    public void onRefresh() {
        list.refresh();
    }
}
