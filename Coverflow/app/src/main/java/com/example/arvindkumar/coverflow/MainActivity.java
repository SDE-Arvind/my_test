package com.example.arvindkumar.coverflow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.LinkagePager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.LinkagePagerContainer;
import me.crosswall.lib.coverflow.core.PageItemClickListener;

public class MainActivity extends AppCompatActivity {

    private LinkagePagerContainer customPagerContainer;
    private LinkagePager pager;
    private TextView t;
//    private View tab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t=(TextView) findViewById(R.id.text) ;

        customPagerContainer = (LinkagePagerContainer) findViewById(R.id.pager_container);

        customPagerContainer.setPageItemClickListener(new PageItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                pager.setCurrentItem(position);   //
                t.setText("my position is :"+position);
            }
        });


        final LinkagePager cover = customPagerContainer.getViewPager();

        PagerAdapter coverAdapter = new MyPagerAdapter();
        cover.setAdapter(coverAdapter);
        cover.setOffscreenPageLimit(5);

        new CoverFlow.Builder()
                .withLinkage(cover)
                .pagerMargin(0f)
                .scale(0.1f)
                .spaceSize(0f)
                .build();
  // scale --- space b/w two image horizontally

//
//        pager = (LinkagePager) findViewById(R.id.pager);
//        MyListPagerAdapter adapter = new MyListPagerAdapter();

//        pager.setOffscreenPageLimit(Data.covers.length);
//        pager.setAdapter(adapter);

//        cover.setLinkagePager(pager);
//        pager.setLinkagePager(cover);
    }

//    class MyListPagerAdapter extends PagerAdapter {
//
//        @Override
//        public int getCount() {
//            return Data.covers.length;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//
//            DataDemoView view = new DataDemoView(MainActivity.this);
//            container.addView(view);
//            return view;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView view = new TextView(MainActivity.this);
            view.setGravity(Gravity.CENTER);
            view.setBackgroundResource(Data.covers[position]);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        @Override
        public int getCount() {
            return Data.covers.length;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }
}
