package com.example.arvindkumar.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arvind Kumar on 24-Oct-16.
 */

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.CardViewHolder> {

    List<Info> infoList = new ArrayList<>();

    InfoAdapter(List<Info> infoList) {
        this.infoList = infoList;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        protected TextView mName;
        protected TextView mMobile;

        public CardViewHolder(View itemView) {
            super(itemView);
            this.mName = (TextView) itemView.findViewById(R.id.name);
            this.mMobile = (TextView) itemView.findViewById(R.id.mobile_no);
        }


    }
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);

        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Info info = infoList.get(position);
        holder.mName.setText(info.getName());
        holder.mMobile.setText(info.getMobile());
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }



}
