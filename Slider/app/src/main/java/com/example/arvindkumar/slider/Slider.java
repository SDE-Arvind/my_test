package com.example.arvindkumar.slider;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Slider extends AppCompatActivity {

    ViewPager mViewPager;
    CustomSwapAdapter mCustomSwapAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        mViewPager =(ViewPager)  findViewById(R.id.pager1);
        mCustomSwapAdapter =new CustomSwapAdapter(this);
        mViewPager.setAdapter(mCustomSwapAdapter);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                int pageCount = mCustomSwapAdapter.getCount();
                Log.e("po, pc ",""+position+"  "+pageCount);
                if (position == 0)
                {
                    mViewPager.setCurrentItem(pageCount-2, false);
                }
                else if (position == pageCount-1) {
                    mViewPager.setCurrentItem(1, false);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
