package com.yuzhi.fine.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.yuzhi.fine.R;
import com.yuzhi.fine.library.base.activity.BaseSwipeBackActivity;
import com.yuzhi.fine.widgets.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

public class LazyLoadFragmentActivity extends BaseSwipeBackActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyStockPagerAdapter adapter;

    private int prevFragment = 0;
    private int currFragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lazy_load_fragment);

        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);
        adapter = new MyStockPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Logger.i("position = %s, positionOffset = %s", position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                Logger.i("position = %s", position);
                MyStockFragment myStockFragment = adapter.getItem(prevFragment);
                if(myStockFragment != null) {
                    myStockFragment.onHide();
                }
                adapter.getItem(position).onShow();

                prevFragment = currFragment;
                currFragment = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Logger.i("state = %s", state);
            }
        });
        pager.setOffscreenPageLimit(3);
        pager.post(() -> adapter.getItem(0).onShow());
    }

    public class MyStockPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"全部", "港股", "美股", "沪深"};
        private List<MyStockFragment> fragments;

        public MyStockPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>(TITLES.length);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public MyStockFragment getItem(int position) {
            Logger.i("getItem position = %d", position);
            if(fragments.size() > position && fragments.get(position) != null) {
                return fragments.get(position);
            } else {
                MyStockFragment myStockFragment =  MyStockFragment.newInstance(TITLES[position]);
                fragments.add(position, myStockFragment);
                return myStockFragment;
            }
        }

    }
}
