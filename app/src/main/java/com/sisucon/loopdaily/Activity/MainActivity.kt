package com.sisucon.loopdaily.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.mxn.soul.flowingdrawer_core.ElasticDrawer
import com.mxn.soul.flowingdrawer_core.FlowingDrawer
import com.sisucon.loopdaily.Adapter.MainViewPagerAdapter
import com.sisucon.loopdaily.Fragment.MenuFragment
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.lib.NoScrollViewPager
import es.dmoral.toasty.Toasty


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
//       requestPermission()
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

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意权限申请
//                Toasty.success(this,"同意权限")
//            } else { //拒绝权限申请
//                Toasty.error(this,"权限被拒绝,这可能导致闪退")
//            }
//            else -> {
//            }
//        }
//    }
//
//    val REQUEST_EXTERNAL_STORAGE = 1
//    var PERMISSIONS_STORAGE = arrayOf("android.permission.READ_EXTERNAL_STORAGE",
//        "android.permission.WRITE_EXTERNAL_STORAGE","android.permission.INTERNET","android.permission.VIBRATE" ,"android.permission.CAMERA")
//
//
//
//    fun requestPermission(){
//        val REQUEST_CODE_PERMISSION_STORAGE = 100
//        val permissions = arrayOf<String>(
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ,Manifest.permission.INTERNET,
//            Manifest.permission.CAMERA,
//            Manifest.permission.VIBRATE
//        )
//        for (str in permissions) {
//            if (checkSelfPermission(str!!) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(permissions, REQUEST_CODE_PERMISSION_STORAGE)
//                return
//            }
//        }
//    }

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
