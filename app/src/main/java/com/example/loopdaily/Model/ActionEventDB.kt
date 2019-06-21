package com.example.loopdaily.Model

import org.litepal.LitePal
import org.litepal.crud.LitePalSupport
import java.util.*

class ActionEventDB (var _id:Long,var startDay:Date,var index:Int,var actionId: Long,var remoteId:Long,var isSuccess:Boolean,var time:Date): LitePalSupport()