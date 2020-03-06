package com.sisucon.loopdaily.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sisucon.loopdaily.Fragment.AllActionFragment
import com.sisucon.loopdaily.Fragment.MyPageFragment
import com.sisucon.loopdaily.Fragment.TodayFragment

class MainViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
       when(position){
           0 -> return TodayFragment()
           1 -> return AllActionFragment()
           2 -> return MyPageFragment()
        else -> return Fragment()
       }
    }

    override fun getCount(): Int {
        return 3
    }

}
