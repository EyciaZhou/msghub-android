package me.eycia.msghub_android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_msgs_display.*
import kotlinx.android.synthetic.main.fragment_msgs_display.view.*
import me.eycia.api.API
import me.eycia.api.ChanInfo
import me.eycia.views.BaseView
import me.eycia.views.NormalView
import me.eycia.views.PictureView

class ChanFragment() : Fragment() {
    private var mChanFragmentData: ChanFragmentData = ChanFragmentData()

    private val RecyclerViewAdapter = object : RecyclerView.Adapter<BaseView>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseView? {
            if (viewType == API.VIEW_NORMAL) {
                return NormalView.GetView(context, parent, activity)
            } else if (viewType == API.VIEW_PICTURE) {
                return PictureView.GetView(context, parent, activity)
            } else {
                return null
            }
        }

        override fun onBindViewHolder(holder: BaseView, position: Int) = holder.UpdateInfo(mChanFragmentData.getItem(position))

        override fun getItemCount(): Int = mChanFragmentData.getCount()

        override fun getItemViewType(position: Int): Int = mChanFragmentData.getItem(position).ViewType
    }

    fun UpdateView() {
        mSwipeRefreshLayout.isRefreshing = false
        RecyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        super.onSaveInstanceState(savedInstanceState)
        mChanFragmentData.onSaveInstanceState(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (savedInstanceState != null) {
            mChanFragmentData = ChanFragmentData(savedInstanceState)
        }
        mChanFragmentData.MsgLinesNotifier.addOnDataChangeListener { activity.runOnUiThread { UpdateView() } }

        val rootView = inflater!!.inflate(R.layout.fragment_msgs_display, container, false)

        rootView.mRecyclerView.adapter = RecyclerViewAdapter

        val refreshListener = SwipeRefreshLayout.OnRefreshListener { mChanFragmentData.GetNewer() }

        rootView.mSwipeRefreshLayout.setOnRefreshListener(refreshListener)

        if (savedInstanceState == null) {
            rootView.mSwipeRefreshLayout.isRefreshing = true
            refreshListener.onRefresh()
        } else {
            rootView.mSwipeRefreshLayout.isRefreshing = false
            RecyclerViewAdapter.notifyDataSetChanged()
        }

        val mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rootView.mRecyclerView.layoutManager = mLayoutManager

        rootView.mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val topRowVerticalPosition = if (recyclerView == null || recyclerView.childCount == 0) 0 else recyclerView.getChildAt(0).top
                rootView.mSwipeRefreshLayout.isEnabled = topRowVerticalPosition >= 0


                if (dy > 0) {
                    val visibleItemCount = recyclerView!!.childCount
                    val totalItemCount = mLayoutManager.itemCount
                    val pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition()

                    if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                        mChanFragmentData.GetOlder()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        return rootView
    }

    companion object {

        fun newInstance(chanInfo: ChanInfo): ChanFragment {
            val fragment = ChanFragment()
            fragment.mChanFragmentData = ChanFragmentData(chanInfo)
            return fragment
        }
    }
}
