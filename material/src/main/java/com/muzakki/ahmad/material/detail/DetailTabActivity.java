package com.muzakki.ahmad.material.detail;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.muzakki.ahmad.material.R;

import java.util.ArrayList;


/**
 * Created by jeki on 6/9/16.
 */
public abstract class DetailTabActivity extends DetailActivity
        implements AppBarLayout.OnOffsetChangedListener {


    @Override
    protected int getLayout() {
        return R.layout.layout_detail_tab;
    }

    protected void render(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ArrayList<String> tabs = getTabs();
        for(String tab: tabs){
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        DetailPagerAdapter adapter = new DetailPagerAdapter(getSupportFragmentManager(),getTabs());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabDetailListener(viewPager));
    }

    @Override
    protected void setTitleSubtitle(String title, String subtitle) {
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

    }

    protected ArrayList<String> getTabs(){
        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("Detail");
        return tabs;
    }

    protected View getTabView(int i){
        Detail detail = getDetail();
        detail.render();
        return detail;
    }

    private class DetailPagerAdapter extends FragmentStatePagerAdapter{

        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> tabs;

        public DetailPagerAdapter(FragmentManager fm, ArrayList<String> tabs) {
            super(fm);
            this.tabs = tabs;
            fragments = new ArrayList<>(tabs.size());
        }

        /**
         * Return the Fragment associated with a specified position.
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {
            try{
                if(fragments.get(position)==null) throw new NullPointerException();
            }catch (NullPointerException | IndexOutOfBoundsException e){
                fragments.add(position,
                        DetailFragment.newInstance(position,DetailTabActivity.this));
            }
            return fragments.get(position);
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return tabs.size();
        }
    }

    private class TabDetailListener implements TabLayout.OnTabSelectedListener {
        private final ViewPager vp;

        TabDetailListener(ViewPager vp){
            this.vp = vp;
        }

        /**
         * Called when a tab enters the selected state.
         *
         * @param tab The tab that was selected
         */
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            vp.setCurrentItem(tab.getPosition());
        }

        /**
         * Called when a tab exits the selected state.
         *
         * @param tab The tab that was unselected
         */
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        /**
         * Called when a tab that is already selected is chosen again by the user. Some applications
         * may use this action to return to the top level of a category.
         *
         * @param tab The tab that was reselected.
         */
        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
