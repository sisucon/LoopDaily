package com.sisucon.loopdaily.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.sisucon.loopdaily.Activity.ActionEventDetailActivity;
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
    private Context context;
    private boolean mWithLinePadding;
    private LayoutInflater layoutInflater;
    private List<TimeLineModel> timeLineModelList;
    private OnItemClickCallBack onItemClickCallBack;
    public interface OnItemClickCallBack{
        void onClick(View view,TimeLineModel info);
        void onLongClick(View view,TimeLineModel info);
    }
    public TimeLineAdapter(List<TimeLineModel> timeLineModelList,boolean mWithLinePadding,OnItemClickCallBack onItemClickCallBack){
        this.timeLineModelList = timeLineModelList;
        this.mWithLinePadding = mWithLinePadding;
        this.onItemClickCallBack = onItemClickCallBack;
    }

    public void setData(List<TimeLineModel> timeLineModelList){this.timeLineModelList = timeLineModelList;}

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        layoutInflater = LayoutInflater.from(context);
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
                     context.startActivity(new Intent(context, ActionEventDetailActivity.class).putExtra("id",""+timeLineModelList.get(position).getEventId()));
                 }else {
                     context.startActivity(new Intent(context, PlanEventDetailActivity.class).putExtra("id",""+timeLineModelList.get(position).getEventId()));
                 }

             }
         });
        if(timeLineModel.getMstatus() == OrderStatus.INACTIVE) {
            holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if(timeLineModel.getMstatus() == OrderStatus.ACTIVE) {
            holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_active, R.color.colorPrimary));
        } else {
            holder.timelineView.setMarker(ContextCompat.getDrawable(context, R.drawable.ic_marker), ContextCompat.getColor(context, R.color.colorPrimary));
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
