package com.example.arvindkumar.slider;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Slider extends AppCompatActivity {

    ViewPager viewPager;
    CustomSwapAdapter customSwapAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        Log.e("log", "test");
        viewPager=(ViewPager)  findViewById(R.id.pager1);
        customSwapAdapter=new CustomSwapAdapter(this);
        viewPager.setAdapter(customSwapAdapter);
    }

}
