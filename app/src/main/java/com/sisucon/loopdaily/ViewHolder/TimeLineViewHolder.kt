package com.sisucon.loopdaily.ViewHolder

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.sisucon.loopdaily.Model.ActionDB
import com.sisucon.loopdaily.R
import com.github.vipulasri.timelineview.TimelineView

class TimeLineViewHolder(itemView: View,viewType:Int) : RecyclerView.ViewHolder(itemView) {
        @kotlin.jvm.JvmField
        var timelineView: TimelineView = itemView.findViewById(R.id.time_marker)
        var dateText:AppCompatTextView
         var placeText:AppCompatTextView
         var contentText:AppCompatTextView
        lateinit var actionDB: ActionDB
        init {
                dateText = itemView.findViewById(R.id.text_timeline_date)
                placeText = itemView.findViewById(R.id.text_timeline_place)
                contentText = itemView.findViewById(R.id.text_timeline_content)
                timelineView.initLine(viewType)
        }

}