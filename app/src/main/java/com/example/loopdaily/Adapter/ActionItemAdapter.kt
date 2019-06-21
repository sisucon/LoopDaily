package com.example.loopdaily.Adapter

import android.content.Context
import android.view.ViewGroup
import com.example.loopdaily.Model.ActionModel
import com.example.loopdaily.ViewHolder.ActionViewHolder
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter

class ActionItemAdapter (context: Context): RecyclerArrayAdapter<ActionModel>(context){
    override fun OnCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*> {
        return ActionViewHolder(parent!!)
    }

}