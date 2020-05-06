package com.sisucon.loopdaily.Model

import org.litepal.crud.LitePalSupport
import java.util.*

class ActionDB (var id:Long, var name:String, var startTime:Date, var loopTime:Long, var type:Int, var isPublic:Boolean, var imagePath:String, var remoteId:Long) : LitePalSupport(){
}