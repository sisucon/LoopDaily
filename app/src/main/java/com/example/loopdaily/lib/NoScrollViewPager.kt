package com.example.loopdaily.lib

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class NoScrollViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    var noScroll = false


    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return !noScroll&&super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return !noScroll&&super.onInterceptTouchEvent(ev)
    }

    override fun setCurrentItem(item: Int) {
//        去除滚动效果
        super.setCurrentItem(item,false)
    }
}