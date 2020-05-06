package com.sisucon.loopdaily.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import com.sisucon.loopdaily.Adapter.MainViewPagerAdapter
import com.sisucon.loopdaily.Fragment.MenuFragment
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.lib.NoScrollViewPager
import com.google.android.material.tabs.TabLayout
import com.mxn.soul.flowingdrawer_core.ElasticDrawer
import com.mxn.soul.flowingdrawer_core.FlowingDrawer

class MainActivity : AppCompatActivity() {
    @Nullable private   var mMenuFragment:MenuFragment? = null
    @Nullable private var underTable:TabLayout? = null
    @Nullable private var viewPager:NoScrollViewPager? = null
    var tabTitle  = arrayOf("今日","全部","我的")
    var allTab = arrayOfNulls<TabLayout.Tab>(3)
    private lateinit var mDrawer: FlowingDrawer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       initView()
    }

    fun initView(){
        setupMenu()
        mDrawer = findViewById(R.id.drawerlayout)
        viewPager = findViewById(R.id.main_viewpager)
        viewPager?.adapter = MainViewPagerAdapter(supportFragmentManager)
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL)
        underTable = findViewById(R.id.navigation_tablayout)
        underTable!!.setupWithViewPager(viewPager)
        for (i in tabTitle.indices){
            allTab[i] = underTable!!.getTabAt(i)
            if(allTab[i]!=null){
                allTab[i]?.text = tabTitle[i]
            }
        }
    }

    fun setupMenu(){
        val fm:FragmentManager = supportFragmentManager
        mMenuFragment = fm.findFragmentById(R.id.id_container_menu) as MenuFragment?
        if(mMenuFragment==null){
            mMenuFragment = MenuFragment()
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment!!).commit()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
