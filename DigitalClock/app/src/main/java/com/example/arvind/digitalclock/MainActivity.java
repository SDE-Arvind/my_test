package com.example.arvind.digitalclock;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    Time mTime;
    Handler handler;
    Runnable r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTime=new Time();

        r=new Runnable() {
            @Override
            public void run() {

            }
        };
        handler=new Handler( );
        handler.postDelayed(r,1000);
    }


    private float getBetteryLevel(){
        Intent betteryIntent=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


           int level=betteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
           int scale=betteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

        if(level==-1||scale==-1)
            return 50.0f;

        return  ((float)level/(float)scale)*100f;
    }

    public class DrawView extends View{

        Typeface tf;
        public DrawView(Context context) {
            super(context);
        }
    }
}
