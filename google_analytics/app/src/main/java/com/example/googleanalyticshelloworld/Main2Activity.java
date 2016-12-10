package com.example.googleanalyticshelloworld;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;

public class Main2Activity extends ActionBarActivity {
Button mGoBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mGoBack = (Button) findViewById(R.id.btn_go_back);
        mGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyApp.tracker().send(new HitBuilders.EventBuilder()
                        .setAction("Click")
                        .setCategory("Button2")
                        .setLabel("clicked back")
                        .setValue(10)
                        .build()
                );
                startActivity(new Intent(Main2Activity.this, MainActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.tracker().setScreenName("second");
        MyApp.tracker().send(new HitBuilders.ScreenViewBuilder().build());;
    }
}
