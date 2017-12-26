package com.example.arvind.countdowntimer;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView mTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextField = (TextView) findViewById(R.id.timer);

        Typeface type = Typeface.createFromAsset(getAssets(),"digital-7.ttf");
        mTextField.setTypeface(type);

        new CountDownTimer(60000, 1500) {
            public void onTick(long millisUntilFinished) {
                mTextField.setText(""+ millisUntilFinished / 1000);
            }

            public void onFinish() {
                mTextField.setText("done!");
            }
        }.start();
    }
}
