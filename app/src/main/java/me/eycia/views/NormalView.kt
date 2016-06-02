package me.eycia.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.normal_view_item.view.*
import me.eycia.api.API
import me.eycia.api.MsgBase
import me.eycia.msghub_android.R
import me.eycia.msghub_android.guider
import org.ocpsoft.prettytime.PrettyTime
import java.util.*


class NormalView(internal var activity: Activity, view: View, val mNormalViewHandler: NormalViewHandler) : BaseView(view) {
    class NormalViewHandler(var parent: View) {
        fun ToggleCover(show: Boolean) {
            if (show) {
                parent.ItemCover.visibility = View.VISIBLE
            } else {
                parent.ItemCover.visibility = View.GONE
            }
        }

        fun SetInfo(msgLine: MsgBase) {
            parent.ItemTitle.visibility = View.VISIBLE
            parent.ItemTitle.text = msgLine.Title
            parent.ItemAuthor.text = msgLine.AuthorName
            if (parent.ItemTitle.length() == 0) {
                parent.ItemTitle.visibility = View.GONE
            }

            parent.ItemTime.text = PrettyTime().format(Date(msgLine.PubTime * 1000))
            parent.ListCover.setImageURI(Uri.parse(msgLine.AuthorCoverImg))

            if (msgLine.CoverImg == "") {
                parent.ItemCover.visibility = View.GONE
            } else {
                parent.ItemCover.visibility = View.VISIBLE
                parent.ItemCover.setImageURI(Uri.parse(msgLine.CoverImg))
            }
        }
    }

    private var msgLine: MsgBase? = null

    override fun ViewType(): Int {
        return API.VIEW_NORMAL
    }

    override fun UpdateInfo(msgBase: MsgBase) {
        this.msgLine = msgBase
        this.mNormalViewHandler.SetInfo(msgBase)
    }

    private class OnNormalClickListener(var normalView: NormalView) : View.OnClickListener {

        override fun onClick(v: View) {
            if (normalView.msgLine != null) {
                val intent = Intent(normalView.activity, guider::class.java)
                intent.putExtra("mid", normalView.msgLine!!.Id)

                normalView.activity.startActivity(intent)
            }
        }
    }

    companion object {

        fun GetView(context: Context, parent: ViewGroup, activity: Activity): NormalView {
            val inflater = LayoutInflater.from(context)
            val convertView = inflater.inflate(R.layout.normal_view_item, parent, false)

            val viewHolder = NormalView(activity, convertView, NormalViewHandler(convertView))

            convertView.setOnClickListener(OnNormalClickListener(viewHolder))
            convertView.tag = viewHolder

            return viewHolder
        }
    }

}
