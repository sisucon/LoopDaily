package com.example.loopdaily.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import butterknife.BindView
import butterknife.ButterKnife
import com.example.loopdaily.Adapter.ActionItemAdapter
import com.example.loopdaily.Model.ActionModel
import com.example.loopdaily.R
import com.example.loopdaily.Util.NetUtil
import com.example.loopdaily.Util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jude.easyrecyclerview.EasyRecyclerView
import com.jude.easyrecyclerview.decoration.SpaceDecoration

class ShowMyAttendActivity : AppCompatActivity(){
    @BindView(R.id.showmyaction_recyclerview) lateinit var recyclerView: EasyRecyclerView
    lateinit var adapter: ActionItemAdapter
    lateinit var actionModelList: List<ActionModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.showmyattend_action_layout)
        ButterKnife.bind(this)
        initView()
    }

    fun initView(){
        val itemDecoration = SpaceDecoration(Utils.convertDpToPixel(8F, this).toInt())//参数是距离宽度
        itemDecoration.setPaddingEdgeSide(true)//是否为左右2边添加padding.默认true.
        itemDecoration.setPaddingStart(true)//是否在给第一行的item添加上padding(不包含header).默认true.
        itemDecoration.setPaddingHeaderFooter(false)//是否对Header于Footer有效,默认false.
        recyclerView.addItemDecoration(itemDecoration)
        recyclerView.setLayoutManager(StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL))
        adapter = ActionItemAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.setRefreshListener {
            setData()
        }
        adapter.setOnItemClickListener {
            startActivity(Intent(this,AttendActionSelectActivity::class.java).putExtra("id",""+adapter.getItem(it).id))
        }
        setData()
    }

        fun setData(){
            adapter.clear()
            Thread(Runnable {
                actionModelList = Gson().fromJson(NetUtil.GetMessage(getString(R.string.server_host)+"/action/getMyAttendAction"),object:
                    TypeToken<List<ActionModel>>(){}.type)
                Handler(this.mainLooper).post(Runnable {
                    adapter.addAll(actionModelList)
                })
            }).start()
        }

}