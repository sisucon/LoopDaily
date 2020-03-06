package com.sisucon.loopdaily.Activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.CustomListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.sisucon.loopdaily.Model.ActionDB
import com.sisucon.loopdaily.Model.ActionModel
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.Util.NetUtil
import com.sisucon.loopdaily.Util.Utils
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import org.litepal.LitePal
import java.text.SimpleDateFormat
import java.util.*

class AttendActionSelectActivity : AppCompatActivity(){
    @BindView(R.id.select_action_img) lateinit var img : ImageView
    @BindView(R.id.select_day) lateinit var dayText:EditText
    @BindView(R.id.select_hour) lateinit var hourText:EditText
    @BindView(R.id.select_min) lateinit var minText:EditText
    @BindView(R.id.select_name) lateinit var name:TextView
    @BindView(R.id.select_starttime_picker) lateinit var timepicker:TextView
    @BindView(R.id.select_okbutton)lateinit var button:TextView
    lateinit var  id : String
    var selectDate : Date? = null
    lateinit var action:ActionModel
    lateinit var pickTime : TimePickerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_select_layout)
        ButterKnife.bind(this)
        initTimePicker()
        initView()
        id = intent.getStringExtra("id")
    }

    fun initView(){
        Thread(Runnable {
            action = Gson().fromJson<ActionModel>(
                NetUtil.GetMessage(getString(R.string.server_host)+"/action/getAction/"+id),
                ActionModel::class.java)
            Handler(this.mainLooper).post(Runnable {
                Utils.getInstance(this).GetImg(getString(R.string.server_host_file)+"/upload/actionDefault/"+id+"/"+action.imageName,img)
                name.text = action.name
            })
        }).start()
        timepicker.setOnClickListener {
            pickTime.show()
        }

        button.setOnClickListener {
            if (selectDate==null){
                Toasty.error(this,"请选择开始时间").show()
            }else if(dayText.text.equals("0")&&hourText.text.equals("0")&&minText.text.equals("0")){
                Toasty.error(this,"请输入周期时间").show()
            }else{
                val loopTime = Date((dayText.text.toString().toLong()*1000*60*60*24)+(hourText.text.toString().toLong()*1000*60*60)+(minText.text.toString().toLong()*1000*60))
                val actionDB = ActionDB(LitePal.count(ActionDB::class.java).toLong(),action.name, selectDate!!,loopTime.time,action.type,true,getString(R.string.server_host_file)+"/upload/actionDefault/"+action.id+"/"+action.imageName
                    ,action.id)
                if (actionDB.save()){
                    Toasty.success(this,"成功").show()
                }else{
                    Toasty.error(this,"失败").show()
                }

            }
        }
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
        Log.d("getTime()", "choice date millis: " + date.time)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }
}