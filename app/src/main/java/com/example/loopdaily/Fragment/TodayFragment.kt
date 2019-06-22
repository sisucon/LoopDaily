package com.example.loopdaily.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdaily.Activity.ActionEventDetailActivity
import com.example.loopdaily.Adapter.TimeLineAdapter
import com.example.loopdaily.Model.ActionDB
import com.example.loopdaily.Model.ActionEventDB
import com.example.loopdaily.Model.ActionModel
import com.example.loopdaily.R
import com.example.loopdaily.Util.ActionClassModel
import com.example.loopdaily.Util.TimeLineModel
import com.example.loopdaily.Util.Utils
import com.example.loopdaily.lib.OrderStatus
import com.jude.easyrecyclerview.EasyRecyclerView
import org.litepal.LitePal
import org.litepal.crud.LitePalSupport
import java.util.*
import kotlin.collections.ArrayList


class TodayFragment : Fragment() {


    lateinit var recyclerView: EasyRecyclerView
    lateinit var rootView:View
    lateinit var timeLineAdapter: TimeLineAdapter
    lateinit var timelinemodelList: ArrayList<TimeLineModel>
    lateinit var actionModelList : List<ActionClassModel>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       rootView = inflater.inflate(R.layout.today_fragment_layout,container,false)
        initView()
        return rootView
    }


    @SuppressLint("WrongConstant")
    fun initView(){
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
        LitePal.findAll(ActionDB::class.java).forEach {
            var startDay = it.startTime
            val eventList = LitePal.where("startDay = ? and actionId = ?",""+Utils.getStartTime().time,""+it._id).find(ActionEventDB::class.java)
            if (eventList.size>0){
                for (event in eventList){
                    timelinemodelList.add(TimeLineModel(it.name,event.time,if (event.isSuccess)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it.remoteId,event._id))
                }
            }else{
                if (todayStart.time-startDay.time>0){
                    var tempTime = (todayStart.time-startDay.time)%it.loopTime
                    var index = 0
                    while (todayEnd.time-(todayStart.time+tempTime)/it.loopTime>=1){
                        val temp =  ActionEventDB(LitePal.count(ActionEventDB::class.java).toLong(),todayStart,index,it._id,it.remoteId,false,Date(todayStart.time+tempTime))
                        temp.save()
                        timelinemodelList.add(TimeLineModel(it.name,Date(todayStart.time+tempTime),OrderStatus.INACTIVE,"",it._id,temp._id))
                        tempTime+=it.loopTime
                        index++
                    }
                }else{
                    var temp = todayEnd.time - startDay.time
                    var index = 0
                    val tempd = ActionEventDB(LitePal.count(ActionEventDB::class.java).toLong(),todayStart,index,it._id,it.remoteId,false,Date(startDay.time))
                    tempd.save()
                    timelinemodelList.add(TimeLineModel(it.name,Date(startDay.time+it.loopTime),OrderStatus.INACTIVE,"",it._id,tempd._id))
                    index++
                    while (temp/it.loopTime>=1){
                        temp -= it.loopTime
                      val tempd = ActionEventDB(LitePal.count(ActionEventDB::class.java).toLong(),todayStart,index,it._id,it.remoteId,false,Date(startDay.time+it.loopTime))
                        tempd.save()
                        timelinemodelList.add(TimeLineModel(it.name,Date(startDay.time+it.loopTime),OrderStatus.INACTIVE,"",it._id,tempd._id))

                        startDay = Date(startDay.time+it.loopTime)
                        index++
                    }
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