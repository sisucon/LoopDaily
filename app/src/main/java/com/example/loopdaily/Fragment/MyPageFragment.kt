package com.example.loopdaily.Fragment

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.loopdaily.Activity.ShowMyAttendActivity
import com.example.loopdaily.R
import com.example.loopdaily.Util.NetUtil
import com.example.loopdaily.Util.ServerUserModel
import com.example.loopdaily.Util.Utils
import com.google.gson.Gson

class MyPageFragment : Fragment(){
    lateinit var rootView:View
    lateinit var imageView: ImageView
    lateinit var actionButton:ImageView
    lateinit var textView: TextView
    lateinit var username:TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.mypage_layout,container,false)
        initView()
        return rootView
    }

    fun initView(){
        imageView = rootView.findViewById(R.id.mypage_userimg)
        username = rootView.findViewById(R.id.mypage_username)
        actionButton = rootView.findViewById(R.id.mypage_item_img)
        textView = rootView.findViewById(R.id.mypage_listview_item_text)
        updateUserInfoToView()
        actionButton.setOnClickListener {
            startActivity(Intent(context,ShowMyAttendActivity::class.java))
        }
        textView.setOnClickListener {
            startActivity(Intent(context,ShowMyAttendActivity::class.java))

        }
    }

    fun updateUserInfoToView() = Thread(Runnable {
        val severUserModel = Gson().fromJson<ServerUserModel>(NetUtil.GetMessage(getString(R.string.server_host)+"/user/myInfo"), ServerUserModel::class.java)
        Handler(context?.mainLooper).post(Runnable {
            username.text = severUserModel.userName
            Utils.getInstance(context).GetImg(getString(R.string.server_host_file)+"/upload/avator/"+severUserModel.userName+"/"+severUserModel.avatorFileName,imageView)
        })
    }).start()
}