package com.example.loopdaily.lib;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.viewpager.widget.ViewPager;


/**
 * 滑动页面工具类
 * <p>
 * Created by XiaobingLiu on 2016/10/21.
 */
public class ViewPagerUtil extends ViewPager {

    private boolean scrollble = false;   //是否可滑动

    public ViewPagerUtil(Context context) {
        super(context);
    }

    public ViewPagerUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollble) {
            return false;
        }
        return super.onTouchEvent(ev);
    }


    public boolean isScrollble() {
        return scrollble;
    }

    /**
     * 外部接口控制页面的滑动
     *
     * @param scrollble
     */
    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childSize = getChildCount();
        int maxHeight = 0;
        for (int i = 0; i < childSize; i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            if (child.getMeasuredHeight() > maxHeight) {
                maxHeight = child.getMeasuredHeight();
            }
        }

        if (maxHeight > 0) {
            setMeasuredDimension(getMeasuredWidth(), maxHeight);
        }

    }
}