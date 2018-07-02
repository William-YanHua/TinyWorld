package com.gyg.lenovo.world;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;

public class BottomNavigation extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager vpager;
    private ArrayList<Fragment> aList;
    private MultiPages mAdapter;
    private BottomNavigationView navigation;
    private MyApp myApp;
//    private Fragment fragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    vpager.setCurrentItem(0);
                    break;
                case R.id.navigation_mine:
                    vpager.setCurrentItem(4);
                    break;
                case R.id.navigation_search:
                    vpager.setCurrentItem(2);
                    break;
                case R.id.navigation_invite:
                    vpager.setCurrentItem(1);
                    break;
                case R.id.navigation_message:
                    vpager.setCurrentItem(3);
                    break;
            }
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        myApp = (MyApp) getApplication();
        CheckIn();
        navigation = (BottomNavigationView) findViewById(R.id.bottomNavi);
        vpager = (ViewPager) findViewById(R.id.pager);
        aList = new ArrayList<Fragment>();
        LayoutInflater li = getLayoutInflater();
        aList.add(new HomePageFragment());
        aList.add(new InvitePageFragment());
        aList.add(new SearchFragment());
        aList.add(new MessageFragment());
        aList.add(new MineFragment());
        mAdapter = new MultiPages(getSupportFragmentManager(),aList);
        vpager.setAdapter(mAdapter);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        vpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("position",String.valueOf(position));
                navigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getBundleExtra("data");
            Integer pages = bundle.getInt("fragment");
            vpager.setCurrentItem(pages);
        }
        catch (Exception e){

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {

    }

    private void CheckIn() {
        if(!myApp.isIn) {
            Intent intent = new Intent();
            intent.setClass(BottomNavigation.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
