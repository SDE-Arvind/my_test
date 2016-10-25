package com.example.arvindkumar.slider;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Arvind Kumar on 24-Oct-16.
 */

public class CustomSwapAdapter extends PagerAdapter {
    private int[] image_resources = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h};
    Context mContext;
    LayoutInflater mLayoutInflater;

    CustomSwapAdapter(Context iContext) {
        mContext = iContext;
        //       MAKE PAGER CIRCULAR

    }

    @Override
    public int getCount() {
        return image_resources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = mLayoutInflater.inflate(R.layout.swap_layout, container, false);
        ImageView imageView = (ImageView) item_view.findViewById(R.id.imageView);
        TextView textView = (TextView) item_view.findViewById(R.id.textView);
        textView.setText("Image : " + position);
        Log.e("log", "Image position: " + position);


        Picasso.with(mContext)
                .load(image_resources[position])
//                .resize(200,200)
//                .centerCrop()
                .into(imageView);
//        imageView.setImageResource(image_resources[position]);
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
