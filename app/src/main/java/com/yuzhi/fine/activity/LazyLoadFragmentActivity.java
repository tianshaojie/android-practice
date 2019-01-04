package com.yuzhi.fine.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.yuzhi.fine.R;
import com.yuzhi.fine.library.base.activity.BaseSwipeBackActivity;
import com.yuzhi.fine.widgets.PagerSlidingTabStrip;

public class LazyLoadFragmentActivity extends BaseSwipeBackActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyStockPagerAdapter adapter;

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

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class MyStockPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"全部", "港股", "美股", "沪深"};

        public MyStockPagerAdapter(FragmentManager fm) {
            super(fm);
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
        public Fragment getItem(int position) {
            return MyStockFragment.newInstance(TITLES[position]);
        }

    }
}
