package me.eycia.msghub_android

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import me.eycia.Notifier
import me.eycia.api.API
import me.eycia.api.ChanInfo

class MainActivityChannelsAdapter(fm: FragmentManager, savedInstanceState: Bundle?) : FragmentPagerAdapter(fm) {
    private var chans: Array<ChanInfo>

    private val cronHandler = Handler()
    var ChansNotifier = Notifier()

    fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putParcelableArray("chanInfos", chans)
    }

    private fun ChangeChansData(cs: Array<ChanInfo>) {
        chans = cs
        notifyDataSetChanged()
        ChansNotifier.ChangeData()
    }

    private fun CallAPI() {
        object : API.Msgs.ChansInfoTask() {
            override fun onSuccess(result: Array<ChanInfo>) {
                ChangeChansData(result)
            }
        }.execute()
    }

    private val cronUpdateChansInfo = Runnable {
        CallAPI()
        //cronHandler.postDelayed(this, 10000);
    }

    init {
        chans = emptyArray()
        cronHandler.post(cronUpdateChansInfo)

        if (savedInstanceState == null) {
            CallAPI()
        } else {
            ChangeChansData(savedInstanceState.getParcelableArray("chanInfos") as Array<ChanInfo>)
        }
    }

    override fun getItem(position: Int): ChanFragment {
        return ChanFragment.newInstance(chans[position])
    }

    override fun getCount(): Int {
        return chans.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return chans[position].Title
    }
}
