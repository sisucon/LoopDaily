package com.sisucon.loopdaily.Model

import org.litepal.crud.LitePalSupport

class PlanDB (var _id:Long,var remoteId : Long ,var name:String,var user_id : Long,var isLoop:Boolean,var loopTime:Long,var info : String ,var startTime : Long,var isRemind : Boolean,var isFinish:Boolean) : LitePalSupport(){

}