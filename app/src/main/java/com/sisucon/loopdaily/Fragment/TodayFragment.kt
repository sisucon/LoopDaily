package com.sisucon.loopdaily.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sisucon.loopdaily.Activity.AddPlanActivity
import com.sisucon.loopdaily.Adapter.TimeLineAdapter
import com.sisucon.loopdaily.Model.ActionDB
import com.sisucon.loopdaily.Model.ActionEventDB
import com.sisucon.loopdaily.Model.PlanDB
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.Util.ActionClassModel
import com.sisucon.loopdaily.Util.TimeLineModel
import com.sisucon.loopdaily.Util.TimeUtil
import com.sisucon.loopdaily.Util.Utils
import com.sisucon.loopdaily.lib.OrderStatus
import com.jude.easyrecyclerview.EasyRecyclerView
import org.litepal.LitePal
import java.util.*
import kotlin.collections.ArrayList


class TodayFragment : Fragment() {


    lateinit var recyclerView: EasyRecyclerView
    lateinit var rootView:View
    lateinit var timeLineAdapter: TimeLineAdapter
    lateinit var timelinemodelList: ArrayList<TimeLineModel>
    lateinit var actionModelList : List<ActionClassModel>
    lateinit var addButton : ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       rootView = inflater.inflate(R.layout.today_fragment_layout,container,false)
        initView()
        return rootView
    }


    @SuppressLint("WrongConstant")
    fun initView(){
        addButton = rootView.findViewById(R.id.today_add_button)
        addButton.setOnClickListener {
            startActivity(Intent(activity,AddPlanActivity::class.java))
        }
        recyclerView = rootView.findViewById(R.id.today_recyclerView)
        recyclerView.setLayoutManager( LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false))
        timelinemodelList = ArrayList()
        timeLineAdapter = TimeLineAdapter(timelinemodelList,false,null)
        recyclerView.adapter = timeLineAdapter
        setData()
        recyclerView.setRefreshListener {
            timelinemodelList.clear()
            timeLineAdapter.notifyDataSetChanged()
            setData()
            recyclerView.setRefreshing(false)
        }

    }
    fun setData(){
        var date: Date = Date()
        val todayStart = Utils.getStartTime()
        val todayEnd = Utils.getEndTime()
       if(LitePal.count(ActionDB::class.java)>0){
           LitePal.findAll(ActionDB::class.java).forEach {
               var startDay = it.startTime
               val eventList = LitePal.where("startDay = ? and actionId = ?",""+Utils.getStartTime().time,""+it._id).find(ActionEventDB::class.java)
               if (eventList.size>0){
                   for (event in eventList){
                       timelinemodelList.add(TimeLineModel(it.name,event.time,if (event.isSuccess)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it.remoteId,event._id,0))
                   }
               }else{
                   if (todayStart.time-startDay.time>0){
                       var tempTime = (todayStart.time-startDay.time)%it.loopTime
                       var index = 0
                       while (todayEnd.time-(todayStart.time+tempTime)/it.loopTime>=1){
                           val temp =  ActionEventDB(LitePal.count(ActionEventDB::class.java).toLong(),todayStart,index,it._id,it.remoteId,false,Date(todayStart.time+tempTime))
                           temp.save()
                           timelinemodelList.add(TimeLineModel(it.name,Date(todayStart.time+tempTime),OrderStatus.INACTIVE,"",it._id,temp._id,0))
                           tempTime+=it.loopTime
                           index++
                       }
                   }else{
                       var temp = todayEnd.time - startDay.time
                       var index = 0
                       val tempd = ActionEventDB(LitePal.count(ActionEventDB::class.java).toLong(),todayStart,index,it._id,it.remoteId,false,Date(startDay.time))
                       tempd.save()
                       timelinemodelList.add(TimeLineModel(it.name,Date(startDay.time+it.loopTime),OrderStatus.INACTIVE,"",it._id,tempd._id,0))
                       index++
                       while (temp/it.loopTime>=1){
                           temp -= it.loopTime
                           val tempd = ActionEventDB(LitePal.count(ActionEventDB::class.java).toLong(),todayStart,index,it._id,it.remoteId,false,Date(startDay.time+it.loopTime))
                           tempd.save()
                           timelinemodelList.add(TimeLineModel(it.name,Date(startDay.time+it.loopTime),OrderStatus.INACTIVE,"",it._id,tempd._id,0))

                           startDay = Date(startDay.time+it.loopTime)
                           index++
                       }
                   }
               }
           }
       }
        if (LitePal.count(PlanDB::class.java)>0){
            val timeUtil = TimeUtil()
            LitePal.findAll(PlanDB::class.java).forEach {
                if (timeUtil.isToday(it.startTime)){
                    timelinemodelList.add(TimeLineModel(it.name,Date(it.startTime),if (it.isFinish)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it._id,it.remoteId,1))
                }
            }
        }
        timelinemodelList.sortBy {
                it.date
        }
        timeLineAdapter.setData(timelinemodelList)
        timeLineAdapter.notifyDataSetChanged()
    }
}