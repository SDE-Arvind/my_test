package com.example.arvindkumar.animationtwo;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnScale;
    private Button mBtnTranslate;
    private Button mBtnRotate;
    private Button mBtnAlpha;
    private Button mBtnSet;
    private ImageView mIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mBtnAlpha = (Button) findViewById(R.id.btn_alpha);
        mBtnScale = (Button) findViewById(R.id.btn_scale);
        mBtnTranslate = (Button) findViewById(R.id.btn_transform);
        mBtnRotate = (Button) findViewById(R.id.btn_rotate);
        mBtnSet = (Button) findViewById(R.id.btn_set);
        mIcon = (ImageView) findViewById(R.id.iv_icon);

        mBtnAlpha.setOnClickListener(this);
        mBtnScale.setOnClickListener(this);
        mBtnTranslate.setOnClickListener(this);
        mBtnRotate.setOnClickListener(this);
        mBtnSet.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Animator anim = null;
        switch (v.getId()) {
            case R.id.btn_alpha:
                anim = AnimatorInflater.loadAnimator(this, R.animator.alpha);
                break;
            case R.id.btn_scale:
                anim = AnimatorInflater.loadAnimator(this, R.animator.scale);
                break;
            case R.id.btn_transform:
                anim = AnimatorInflater.loadAnimator(this, R.animator.translate);
                break;
            case R.id.btn_rotate:
                anim = AnimatorInflater.loadAnimator(this, R.animator.rotate);
                break;
            case R.id.btn_set:
                anim = AnimatorInflater.loadAnimator(this, R.animator.set);
                break;
        }
        anim.setTarget(mIcon);
        anim.start();
    }
}
