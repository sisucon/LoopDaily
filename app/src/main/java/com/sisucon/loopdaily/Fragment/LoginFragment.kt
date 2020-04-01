package com.sisucon.loopdaily.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.textfield.TextInputLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.sisucon.loopdaily.Activity.MainActivity
import com.sisucon.loopdaily.Model.Userkey
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.Util.NetUtil
import com.sisucon.loopdaily.Util.ReplyMessage
import com.sisucon.loopdaily.lib.MyEditText
import es.dmoral.toasty.Toasty
import org.litepal.LitePal

class LoginFragment : androidx.fragment.app.Fragment(){
    @BindView(R.id.login_phonenum_layout) lateinit var login_username_layout : TextInputLayout
    @BindView(R.id.login_phontNum) lateinit var login_username_text :MyEditText
    @BindView(R.id.login_password_layout) lateinit var login_password_layout : TextInputLayout
    @BindView(R.id.login_password) lateinit var login_password_text : MyEditText
    lateinit var unbinder : Unbinder
    lateinit var username:String
    lateinit var password:String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val user = LitePal.findFirst(Userkey::class.java)
        if (user!=null){
            username = user.username
            password = user.password
            loginPost()
        }
        val rootView : View = inflater.inflate(R.layout.login_fragment,container,false)
        unbinder = ButterKnife.bind(this,rootView)
            return  rootView
    }

    @OnClick(R.id.login_button)
    fun login(v:View){
        username = login_username_text.text.toString()
        password = login_password_text.text.toString()
        loginPost()
    }

    fun loginPost(){
        var reply : ReplyMessage? = null
        Thread(Runnable {
            reply =   NetUtil.PostUserMessage(getString(R.string.server_host)+"/login",username,password)
            Handler(context!!.mainLooper).post(Runnable {
                if (reply?.result!!){
                    LitePal.deleteAll(Userkey::class.java)
                    Userkey(username,password).save()
                    startActivity(Intent().setClass(activity,MainActivity::class.java))
                    Toasty.success(context!!,"登录成功").show()
                }else{
                    println(reply!!.message)
                    Toasty.error(context!!, reply!!.message).show()

                }
            })
        }).start()
    }


    override fun onDestroy() {
        super.onDestroy()
        unbinder.unbind()
    }
}