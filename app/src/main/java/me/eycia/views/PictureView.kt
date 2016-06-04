package me.eycia.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.picture_view_item.view.*
import me.eycia.api.API
import me.eycia.api.MsgBase
import me.eycia.msghub_android.R
import me.eycia.msghub_android.guider
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class PictureView(internal var activity: Activity, var view: View, var PicViews: Array<SimpleDraweeView>) : BaseView(view) {
    private var msgBase: MsgBase? = null

    fun SetUpdateNine() {
        view.mBody.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                view.mBody.viewTreeObserver.removeOnGlobalLayoutListener(this)
                width = Math.max(width, view.mBody.width)
                UpdateNineLayout()
            }
        })
    }

    fun UpdateNineLayout() {
        run {
            var i = 0
            while (i < 9 && i < msgBase!!.PicRefs.size) {
                this.PicViews[i].visibility = View.VISIBLE
                val lp = this.PicViews[i].layoutParams
                if (width > 0) {
                    lp.height = width / 3
                    lp.width = width / 3
                }
                this.PicViews[i].layoutParams = lp
                i++
            }
        }

        for (i in msgBase!!.PicRefs.size..8) {
            this.PicViews[i].visibility = View.GONE
        }
    }

    override fun UpdateInfo(msgBase: MsgBase) {
        this.msgBase = msgBase

        view.ItemTitle.visibility = View.VISIBLE
        view.ItemText.visibility = View.VISIBLE
        view.ItemTitle.text = msgBase.Title
        view.ItemAuthor.text = msgBase.AuthorName
        view.ItemText.text = msgBase.SubTitle
        if (view.ItemTitle.text.length == 0) {
            view.ItemTitle.visibility = View.GONE
        }
        if (view.ItemText.text.length == 0) {
            view.ItemText.visibility = View.GONE
        }

        view.ItemTime.text = PrettyTime().format(Date(msgBase.PubTime * 1000))
        view.ItemAuthorHead.setImageURI(Uri.parse(msgBase.AuthorCoverImg))

        width = Math.max(view.mBody.width, width)

        this.SetUpdateNine()
        this.UpdateNineLayout()

        for (i in 0..Math.min(8, msgBase.PicRefs.size - 1)) {
            this.PicViews[i].setImageURI(Uri.parse(msgBase.PicRefs[i].Url))
        }

        for (i in msgBase.PicRefs.size..8) {
            this.PicViews[i].setImageURI(Uri.EMPTY)
        }
    }

    private class OnPictureClickListener(val clicked_pic: Int, val pictureView: PictureView?) : View.OnClickListener {

        override fun onClick(v: View) {
            if (pictureView != null) {
                val intent = Intent(pictureView.activity, guider::class.java)
                intent.putExtra("mid", pictureView.msgBase!!.Id)
                intent.putExtra("clicked_pic", clicked_pic)

                pictureView.activity.startActivity(intent)
            }
        }
    }

    override fun ViewType(): Int {
        return API.VIEW_PICTURE
    }

    companion object {
        internal var width: Int = 0

        fun GetView(context: Context, parent: ViewGroup, activity: Activity): PictureView {
            val inflater = LayoutInflater.from(context)
            val convertView = inflater.inflate(R.layout.picture_view_item, parent, false)

            val viewHolder = PictureView(activity, convertView, arrayOf(convertView.PIC11, convertView.PIC12, convertView.PIC13,
                    convertView.PIC21, convertView.PIC22, convertView.PIC23, convertView.PIC31, convertView.PIC32, convertView.PIC33))

            viewHolder.SetUpdateNine()

            //viewHolder.ItemText.setAutoLinkMask(Linkify.WEB_URLS);
            //viewHolder.ItemText.setMovementMethod(LinkMovementMethod.getInstance());

            for (i in 0..8) {
                viewHolder.PicViews[i].setOnClickListener(OnPictureClickListener(i, viewHolder))
            }

            convertView.tag = viewHolder

            return viewHolder
        }
    }
}
