package com.example.arvindkumar.expendablelist;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Arvind Kumar on 29-Sep-16.
 */

public class CustomAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<Team> mTeams;
    private LayoutInflater inflater;

    public CustomAdapter(Context context, ArrayList<Team> teams) {
        this.mContext = context;
        this.mTeams = teams;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return mTeams.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return mTeams.get(groupPosition).players.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mTeams.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return  mTeams.get(groupPosition).players.get(childPosition);              //////////////////////////////////////////////
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0 ;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

         if(convertView==null)
         {
          convertView=inflater.inflate(R.layout.team,null);
         }
       Team t= (Team) getGroup(groupPosition);

        TextView teamName=(TextView) convertView.findViewById(R.id.team_textView);
        teamName.setText(t.name);
        convertView.setBackgroundColor(Color.GRAY);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            convertView=inflater.inflate(R.layout.players,null);        ///////////////////////////////////////////////////////
        }
        String child=(String) getChild(groupPosition,childPosition);

        TextView player=(TextView) convertView.findViewById(R.id.player_textView);
        player.setText(child);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
