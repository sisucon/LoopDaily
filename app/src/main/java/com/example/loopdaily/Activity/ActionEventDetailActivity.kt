package com.example.loopdaily.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.example.loopdaily.Model.ActionDB
import com.example.loopdaily.Model.ActionEventDB
import com.example.loopdaily.Model.ActionModel
import com.example.loopdaily.R
import com.example.loopdaily.Util.NetUtil
import com.example.loopdaily.Util.Utils
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import org.litepal.LitePal
import org.litepal.extension.find
import org.w3c.dom.Text
import java.util.*

class ActionEventDetailActivity : AppCompatActivity(){
    @BindView(R.id.detail_action_img) lateinit var img:ImageView
    @BindView(R.id.detail_action_name) lateinit var name:TextView
    @BindView(R.id.event_finish) lateinit var finishBtn:TextView
    @BindView(R.id.event_change) lateinit var changeBtn:TextView
    @BindView(R.id.event_delete) lateinit var deleteBtn:TextView
    var eventId : Long? = null
    lateinit var eventModel : ActionEventDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_event_detail_layout)
        ButterKnife.bind(this)
        var ra = intent.getStringExtra("id")
        eventId = ra.toLong()
        eventModel =  LitePal.find(ActionEventDB::class.java, eventId!!)
        initView()
    }

    fun initView(){
        Thread(Runnable {
            var action = Gson().fromJson<ActionModel>(
                NetUtil.GetMessage(getString(R.string.server_host)+"/action/getAction/"+eventModel.remoteId),
                ActionModel::class.java)
            Handler(this.mainLooper).post(Runnable {
                Utils.getInstance(this).GetImg(getString(R.string.server_host_file)+"/upload/actionDefault/"+action.id+"/"+action.imageName,img)
                name.text = action.name
            })
        }).start()
        finishBtn.setOnClickListener {
            eventModel.isSuccess = true
            eventModel.save()
            Toasty.success(this,"完成").show()
        }
        deleteBtn.setOnClickListener {
            LitePal.delete(ActionDB::class.java,eventModel.actionId)
            LitePal.deleteAll(ActionEventDB::class.java,"startDay = ? and actionId = ?",""+eventModel.startDay.time,""+eventModel.actionId)
            Toasty.success(this,"删除成功").show()

        }
        changeBtn.setOnClickListener {
            startActivity(            Intent(this,ChangeSelectActivity::class.java).putExtra("id",""+eventModel._id))
        }
    }
}