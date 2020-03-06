package com.sisucon.loopdaily.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sisucon.loopdaily.Fragment.LoginFragment
import com.sisucon.loopdaily.Fragment.RegisterFragment

class LoginAndFragmentAdapter(fm: FragmentManager?) : androidx.fragment.app.FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }


    override fun getItem(p0: Int): Fragment {
        return if(p0==0){
            LoginFragment()
        }else{
            RegisterFragment()
        }
    }
}
