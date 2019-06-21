package com.example.loopdaily.Util

import com.example.loopdaily.lib.OrderStatus
import java.util.*

data class TimeLineModel(var name:String,var date: Date,var mstatus:OrderStatus,var place:String,var id: Long,var eventId:Long)
data class ActionClassModel(var id : Long,var name:String,var type:Int,var imageName:String,var loopTime:Long,var uploadTime:Int,var successTime:Int)