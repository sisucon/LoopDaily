package com.example.loopdaily.Model

import org.litepal.crud.LitePalSupport
import org.litepal.exceptions.DataSupportException
import java.util.*

class ActionDB (var _id:Long,var name:String,var startTime:Date,var loopTime:Long,var type:Int,var isPublic:Boolean,var imagePath:String,var remoteId:Long) : LitePalSupport(){
}