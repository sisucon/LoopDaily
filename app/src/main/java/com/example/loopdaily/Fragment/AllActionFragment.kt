package com.example.loopdaily.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.loopdaily.Activity.AttendActionActivity
import com.example.loopdaily.Adapter.ActionItemAdapter
import com.example.loopdaily.Model.ActionModel
import com.example.loopdaily.R
import com.example.loopdaily.Util.NetUtil
import com.example.loopdaily.Util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jude.easyrecyclerview.EasyRecyclerView
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter
import com.jude.easyrecyclerview.decoration.SpaceDecoration



class AllActionFragment : Fragment(){
    lateinit var rootView : View
    lateinit var easyRecyclerView: EasyRecyclerView
    lateinit var adapter:ActionItemAdapter
    lateinit var actionModelList: List<ActionModel>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.allaction_fragment_layout,container,false)
        initView()

        return rootView
    }
    fun initView(){
        val itemDecoration = SpaceDecoration(Utils.convertDpToPixel(8F, context).toInt())//参数是距离宽度
        itemDecoration.setPaddingEdgeSide(true)//是否为左右2边添加padding.默认true.
        itemDecoration.setPaddingStart(true)//是否在给第一行的item添加上padding(不包含header).默认true.
        itemDecoration.setPaddingHeaderFooter(false)//是否对Header于Footer有效,默认false.
        easyRecyclerView = rootView.findViewById(R.id.allaction_recyclerview)
        easyRecyclerView.addItemDecoration(itemDecoration)
        easyRecyclerView.setLayoutManager(StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL))
        adapter = ActionItemAdapter(context!!)
        easyRecyclerView.adapter = adapter
        easyRecyclerView.setRefreshListener {
            setData()
        }
        adapter.setOnItemClickListener {
            val intent = Intent(context,AttendActionActivity::class.java)
            intent.putExtra("id",""+adapter.getItem(it).id)
            println(adapter.getItem(it).id)
            startActivity(intent)
        }
        setData()
    }

    fun setData(){
        adapter.clear()
        Thread(Runnable {
            actionModelList = Gson().fromJson(NetUtil.GetMessage(getString(R.string.server_host)+"/action/getAction"),object:TypeToken<List<ActionModel>>(){}.type)
            Handler(context?.mainLooper).post(Runnable {
                adapter.addAll(actionModelList)
            })
        }).start()
    }
}