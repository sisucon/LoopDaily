package com.sisucon.loopdaily.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.Unbinder
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.Util.NetUtil
import com.sisucon.loopdaily.Util.ServerUserModel
import com.sisucon.loopdaily.lib.CircleImageView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sisucon.loopdaily.Activity.LoginActivity
import com.sisucon.loopdaily.Model.ActionModel
import com.sisucon.loopdaily.Model.PlanDB
import com.sisucon.loopdaily.Model.Userkey
import org.litepal.LitePal
import org.litepal.extension.delete
import org.litepal.extension.deleteAll

class MenuFragment : Fragment() {
    lateinit var unbinder : Unbinder
      lateinit var userAvator:CircleImageView
     var vNavigation:NavigationView? = null
     lateinit var username_menu:TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.menu_fragment,container,false)
        vNavigation = rootView?.findViewById(R.id.leftView) as NavigationView?
        initView()
        return rootView
    }

    fun initView(){
        userAvator = vNavigation?.getHeaderView(0)!!.findViewById(R.id.left_view_userimg)
        username_menu = vNavigation!!.getHeaderView(0).findViewById(R.id.main_menu_username)
        vNavigation!!.setNavigationItemSelectedListener {
            logout()
            true
        }
        updateUserInfoToView()
    }

    private fun logout(){
        LitePal.deleteAll(Userkey::class.java)
        activity!!.finish()
        startActivity(Intent(activity,LoginActivity::class.java))
    }


    private fun updateUserInfoToView() = Thread(Runnable {
        val severUserModel = Gson().fromJson<ServerUserModel>(NetUtil.GetMessage(getString(R.string.server_host)+"/user/myInfo"), ServerUserModel::class.java)
        Handler(context?.mainLooper).post(Runnable {
            username_menu.text = severUserModel.userName
            userAvator.setImageURL(getString(R.string.server_host_file)+"/upload/avator/"+severUserModel.userName+"/"+severUserModel.avatorFileName)
        })
    }).start()


}


