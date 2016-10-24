package com.example.arvindkumar.recycler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RecyclerView extends AppCompatActivity {
    private android.support.v7.widget.RecyclerView recyclerView;
    private android.support.v7.widget.RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.RecyclerView.LayoutManager mLayoutManager;
    List<Info> infoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        recyclerView = (android.support.v7.widget.RecyclerView) findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new InfoAdapter(infoList);
        recyclerView.setAdapter(mAdapter);
        prepareInfoData();
        recyclerView.addOnItemTouchListener(new InfoAdapter(getApplicationContext(), new InfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
               Toast.makeText(getApplicationContext()," " + position,Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void prepareInfoData() {

        Info info = new Info("arvind", "12345");
        infoList.add(info);
        info = new Info("arvind1", "12345");
        infoList.add(info);
        info = new Info("arvind2", "12345");
        infoList.add(info);
        info = new Info("arvind3", "12345");
        infoList.add(info);
        info = new Info("arvind4", "12345");
        infoList.add(info);
        info = new Info("arvind5", "12345");
        infoList.add(info);
        info = new Info("arvind1", "12345");
        infoList.add(info);
        info = new Info("arvind2", "12345");
        infoList.add(info);
        info = new Info("arvind3", "12345");
        infoList.add(info);
        info = new Info("arvind4", "12345");
        infoList.add(info);
        info = new Info("arvind5", "12345");
        infoList.add(info);
        info = new Info("arvind1", "12345");
        infoList.add(info);
        info = new Info("arvind2", "12345");
        infoList.add(info);
        info = new Info("arvind3", "12345");
        infoList.add(info);
        info = new Info("arvind4", "12345");
        infoList.add(info);
        info = new Info("arvind5", "12345");
        infoList.add(info);
        mAdapter.notifyDataSetChanged();

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
