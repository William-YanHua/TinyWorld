package com.gyg.lenovo.world;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 24448 on 2018/6/10.
 */

public class MultiPages extends FragmentPagerAdapter {
    private ArrayList<Fragment> viewLists;
    public MultiPages(FragmentManager fm, ArrayList<Fragment>vl){
        super(fm);
        this.viewLists = vl;
    }

    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public Fragment getItem(int position) {
        return viewLists.get(position);
    }
}
