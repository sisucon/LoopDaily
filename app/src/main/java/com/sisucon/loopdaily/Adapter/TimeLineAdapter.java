package com.sisucon.loopdaily.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.sisucon.loopdaily.Activity.ActionEventDetailActivity;
import com.sisucon.loopdaily.Activity.MainActivity;
import com.sisucon.loopdaily.Activity.PlanEventDetailActivity;
import com.sisucon.loopdaily.R;
import com.sisucon.loopdaily.Util.TimeLineModel;
import com.sisucon.loopdaily.Util.Utils;
import com.sisucon.loopdaily.ViewHolder.TimeLineViewHolder;
import com.sisucon.loopdaily.lib.OrderStatus;
import com.sisucon.loopdaily.lib.VectorDrawableUtils;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

public class TimeLineAdapter  extends RecyclerView.Adapter<TimeLineViewHolder> {
    private Fragment context;
    private boolean mWithLinePadding;
    private LayoutInflater layoutInflater;
    private List<TimeLineModel> timeLineModelList;
    private OnItemClickCallBack onItemClickCallBack;
    public interface OnItemClickCallBack{
        void onClick(View view,TimeLineModel info);
        void onLongClick(View view,TimeLineModel info);
    }
    public TimeLineAdapter(List<TimeLineModel> timeLineModelList,boolean mWithLinePadding,OnItemClickCallBack onItemClickCallBack,Fragment context){
        this.timeLineModelList = timeLineModelList;
        this.mWithLinePadding = mWithLinePadding;
        this.onItemClickCallBack = onItemClickCallBack;
        this.context = context;
    }

    public void setData(List<TimeLineModel> timeLineModelList){this.timeLineModelList = timeLineModelList;}

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context.getActivity());
        View view = layoutInflater.inflate(mWithLinePadding? R.layout.item_timeline_line_padding:R.layout.item_timeline,parent,false);
        return new TimeLineViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineViewHolder holder, int position) {
         TimeLineModel timeLineModel = timeLineModelList.get(position);
         holder.timelineView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (timeLineModel.getType()==0){
                     Log.d("eventid",""+timeLineModelList.get(position).getEventId());
                     context.startActivityForResult(new Intent(context.getActivity(), ActionEventDetailActivity.class).putExtra("id",""+timeLineModelList.get(position).getEventId()),0);

                 }else {
                     context.startActivityForResult(new Intent(context.getActivity(), PlanEventDetailActivity.class).putExtra("id",""+timeLineModelList.get(position).getEventId()),1);
                 }

             }
         });
        if(timeLineModel.getMstatus() == OrderStatus.INACTIVE) {
            holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(context.getActivity(), R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if(timeLineModel.getMstatus() == OrderStatus.ACTIVE) {
            holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(context.getActivity(), R.drawable.ic_marker_active, R.color.colorPrimary));
        } else {
            holder.timelineView.setMarker(ContextCompat.getDrawable(context.getActivity(), R.drawable.ic_marker), ContextCompat.getColor(context.getActivity(), R.color.colorPrimary));
        }

        setText(Utils.DateToLessionType(timeLineModel.getDate()),holder.getDateText());
        setText(timeLineModel.getName(),holder.getContentText());

    }



    @Override
    public int getItemCount() {
        return (timeLineModelList!=null?timeLineModelList.size():0);
    }

    private void setText(String text, AppCompatTextView view){
        if (text!=null&&!text.equals("")){
            view.setVisibility(View.VISIBLE);
            view.setText(text);
        }else {
            view.setVisibility(View.GONE);
        }
    }
}
