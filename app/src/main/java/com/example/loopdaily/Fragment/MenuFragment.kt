package com.example.loopdaily.Fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.example.loopdaily.R
import com.example.loopdaily.Util.NetUtil
import com.example.loopdaily.Util.ServerUserModel
import com.example.loopdaily.lib.CircleImageView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson

class MenuFragment : Fragment() {
    lateinit var unbinder : Unbinder
      lateinit var userAvator:CircleImageView
     var vNavigation:NavigationView? = null
     lateinit var username_menu:TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.menu_fragment,container,false)
        vNavigation = rootView?.findViewById(R.id.leftView) as NavigationView?
        userAvator = vNavigation?.getHeaderView(0)!!.findViewById(R.id.left_view_userimg)
        username_menu = vNavigation!!.getHeaderView(0).findViewById(R.id.main_menu_username)
        updateUserInfoToView()
        return rootView
    }

    fun updateUserInfoToView() = Thread(Runnable {
        val severUserModel = Gson().fromJson<ServerUserModel>(NetUtil.GetMessage(getString(R.string.server_host)+"/user/myInfo"), ServerUserModel::class.java)
        Handler(context?.mainLooper).post(Runnable {
            username_menu.text = severUserModel.userName
            userAvator.setImageURL(getString(R.string.server_host_file)+"/upload/avator/"+severUserModel.userName+"/"+severUserModel.avatorFileName)
        })
    }).start()


}