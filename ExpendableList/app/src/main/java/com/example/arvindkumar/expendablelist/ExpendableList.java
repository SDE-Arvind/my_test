package com.example.arvindkumar.expendablelist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import java.util.ArrayList;

public class ExpendableList extends AppCompatActivity {
/**
 skjhsjdshbasjznsjndxnsznvjhvjhv

 **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expendable_list);
        ExpandableListView el = (ExpandableListView) findViewById(R.id.expendable_list);
        ArrayList<Team> team=getData();

        CustomAdapter adapter=new CustomAdapter(this,team);

        el.setAdapter(adapter);

    }

    private ArrayList<Team> getData() {

        Team t1 = new Team("india");
        t1.players.add("aa");
        t1.players.add("aaa");
        t1.players.add("aaaa");


        Team t2 = new Team("india2");
        t2.players.add("bb");
        t2.players.add("bbb");
        t2.players.add("bbbb");
        ArrayList<Team> allteams = new ArrayList<>();
        allteams.add(t1);
        allteams.add(t2);
        return allteams;
    }
}
