package com.example.loopdaily.Fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.loopdaily.R
import com.example.loopdaily.Util.NetUtil
import com.example.loopdaily.Util.UserModel
import com.example.loopdaily.lib.MyEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import es.dmoral.toasty.Toasty

class RegisterFragment : Fragment(){
    @BindView(R.id.register_phonenum_layout) lateinit var phoneLayout : TextInputLayout
    @BindView(R.id.register_password_layout) lateinit var passwordLayout : TextInputLayout
    @BindView(R.id.register_phontNum) lateinit var phoneText:MyEditText
    @BindView(R.id.register_password) lateinit var passwordText:MyEditText
    @BindView(R.id.register_password_re) lateinit var passwordTextRe:MyEditText
    lateinit var unBind:Unbinder
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView : View = inflater.inflate(R.layout.register_fragment,container,false)
        unBind = ButterKnife.bind(this,rootView)
        return rootView
    }

    @OnClick(R.id.register_button)
    fun register(v:View){
        if(passwordText.text.toString() == passwordTextRe.text.toString()){
            Thread(Runnable {
                val replyMessage = NetUtil.PostMessage(getString(R.string.server_host)+"/user/register",Gson().toJson(UserModel(phoneText.text.toString(),passwordText.text.toString())))
               Handler(context!!.mainLooper).post(Runnable {
                   if (replyMessage.result){
                       Toasty.success(context!!,replyMessage.message).show()
                   }else{
                       Toasty.error(context!!,replyMessage.message).show()
                   }
               })
            }).start()
        }else{
            passwordText.startShakeAnimation()
            passwordLayout.error = "两次输入不相同"
        }
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        unBind.unbind()
    }
}