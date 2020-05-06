package com.sisucon.loopdaily.Model

import org.litepal.crud.LitePalSupport
import java.util.*

class PlanEventDB(var _id:Long, var startDay:Date, var planId : Long, var isSuccess:Boolean, var isPublish : Boolean, var time:Date, var name : String, var isLoop:Boolean): LitePalSupport(){
    var remoteId:Long = -1

}
