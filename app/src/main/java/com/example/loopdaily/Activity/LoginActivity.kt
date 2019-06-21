package com.example.loopdaily.Activity

import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.loopdaily.Adapter.LoginAndFragmentAdapter
import com.example.loopdaily.R
import com.example.loopdaily.Util.Utils
import com.example.loopdaily.lib.ViewPagerUtil

class LoginActivity : AppCompatActivity(){
    @BindView(R.id.login_register_viewpager) lateinit var viewPager : ViewPagerUtil
   @BindView(R.id.login_text) lateinit var loginText : TextView
    @BindView(R.id.register_text)
    lateinit var registerText : TextView
    var flag = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_register_layout)
        ButterKnife.bind(this)
        initView()

    }

    @OnClick(R.id.login_text,R.id.register_text)
    fun switchFragment(v: View){
        when(v.id){
            R.id.login_text ->{
                if (flag==1){
                    clickText(registerText,false)
                    clickText(loginText,true)
                    viewPager.currentItem = 0
                    flag = 0
                }
            }
            R.id.register_text ->{
                if(flag==0){
                    clickText(loginText,false)
                    clickText(registerText,true)
                    viewPager.currentItem = 1
                    flag=1
                }
            }
        }
    }

    fun initView(){
        viewPager.adapter = LoginAndFragmentAdapter(supportFragmentManager)
        clickText(loginText,true)
    }

    fun clickText(textView: TextView,isClick:Boolean){
        textView.paint.flags = Paint.ANTI_ALIAS_FLAG;
        textView.paint.isAntiAlias = false
        val resources = baseContext.resources
        val colorStateList : ColorStateList
        colorStateList = if(isClick){
            resources.getColorStateList(R.color.textCilck)
        }else{
            resources.getColorStateList(R.color.nromalText)
        }
        textView.setTextColor(colorStateList)
    }

}