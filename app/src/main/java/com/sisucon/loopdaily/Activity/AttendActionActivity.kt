package com.sisucon.loopdaily.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.sisucon.loopdaily.Model.ActionModel
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.Util.NetUtil
import com.sisucon.loopdaily.Util.Utils
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import java.util.*

class AttendActionActivity : AppCompatActivity(){
    @BindView(R.id.attend_action_img) lateinit var img :ImageView
    @BindView(R.id.attend_action_attendnumber) lateinit var attendNumber:TextView
    @BindView(R.id.attend_action_creattime) lateinit var createTime :TextView
    @BindView(R.id.attend_action_loopTime) lateinit var loopTime:TextView
    @BindView(R.id.attend_action_successtimes) lateinit var successTimes:TextView
    @BindView(R.id.attend_attendbutton) lateinit var attendButton:TextView
    @BindView(R.id.attend_action_name) lateinit var name:TextView
     lateinit var id :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.attendaction_activity_layout)
        ButterKnife.bind(this)
        id =  intent.getStringExtra("id")
        initView()
    }

    @SuppressLint("SetTextI18n")
    fun initView(){
        attendButton.setOnClickListener {
            Thread(Runnable {
                val replyMessage = NetUtil.ConvertToReplyMessage(NetUtil.GetMessage(getString(R.string.server_host)+"/action/attendAction/"+id)!!)
                    print(replyMessage)
                runOnUiThread(Runnable {
                    if (replyMessage.result){
                        startActivity(Intent(this,AttendActionSelectActivity::class.java).putExtra("id",id))
                        Toasty.success(this,replyMessage.message,Toast.LENGTH_SHORT).show()
                    }else{
                        Toasty.error(this,replyMessage.message,Toast.LENGTH_SHORT).show()
                    }
                })
            }).start()
        }
        Thread(Runnable {
            var action = Gson().fromJson<ActionModel>(NetUtil.GetMessage(getString(R.string.server_host)+"/action/getAction/"+id),ActionModel::class.java)
            Handler(this.mainLooper).post(Runnable {
                Utils.getInstance(this).GetImg(getString(R.string.server_host_file)+"/upload/actionDefault/"+id+"/"+action.imageName,img,this)
                attendNumber.text = ""+action.attendNumber
                createTime.text = getString(R.string.createwhen)+Utils.DateToLessionType(Date(action.uploadTime))
                loopTime.text = Utils.loopTimeToString(action.loopTime)
                successTimes.text = ""+action.successTimes
                name.text = action.name
            })
        }).start()
    }
}