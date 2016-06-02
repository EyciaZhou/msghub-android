package me.eycia.msghub_android

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.activity_msg.*
import me.eycia.api.MsgBase
import me.eycia.api.PicRef
import me.eycia.views.NormalView
import java.util.*

private const val TYPE_FIRST_TITLE = 0
private const val TYPE_TEXT = 1
private const val TYPE_PICTURE = 2

class MoreInfoActivity : AppCompatActivity() {
    private var mMsg: MsgBase? = null

    internal class row(var type: Int, var content: String)

    fun NotifyDateUpdate(m: MsgBase) {
        mRecyclerView.adapter = moreInfoAdapter(m)
        mTopCover.setImageURI(Uri.parse(m.CoverImg))
        supportActionBar!!.title = m.Title
    }

    internal abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var Type: Int = 0
    }

    internal class TextHolder(itemView: View) : Holder(itemView) {
        private var mTextView: WebView? = null

        fun Bind(data: String) {
            mTextView!!.loadDataWithBaseURL("", data, null, "utf-8", "")
        }

        companion object {
            fun getView(parent: ViewGroup): TextHolder {
                val tv = WebView(parent.context)
                val holder = TextHolder(tv)
                holder.mTextView = tv
                holder.Type = TYPE_TEXT

                return holder
            }
        }
    }

    internal class PictureHolder(itemView: View) : Holder(itemView) {
        private var mSimpleDraweeView: SimpleDraweeView? = null
        private var parentWidth = 0

        fun Bind(data: PicRef) {
            mSimpleDraweeView!!.controller = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(true)
                    .setTapToRetryEnabled(true)
                    .setUri(data.Url)
                    .build()

            Log.d("msghub", (parentWidth - 100).toString())
            Log.d("msghub", ((parentWidth - 100) * data.py / (data.px + 1)).toString())

            val lp = mSimpleDraweeView!!.layoutParams
            lp.width = parentWidth - 100
            lp.height = (parentWidth - 100) * data.py / (data.px + 1)
            mSimpleDraweeView!!.layoutParams = lp
        }

        companion object {

            fun getView(parent: ViewGroup): PictureHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.picture_in_more, parent, false)
                val holder = PictureHolder(view)
                holder.mSimpleDraweeView = view.findViewById(R.id.PictureField) as SimpleDraweeView
                holder.parentWidth = parent.width
                holder.Type = TYPE_PICTURE
                return holder
            }
        }
    }

    internal class FirstTitleHolder(itemView: View) : Holder(itemView) {
        private var mHandler: NormalView.NormalViewHandler? = null

        fun Bind(data: MsgBase) {
            mHandler!!.SetInfo(data)
        }

        companion object {

            fun getView(parent: ViewGroup): FirstTitleHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.normal_view_item, parent, false)
                val holder = FirstTitleHolder(view)
                holder.mHandler = NormalView.NormalViewHandler(view)
                holder.Type = TYPE_FIRST_TITLE
                holder.mHandler!!.ToggleCover(false)
                return holder
            }
        }
    }

    private inner class moreInfoAdapter : RecyclerView.Adapter<Holder> {
        private val msg: MsgBase
        private var rows: List<row> = ArrayList()
        private var RefToPicRef: MutableMap<String, PicRef> = HashMap()

        constructor(msg: MsgBase) {
            SplitContent(msg)
            this.msg = msg
            notifyDataSetChanged()
        }

        private fun SplitContent(mMsg: MsgBase) {
            val _rows = ArrayList<row>()
            val _RefToPicRef = HashMap<String, PicRef>()
            val result = " " + mMsg.Body + " "

            val picPositions = ArrayList<Pair<Int, Int>>()
            for (i in mMsg.PicRefs.indices) {
                val p = mMsg.PicRefs[i]
                if (p.Ref != "") {
                    _RefToPicRef.put(p.Ref, p)
                    val index = result.indexOf(p.Ref)
                    if (index >= 0) {
                        picPositions.add(Pair<Int, Int>(index, index + p.Ref.length))
                    }
                }
            }

            Collections.sort(picPositions) { lhs, rhs -> Integer.compare(lhs.first, rhs.first) }

            var last_position = 0
            for (i in picPositions.indices) {
                if (last_position <= picPositions[i].first) {
                    _rows.add(row(TYPE_TEXT, result.substring(last_position, picPositions[i].first)))
                }
                _rows.add(row(TYPE_PICTURE, result.substring(picPositions[i].first, picPositions[i].second)))
                last_position = picPositions[i].second
            }
            _rows.add(row(TYPE_TEXT, result.substring(last_position, result.length)))

            rows = _rows
            RefToPicRef = _RefToPicRef
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder? {
            if (viewType == TYPE_FIRST_TITLE) {
                return FirstTitleHolder.getView(parent)
            } else if (viewType == TYPE_TEXT) {
                return TextHolder.getView(parent)
            } else if (viewType == TYPE_PICTURE) {
                return PictureHolder.getView(parent)
            } else {
                return null
            }
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            if (holder.Type == TYPE_FIRST_TITLE) {
                (holder as FirstTitleHolder).Bind(msg)
            } else if (holder.Type == TYPE_TEXT) {
                (holder as TextHolder).Bind(rows[position - 1].content)
            } else if (holder.Type == TYPE_PICTURE) {
                (holder as PictureHolder).Bind(RefToPicRef[rows[position - 1].content]!!)
            }
        }

        override fun getItemCount(): Int {
            return 1 + rows.size
        }

        override fun getItemViewType(position: Int): Int {
            if (position == 0) return TYPE_FIRST_TITLE
            return rows[position - 1].type
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_msg)

        val toolbar = findViewById(R.id.toolbar2) as Toolbar
        setSupportActionBar(toolbar)

        mRecyclerView.layoutManager = LinearLayoutManager(this)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val mAppBarLayout = findViewById(R.id.appbar2) as AppBarLayout
        mAppBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = Math.abs(verticalOffset).toFloat() / maxScroll.toFloat()

            mTopCover.alpha = 1 - percentage
            mGradientView.alpha = 1 - percentage
        }

        val m: MsgBase

        if (savedInstanceState != null) {
            m = savedInstanceState.getParcelable<MsgBase>("m")
        } else {
            val intent = intent
            m = intent.getParcelableExtra<MsgBase>("m")
        }

        NotifyDateUpdate(m)

        mMsg = m
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putParcelable("m", mMsg)
    }
}
