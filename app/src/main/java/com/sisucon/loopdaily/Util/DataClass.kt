package com.sisucon.loopdaily.Util

import com.sisucon.loopdaily.lib.OrderStatus
import java.util.*

data class TimeLineModel(var name:String,var date: Date,var mstatus:OrderStatus,var place:String,var id: Long,var eventId:Long,var type : Int)
data class ActionClassModel(var id : Long,var name:String,var type:Int,var imageName:String,var loopTime:Long,var uploadTime:Int,var successTime:Int)
data class PlanJson(var name:String ,var isLoop:Boolean,var loopTime:Long ,var startTime : Long,var isRemind : Boolean,var isFinish:Boolean,var remoteId:Long?)
data class PlanJsonRemote (
    var id:Long ,
    var name:String,
    var userId : Long,
    var loop:Boolean,
    var loopTime:Long,
    var info : String="" ,
    var startTime : Long
    ,var remind : Boolean
    ,var finish:Boolean
)