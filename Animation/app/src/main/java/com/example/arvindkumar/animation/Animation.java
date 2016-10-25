package com.example.arvindkumar.animation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Animation extends AppCompatActivity implements View.OnClickListener {
    private Button animOne;
    private Button animTwo;
    private Button animThree;
    private Button animFour;
    private Button animFive;
    private Button animZoom;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        animOne = (Button) findViewById(R.id.button_anim_one);
        animTwo = (Button) findViewById(R.id.button_anim_two);
        animThree = (Button) findViewById(R.id.button_anim_three);
        animFour = (Button) findViewById(R.id.button_anim_four);
        animFive = (Button) findViewById(R.id.button_anim_five);
        animZoom = (Button) findViewById(R.id.button_anim_zoom);

        intent = new Intent(getApplicationContext(), SecondActivity.class);

        animOne.setOnClickListener(this);
        animTwo.setOnClickListener(this);
        animThree.setOnClickListener(this);
        animFour.setOnClickListener(this);
        animFive.setOnClickListener(this);
        animZoom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_anim_one:

                startActivity(intent);
                overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                break;
            case R.id.button_anim_two:
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in ,R.anim.trans_left_out);
                break;
            case R.id.button_anim_three:
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
            case R.id.button_anim_four:
                startActivity(intent);
                overridePendingTransition(R.anim.trans_buttom_in, R.anim.trans_button_out);
                break;
            case R.id.button_anim_five:
                startActivity(intent);
                overridePendingTransition(R.anim.trans_top_in, R.anim.trans_top_out);
                break;
            case R.id.button_anim_zoom:
                startActivity(intent);
                overridePendingTransition(R.anim.trans_zoom_in, R.anim.trans_zoom_out);
                break;
        }
    }
}
