package com.sisucon.loopdaily.ViewHolder

import android.os.Handler
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import com.sisucon.loopdaily.Model.ActionModel
import com.sisucon.loopdaily.R
import com.sisucon.loopdaily.Util.Utils
import com.jude.easyrecyclerview.adapter.BaseViewHolder

class ActionViewHolder (parent: ViewParent): BaseViewHolder<ActionModel>(parent as ViewGroup?, R.layout.item_action){
    lateinit var img:ImageView
    lateinit var name:TextView
    init {
        name = itemView.findViewById(R.id.action_name)
        img = itemView.findViewById(R.id.action_img)
    }

    override fun setData(data: ActionModel) {
        name.text = data.name
        Handler(context?.mainLooper).post(Runnable {
            Utils.getInstance(context).GetImg(context.getString(R.string.server_host_file)+"/upload/actionDefault/"+data.id+"/"+data.imageName,img)
        })
    }
}