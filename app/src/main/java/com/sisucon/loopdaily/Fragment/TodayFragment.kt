package com.sisucon.loopdaily.Fragment

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jude.easyrecyclerview.EasyRecyclerView
import com.sisucon.loopdaily.Activity.AddPlanActivity
import com.sisucon.loopdaily.Adapter.TimeLineAdapter
import com.sisucon.loopdaily.Model.ActionDB
import com.sisucon.loopdaily.Model.ActionEventDB
import com.sisucon.loopdaily.Model.PlanDB
import com.sisucon.loopdaily.Model.PlanEventDB
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.Util.ActionClassModel
import com.sisucon.loopdaily.Util.NetUtil
import com.sisucon.loopdaily.Util.TimeLineModel
import com.sisucon.loopdaily.Util.Utils
import com.sisucon.loopdaily.lib.AlarmManagerUtils
import com.sisucon.loopdaily.lib.OrderStatus
import es.dmoral.toasty.Toasty
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
        checkPushSwitchStatus()
        notificationTest()
        return rootView
    }

    fun notificationTest(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channelId = "notification"
            val channalName = "活动提醒"
            val importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(channelId,channalName,importance)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(channelId:String, channelName:String, importance:Int){
        val channel = NotificationChannel(channelId,channelName,importance)
        val notifitacionManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifitacionManager.createNotificationChannel(channel)
    }

    fun sendNotificationMsg(channelTitle:String, channelText:String,time:Long,index:Int) {
        val temp = Utils.createTimeArray(time)
        AlarmManagerUtils.setAlarm(activity,0,temp[0],temp[1],temp[2],temp[0]*60*60+temp[1]*60+temp[2],0,channelText,2)
    }



    private fun checkPushSwitchStatus() {
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(activity!!);
        val isOpend = notificationManager.areNotificationsEnabled()
        if (!isOpend){
            val intent: Intent = Intent()
            try {
                //8.0及以后版本使用这两个extra.  >=API 26
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity!!.packageName)
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, activity!!.applicationInfo.uid)

                }
                else{
                    //5.0-7.1 使用这两个extra.  <= API 25, >=API 21
                    intent.putExtra("app_package", activity!!.packageName)
                    intent.putExtra("app_uid", activity!!.applicationInfo.uid)
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                //其他低版本或者异常情况，走该节点。进入APP设置界面
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.putExtra("package",activity!!.packageName)
                //val uri = Uri.fromParts("package", packageName, null)
                //intent.data = uri
                startActivity(intent)
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        setData()
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("WrongConstant")
    fun initView(){
        addButton = rootView.findViewById(R.id.today_add_button)
        addButton.setOnClickListener {
            startActivityForResult(Intent(activity,AddPlanActivity::class.java),0)
        }
        recyclerView = rootView.findViewById(R.id.today_recyclerView)
        recyclerView.setLayoutManager( LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false))
        timelinemodelList = ArrayList()
        timeLineAdapter = TimeLineAdapter(timelinemodelList,false,null,this)
        recyclerView.adapter = timeLineAdapter
        setData()
        //设置刷新监听器
        recyclerView.setRefreshListener {
            timelinemodelList.clear()
            timeLineAdapter.notifyDataSetChanged()
            setData()
            notificationTest()
            recyclerView.setRefreshing(false)
        }
    }
    //更新数据
    fun syncDate(){
            NetUtil.getPlanDB(getString(R.string.server_host)+"/plan/getPlan")
    }
    var notificationSum = 0
    private val todayStart = Utils.getStartTime()
    private val todayEnd = Utils.getEndTime()
     fun setData(){
        timelinemodelList.clear()
        timeLineAdapter.notifyDataSetChanged()
        notificationSum = 0
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
                val eventList = LitePal.where("startDay = ? and actionId = ?",""+Utils.getStartTime().time,""+it.id).find(ActionEventDB::class.java)
                if (eventList.size>0){
                    for (event in eventList){
                        if (!event.isDeleted){
                            timelinemodelList.add(TimeLineModel(it.name,event.time,if (event.isSuccess)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it.remoteId,event.id,0))
                            sendNotificationMsg("提醒","该"+it.name+"了",event.time.time,timelinemodelList.size)
                        }
                    }
                }else{
                    if (todayStart.time-startDay.time>0){ //事件开始时间在今天之前*需计算今天是否有事件
                        var tempTime = (todayStart.time-startDay.time)%it.loopTime
                        var index = 0
                        while (todayEnd.time-(todayStart.time+tempTime)/it.loopTime>=1){
                            val temp =  ActionEventDB(LitePal.count(ActionEventDB::class.java).toLong(),todayStart,index,it.id,it.remoteId,false,Date(todayStart.time+tempTime))
                            temp.save()
                            timelinemodelList.add(TimeLineModel(it.name,Date(todayStart.time+tempTime),OrderStatus.INACTIVE,"",it.id,temp.id,0))
                            sendNotificationMsg("提醒","该"+it.name+"了",Date(todayStart.time+tempTime).time,notificationSum++)

                            tempTime+=it.loopTime
                            index++
                        }
                    }else{ //事件开始时间是今天0点之后,需计算是否是今天开始
                        var temp = todayEnd.time - startDay.time
                        var index = 0
                        val tempd = ActionEventDB(LitePal.count(ActionEventDB::class.java).toLong(),todayStart,index,it.id,it.remoteId,false,Date(startDay.time))
                        tempd.save()
                        timelinemodelList.add(TimeLineModel(it.name,Date(startDay.time+it.loopTime),OrderStatus.INACTIVE,"",it.id,tempd.id,0))
                        sendNotificationMsg("提醒","该"+it.name+"了",Date(startDay.time+it.loopTime).time,notificationSum++)
                        index++
                        while (temp/it.loopTime>=1){
                            temp -= it.loopTime
                            val tempd = ActionEventDB(LitePal.count(ActionEventDB::class.java).toLong(),todayStart,index,it.id,it.remoteId,false,Date(startDay.time+it.loopTime))
                            tempd.save()
                            timelinemodelList.add(TimeLineModel(it.name,Date(startDay.time+it.loopTime),OrderStatus.INACTIVE,"",it.id,tempd.id,0))
                            sendNotificationMsg("提醒","该"+it.name+"了",Date(startDay.time+it.loopTime).time,notificationSum++)
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
                    var planEventDB = LitePal.where("planId = ? and startDay = ?",""+it.id,""+Date(todayStart.time+tempTime)).findFirst(PlanEventDB::class.java)
                    if (planEventDB==null){
                         planEventDB = PlanEventDB(todayStart.time+startTime.time+it.id,Date(todayStart.time+tempTime),it.id,false,true,Date(it.loopTime),it.name,it.isLoop)
                    }
                    println("planEventDB = ${planEventDB._id}")
                    planEventDB.save()
                    timelinemodelList.add(TimeLineModel(it.name,Date(todayStart.time+tempTime),if (planEventDB.isSuccess)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it.id,planEventDB._id,1))
                    if (it.isRemind)
                    {
                        sendNotificationMsg("提醒","该"+it.name+"了",Date(todayStart.time+tempTime).time,notificationSum++)
                    }
                    tempTime+=it.loopTime
                    index++
                }
            }else{ //事件开始时间是今天0点之后,需计算是否是今天开始
                var temp = todayEnd.time - startTime.time
                if (temp>0){
                    var index = 0
                    var planEventDB = LitePal.where("planId = ? and startDay = ?",""+it.id,""+Date(startTime.time).time).findFirst(PlanEventDB::class.java)
                    if (planEventDB==null){
                        planEventDB = PlanEventDB(startTime.time+it.id,Date(startTime.time),it.id,false,true,Date(it.loopTime),it.name,it.isLoop)
                    }
                    planEventDB.save()
                    println("planEventDB = ${planEventDB._id}")
                    timelinemodelList.add(TimeLineModel(it.name,Date(startTime.time),if (planEventDB.isSuccess)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it.id,planEventDB._id,1))
                    if (it.isRemind)
                    {
                        sendNotificationMsg("提醒","该"+it.name+"了",Date(startTime.time).time,notificationSum++)
                    }
                    index++
                    while (temp/it.loopTime>=1){
                        temp -= it.loopTime
                        timelinemodelList.add(TimeLineModel(it.name,Date(startTime.time+it.loopTime),if (planEventDB.isSuccess)OrderStatus.ACTIVE else OrderStatus.INACTIVE,"",it.id,planEventDB._id,1))
                        if (it.isRemind)
                        {
                            sendNotificationMsg("提醒","该"+it.name+"了",Date(startTime.time+it.loopTime).time,notificationSum++)
                        }
                        startTime = Date(startTime.time+it.loopTime)
                        index++
                    }
                }
                }
        }
    }
}