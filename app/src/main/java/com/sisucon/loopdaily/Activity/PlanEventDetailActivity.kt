package com.sisucon.loopdaily.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

    fun initView() {
        setContentView(R.layout.plan_event_detail_layout)
        ButterKnife.bind(this)
        val planEventId = intent.getStringExtra("id").toLong()
        println("planEventId = ${planEventId}")
        eventModel = LitePal.find(PlanEventDB::class.java, planEventId)
        localPlan = LitePal.find(PlanDB::class.java, eventModel.planId)
        nameText.text = localPlan.name
        finishBtn.setText(if(eventModel.isSuccess){"取消"}else{"完成"})
        finishBtn.setOnClickListener {
            eventModel.isSuccess = !eventModel.isSuccess
            eventModel.save()
            if (eventModel.isSuccess) {
                finishBtn.setText("取消")
            } else {
                finishBtn.setText("完成")
            }
        }
        deleteBtn.setOnClickListener {
            Thread(Runnable {
                NetUtil.GetMessage(getString(R.string.server_host) + "/plan/deletePlan/" + localPlan.remoteId)
                runOnUiThread(Runnable {
                    LitePal.deleteAll(PlanEventDB::class.java, "planId = ?", "" + localPlan.id)
                    localPlan.delete()
                    Toasty.success(this, "删除日程成功").show()
                    this.setResult(Activity.RESULT_OK)
                    this.finish()
                })
            }).start()
        }

        changeBtn.setOnClickListener {
            val intent = Intent().setClass(this, AddPlanActivity::class.java)
            intent.putExtra("planId", localPlan.id)
            startActivity(intent)
            this.finish()
        }


    }
}
