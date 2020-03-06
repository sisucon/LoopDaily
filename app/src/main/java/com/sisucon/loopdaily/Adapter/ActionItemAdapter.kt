package com.sisucon.loopdaily.Adapter

import android.content.Context
import android.view.ViewGroup
import com.sisucon.loopdaily.Model.ActionModel
import com.sisucon.loopdaily.ViewHolder.ActionViewHolder
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter

class ActionItemAdapter (context: Context): RecyclerArrayAdapter<ActionModel>(context){
    override fun OnCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*> {
        return ActionViewHolder(parent!!)
    }

}