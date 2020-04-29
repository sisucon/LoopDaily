package com.sisucon.loopdaily.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.sisucon.loopdaily.lib.OrderStatus
import com.jude.easyrecyclerview.EasyRecyclerView
import com.sisucon.loopdaily.Model.PlanEventDB
import com.sisucon.loopdaily.Util.*
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
        //设置刷新监听器
        recyclerView.setRefreshListener {
            timelinemodelList.clear()
            timeLineAdapter.notifyDataSetChanged()
            setData()
            recyclerView.setRefreshing(false)
        }
    }
    //更新数据
    fun syncDate(){
            NetUtil.getPlanDB(getString(R.string.server_host)+"/plan/getPlan")
    }

    private val todayStart = Utils.getStartTime()
    private val todayEnd = Utils.getEndTime()
    private fun setData(){
        timelinemodelList.clear()
            Thread(Runnable {
                syncDate()
                createActionTimeLine()
                createPlanTimeLine()
                timelinemodelList.sortBy {
                    it.date
                }
                activity!!.runOnUiThread {
                    timeLineAdapter.setData(timelinemodelList)
                    timeLineAdapter.notifyDataSetChanged() }
            }).start()
    }


    private fun createActionTimeLine(){
        if(LitePal.count(ActionDB::class.java)>0){
            LitePal.findAll(ActionDB::class.java).forEach {
                var startDay = it.startTime
                val eventList = LitePal.where("startDay = ? and actionId = ?",""+Utils.getStartTime().time,""+it._id).find(ActionEventDB::class.java)
                if (eventList.size>0){
                    for (event in eventList){
                        if (!event.isDeleted){
                            timelinemodelList.add(TimeLineModel(it.name,event.time,if (event.isSuccess)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it.remoteId,event._id,0))
                        }
                    }
                }else{
                    if (todayStart.time-startDay.time>0){ //事件开始时间在今天之前*需计算今天是否有事件
                        var tempTime = (todayStart.time-startDay.time)%it.loopTime
                        var index = 0
                        while (todayEnd.time-(todayStart.time+tempTime)/it.loopTime>=1){
                            val temp =  ActionEventDB(LitePal.count(ActionEventDB::class.java).toLong(),todayStart,index,it._id,it.remoteId,false,Date(todayStart.time+tempTime))
                            temp.save()
                            timelinemodelList.add(TimeLineModel(it.name,Date(todayStart.time+tempTime),OrderStatus.INACTIVE,"",it._id,temp._id,0))
                            tempTime+=it.loopTime
                            index++
                        }
                    }else{ //事件开始时间是今天0点之后,需计算是否是今天开始
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
    }

    private fun createPlanTimeLine(){
        LitePal.findAll(PlanDB::class.java).forEach {
            var startTime = Date(it.startTime)
            if (todayStart.time-startTime.time>0){ //事件开始时间在今天之前*需计算今天是否有事件
                var tempTime = (todayStart.time-startTime.time)%it.loopTime
                var index = 0
                while ((todayEnd.time-(todayStart.time+tempTime))/it.loopTime>=1){
                    val planEventDB = PlanEventDB(todayStart.time+startTime.time+it._id,Date(todayStart.time+tempTime),it._id,false,true,Date(it.loopTime),it.name,it.isLoop)
                    planEventDB.save()
                    timelinemodelList.add(TimeLineModel(it.name,Date(todayStart.time+tempTime),if (it.isFinish)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it._id,planEventDB._id,1))
                    tempTime+=it.loopTime
                    index++
                }
            }else{ //事件开始时间是今天0点之后,需计算是否是今天开始
                var temp = todayEnd.time - startTime.time
                if (temp>0){
                    var index = 0
                    val planEventDB = PlanEventDB(startTime.time+it._id,Date(startTime.time),it._id,false,true,Date(it.loopTime),it.name,it.isLoop)
                    planEventDB.save()
                    timelinemodelList.add(TimeLineModel(it.name,Date(startTime.time),if (it.isFinish)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it._id,planEventDB._id,1))
                    index++
                    while (temp/it.loopTime>=1){
                        temp -= it.loopTime
                        timelinemodelList.add(TimeLineModel(it.name,Date(startTime.time+it.loopTime),if (it.isFinish)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it._id,planEventDB._id,1))
                        startTime = Date(startTime.time+it.loopTime)
                        index++
                    }
                }
                }
        }
    }
}