package com.example.arvindkumar.recycler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class RecyclerView extends AppCompatActivity {
    private android.support.v7.widget.RecyclerView mRecyclerView;
    private android.support.v7.widget.RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.RecyclerView.LayoutManager mLayoutManager;
    List<Info> mInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = (android.support.v7.widget.RecyclerView) findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new InfoAdapter(mInfoList,getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        prepareInfoData();
        // on Item CLICK

//        mRecyclerView.addOnItemTouchListener(new InfoAdapter(getApplicationContext(), new InfoAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//               Toast.makeText(getApplicationContext()," " + position,Toast.LENGTH_SHORT).show();
//            }
//        }));
    }

    private void prepareInfoData() {
        Info info = new Info("arvind", "12345");
        mInfoList.add(info);
        info = new Info("arvind1", "12345");
        mInfoList.add(info);
        info = new Info("arvind2", "12345");
        mInfoList.add(info);
        info = new Info("arvind3", "12345");
        mInfoList.add(info);
        info = new Info("arvind4", "12345");
        mInfoList.add(info);
        info = new Info("arvind5", "12345");
        mInfoList.add(info);
        info = new Info("arvind6", "12345");
        mInfoList.add(info);
        info = new Info("arvind7", "12345");
        mInfoList.add(info);
        info = new Info("arvind8", "12345");
        mInfoList.add(info);
        info = new Info("arvind9", "12345");
        mInfoList.add(info);
        info = new Info("arvind10", "12345");
        mInfoList.add(info);
        info = new Info("arvind11", "12345");
        mInfoList.add(info);
        info = new Info("arvind12", "12345");
        mInfoList.add(info);
        info = new Info("arvind13", "12345");
        mInfoList.add(info);
        info = new Info("arvind14", "12345");
        mInfoList.add(info);
        info = new Info("arvind15", "12345");
        mInfoList.add(info);
        mAdapter.notifyDataSetChanged();
    }
}
