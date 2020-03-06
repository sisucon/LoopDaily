package com.sisucon.loopdaily.Activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.CustomListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.sisucon.loopdaily.Model.ActionModel
import com.sisucon.loopdaily.Model.PlanDB
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.Util.NetUtil
import com.sisucon.loopdaily.Util.PlanClassRemote
import com.sisucon.loopdaily.Util.PlanJson
import com.sisucon.loopdaily.Util.Utils
import com.google.gson.Gson
import com.suke.widget.SwitchButton
import es.dmoral.toasty.Toasty
import org.litepal.LitePal
import java.text.SimpleDateFormat
import java.util.*

class AddPlanActivity : AppCompatActivity(){
    @BindView(R.id.plan_name) lateinit var nameEdit :EditText
    @BindView(R.id.plan_loopText) lateinit var loopText : TextView
    @BindView(R.id.plan_loop_bord) lateinit var loopBord : LinearLayout
    @BindView(R.id.plan_select_day) lateinit var dayText: EditText
    @BindView(R.id.plan_select_hour) lateinit var hourText: EditText
    @BindView(R.id.plan_select_min) lateinit var minText: EditText
    @BindView(R.id.plan_select_starttime_picker) lateinit var timepicker: TextView
    @BindView(R.id.plan_select_okbutton)lateinit var button: TextView
     lateinit var remindButton : SwitchButton
    lateinit var  id : String
    lateinit var switch : SwitchButton

    var selectDate : Date? = null
    lateinit var action: ActionModel
    lateinit var pickTime : TimePickerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_plan_layout)
        ButterKnife.bind(this)
        initTimePicker()
        initView()
    }

    fun initView() {
        switch = findViewById(R.id.plan_switch)
        remindButton = findViewById(R.id.plan_remind_switch)
        timepicker.setOnClickListener {
            pickTime.show()
        }
        switch.setOnCheckedChangeListener { view, isChecked -> if (isChecked){
            loopText.visibility = View.VISIBLE
            loopBord.visibility = View.VISIBLE
        }else{
            loopText.visibility = View.GONE
            loopBord.visibility = View.GONE
        }
        }
        button.setOnClickListener {
             val temp : PlanJson
            if(switch.isChecked){
                if (Utils.checkEditNotNUll(dayText)&&Utils.checkEditNotNUll(hourText)&&Utils.checkEditNotNUll(minText)){
                     postJson( PlanJson(nameEdit.text.toString(),switch.isChecked,
                         Date((dayText.text.toString().toLong()*1000*60*60*24)+(hourText.text.toString().toLong()*1000*60*60)+(minText.text.toString().toLong()*1000*60)).time,
                         selectDate!!.time,remindButton.isChecked,false))
                }else{
                    Toasty.error(this,"请填写循环的所有时间").show()
                }
            }else{
                postJson(PlanJson(nameEdit.text.toString(),switch.isChecked, 0,
                    selectDate!!.time,remindButton.isChecked,false))
            }

        }
    }

    fun postJson(temp : PlanJson){
        Thread(Runnable {
            val reply =  NetUtil.PostMessage(getString(R.string.server_host)+"/plan/createPlan",Gson().toJson(temp))
            runOnUiThread {
                if (reply.result){
                    Toasty.success(this,"创建日程成功").show()
                    println(reply.message)
                    val remoteJson = Gson().fromJson(reply.message,PlanClassRemote::class.java)
                    val db = PlanDB(LitePal.count(PlanDB::class.java).toLong(),remoteJson.id,remoteJson.name,remoteJson.userId,remoteJson.isLoop,remoteJson.loopTime,remoteJson.info,remoteJson.startTime,remoteJson.isRemind,remoteJson.isFinish)
                    db.save()
                }else{
                    Toasty.error(this,"创建日程失败").show()
                }
            }
        }).start()
    }

        fun initTimePicker(){
            pickTime = TimePickerBuilder(this, OnTimeSelectListener { date, v -> timepicker.text=getTime(date)
                selectDate = date
            })
                .setLayoutRes(R.layout.pickerview_custom_time, CustomListener {
                    val tvSubmit = it.findViewById(R.id.tv_finish) as TextView
                    val ivCancel = it.findViewById(R.id.iv_cancel) as ImageView
                    tvSubmit.setOnClickListener {
                        pickTime.returnData()
                        pickTime.dismiss()
                    }
                    ivCancel.setOnClickListener { pickTime.dismiss() }
                })
                .setContentTextSize(18)
                .setType( booleanArrayOf(false, false, true, true, true, false))
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 40, 0, -40)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D.toInt())
                .build()
        }


    private fun getTime(date: Date): String {//可根据需要自行截取数据显示
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }
}