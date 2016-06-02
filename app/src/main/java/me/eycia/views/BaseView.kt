package me.eycia.views

import android.support.v7.widget.RecyclerView
import android.view.View

import me.eycia.api.MsgBase

abstract class BaseView(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun ViewType(): Int

    abstract fun UpdateInfo(msgBase: MsgBase)
}
