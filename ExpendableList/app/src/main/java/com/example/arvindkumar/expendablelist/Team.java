package com.example.arvindkumar.expendablelist;

import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Arvind Kumar on 29-Sep-16.
 */

public class Team {
    String name;
    String image;
    ArrayList<String>  players=new ArrayList<>();

    public Team(String name)
    {
       this.name =name;
    }

    @Override
    public String toString() {
        return name;
    }
}
