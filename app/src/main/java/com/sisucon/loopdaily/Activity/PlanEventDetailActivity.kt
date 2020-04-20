package com.sisucon.loopdaily.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.sisucon.loopdaily.Model.PlanDB
import com.sisucon.loopdaily.Model.PlanEventDB
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.Util.NetUtil
import es.dmoral.toasty.Toasty
import org.litepal.LitePal
import org.litepal.extension.find

class PlanEventDetailActivity : AppCompatActivity(){
    @BindView(R.id.event_finish) lateinit var finishBtn: TextView
    @BindView(R.id.event_change) lateinit var changeBtn: TextView
    @BindView(R.id.event_delete) lateinit var deleteBtn: TextView
    @BindView(R.id.detail_plan_name) lateinit var nameText:TextView
    lateinit var eventModel : PlanEventDB
    lateinit var localPlan : PlanDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    fun initView(){
        setContentView(R.layout.plan_event_detail_layout)
        ButterKnife.bind(this)
        val planEventId = intent.getStringExtra("id").toLong()
        eventModel  = LitePal.find(PlanEventDB::class.java,planEventId)
        localPlan = LitePal.find(PlanDB::class.java,eventModel.planId)
        nameText.text = localPlan.name
        finishBtn.setOnClickListener {
            if(eventModel.isSuccess){
                finishBtn.setText("取消")
            }else{
                finishBtn.setText("完成")
            }
            eventModel.isSuccess = !eventModel.isSuccess
            eventModel.save()
        }
        deleteBtn.setOnClickListener {
            Thread(Runnable {
                NetUtil.GetMessage(getString(R.string.server_host)+"/plan/deletePlan/"+localPlan.remoteId)
                runOnUiThread(Runnable {
                    LitePal.deleteAll(PlanEventDB::class.java,"planId = ?",""+localPlan._id)
                    localPlan.delete()
                    Toasty.success(this,"删除日程成功").show()
                })
            }).start()
        }

        changeBtn.setOnClickListener {
            val intent = Intent().setClass(this,AddPlanActivity::class.java)
            intent.putExtra("planId",localPlan._id)
            startActivity(intent)
            this.finish()
        }



    }
}
