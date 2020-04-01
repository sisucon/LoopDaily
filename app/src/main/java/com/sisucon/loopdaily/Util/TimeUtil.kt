package com.sisucon.loopdaily.Util

class TimeUtil {
    companion object {
        val startTime = Utils.getStartTime().time
        val endTime = Utils.getEndTime().time

    }
     fun isToday( target :Long) : Boolean{
        if (target in (startTime + 1) until endTime){
            return true
        }
        return false
    }
}