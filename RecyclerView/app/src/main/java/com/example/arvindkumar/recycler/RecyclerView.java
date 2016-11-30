package com.example.arvindkumar.recycler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RecyclerView extends AppCompatActivity {
    private android.support.v7.widget.RecyclerView mRecyclerView;
    private android.support.v7.widget.RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    List<Info> mInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = (android.support.v7.widget.RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new InfoAdapter(mInfoList,getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        prepareInfoData();
        // on Item CLICK

//        mRecyclerView.addOnItemTouchListener(new InfoAdapter(getApplicationContext(), new InfoAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//               Toast.makeText(getApplicationContext()," " + position, Toast.LENGTH_SHORT).show();
//            }
//        }));
    }

    private void prepareInfoData() {
        Info info = new Info(R.mipmap.ic_cover_1);
        mInfoList.add(info);
        info = new Info(R.mipmap.ic_cover_2);
        mInfoList.add(info);
        info = new Info(R.mipmap.ic_cover_3);
        mInfoList.add(info);
        info = new Info(R.mipmap.ic_cover_4);
        mInfoList.add(info);
        info = new Info(R.mipmap.ic_cover_5);
        mInfoList.add(info);
        info = new Info(R.mipmap.ic_cover_6);
        mInfoList.add(info);
        info = new Info(R.mipmap.ic_cover_7);
        mInfoList.add(info);
//        info = new Info("arvind7", "12345");
//        mInfoList.add(info);
//        info = new Info("arvind8", "12345");
//        mInfoList.add(info);
//        info = new Info("arvind9", "12345");
//        mInfoList.add(info);
//        info = new Info("arvind10", "12345");
//        mInfoList.add(info);
//        info = new Info("arvind11", "12345");
//        mInfoList.add(info);
//        info = new Info("arvind12", "12345");
//        mInfoList.add(info);
//        info = new Info("arvind13", "12345");
//        mInfoList.add(info);
//        info = new Info("arvind14", "12345");
//        mInfoList.add(info);
//        info = new Info("arvind15", "12345");
//        mInfoList.add(info);
        mAdapter.notifyDataSetChanged();
    }
}
