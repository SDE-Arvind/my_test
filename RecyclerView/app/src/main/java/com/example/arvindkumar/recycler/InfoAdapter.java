package com.example.arvindkumar.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arvind Kumar on 24-Oct-16.
 * Adapter for recyclerview to set data and manage onClickItem Listener
 */

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {//implements RecyclerView.OnItemTouchListener

    List<Info> mInfoList = new ArrayList<>();
    Context mContext;

    InfoAdapter(List<Info> iInfoList, Context iContext) {

        this.mInfoList = iInfoList;
        mContext=iContext;
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

//    private GestureDetector mGestureDetector;

//    public InfoAdapter(Context context, OnItemClickListener listener) {
//        mListener = listener;
//        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return true;
//            }
//        });
//    }

//    @Override
//    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
//        View childView = view.findChildViewUnder(e.getX(), e.getY());
//        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
//            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
//        }
//        return false;
//    }
//
//    @Override
//    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//
//    }
//
//    @Override
//    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//    }

    /**
     * Card view holder class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mName;
        protected TextView mMobile;
        protected ImageView mImage;

        public ViewHolder(View itemView) {
            super(itemView);

            // ON ITEM CLICK

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.e("click", "" + position);
                    Toast.makeText(mContext,"U click on: " + position,Toast.LENGTH_SHORT).show();
                }
            });

//            this.mName = (TextView) itemView.findViewById(R.id.name);
//            this.mMobile = (TextView) itemView.findViewById(R.id.mobile_no);
            this.mImage=(ImageView) itemView.findViewById(R.id.imageView);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Info info = mInfoList.get(position);
//        holder.mName.setText(info.getName());
//        holder.mMobile.setText(info.getMobile());
        holder.mImage.setImageResource(info.getImage());
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

}
