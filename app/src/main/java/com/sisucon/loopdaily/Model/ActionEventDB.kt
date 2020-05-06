package com.sisucon.loopdaily.Model

import org.litepal.crud.LitePalSupport
import java.util.*

class ActionEventDB (var id:Long, var startDay:Date, var index:Int, var actionId: Long, var remoteId:Long, var isSuccess:Boolean, var time:Date, var isDeleted : Boolean = false): LitePalSupport()