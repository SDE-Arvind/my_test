package com.example.arvindrathour.analyticsandnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private Button mButtonNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseLog();

        mButtonNotification = (Button) findViewById(R.id.button_send_notification);
//        mButtonNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlarmNotificationView.show("title","messaage",2,null);
//            }
//        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.hello.action");
        registerReceiver(updateUIReciver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateUIReciver);
    }

    void firebaseLog() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "q");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    BroadcastReceiver updateUIReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //UI update here
            mButtonNotification.setText(intent.getStringExtra("data"));
        }
    };
}
