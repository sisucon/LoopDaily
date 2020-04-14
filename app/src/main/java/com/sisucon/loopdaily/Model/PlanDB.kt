package com.sisucon.loopdaily.Model

import com.sisucon.loopdaily.Util.PlanJsonRemote
import org.litepal.LitePal
import org.litepal.crud.LitePalSupport

class PlanDB  (var _id:Long,var remoteId : Long ,var name:String,var user_id : Long,var isLoop:Boolean,var loopTime:Long,var info : String ,var startTime : Long,var isRemind : Boolean,var isFinish:Boolean,var isPublish : Boolean) : LitePalSupport(){
constructor(jsonRemote: PlanJsonRemote):this(LitePal.count(PlanDB::class.java).toLong(),jsonRemote.id,jsonRemote.name,jsonRemote.userId,jsonRemote.isLoop,jsonRemote.loopTime,jsonRemote.info,jsonRemote.startTime,jsonRemote.isRemind,jsonRemote.isFinish,true){
}
    fun setData(jsonRemote: PlanJsonRemote){
        PlanDB(_id,jsonRemote.id,jsonRemote.name,jsonRemote.userId,jsonRemote.isLoop,jsonRemote.loopTime,jsonRemote.info,jsonRemote.startTime,jsonRemote.isRemind,jsonRemote.isFinish,true)
    }
}