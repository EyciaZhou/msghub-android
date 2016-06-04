package me.eycia.msghub_android

import android.os.Bundle
import me.eycia.Notifier
import me.eycia.api.API
import me.eycia.api.ChanInfo
import me.eycia.api.MsgBase
import java.util.*

class ChanFragmentData(private var chanInfo: ChanInfo) {
    private var msgLines: Array<MsgBase> = emptyArray()
    private var noMore: Int = 0
    private var isFetching: Boolean = false
    private var noMoreTime: Long = 0

    var MsgLinesNotifier = Notifier()

    constructor(savedInstanceState: Bundle) : this(savedInstanceState.getParcelable<ChanInfo>("chanInfo")) {
        this.msgLines = savedInstanceState.getParcelableArray("msgLines") as Array<MsgBase>
        this.noMore = savedInstanceState.getInt("noMore")
    }

    constructor() : this(ChanInfo("", "", 0)) {
    }

    fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState!!.putParcelableArray("msgLines", msgLines)
        savedInstanceState.putParcelable("chanInfo", chanInfo)
        savedInstanceState.putInt("noMore", noMore)
    }

    fun getCount(): Int {
        return msgLines.size
    }

    fun getItem(index: Int): MsgBase {
        return msgLines[index]
    }

    fun GetNewer() {
        object : API.Msgs.PageGetTask(chanInfo.Id, 20, "0", -1) {
            override fun onSuccess(result: Array<MsgBase>) {
                Arrays.sort(msgLines)
                this@ChanFragmentData.msgLines = result
            }

            override fun onFinish() {
                MsgLinesNotifier.ChangeData()
            }
        }.execute()
    }

    fun GetOlder() {
        if (msgLines.size == 0) return
        if (noMore == 1 && System.currentTimeMillis() - noMoreTime > 10 * 1000) noMore = 0 //if no more message, wait 10 seconds
        if (noMore == 1 || isFetching) return
        isFetching = true

        object : API.Msgs.PageGetTask(chanInfo.Id, 20, msgLines[msgLines.size - 1].Id,
                msgLines[msgLines.size - 1].SnapTime) {
            override fun onSuccess(result: Array<MsgBase>) {
                if (result.size == 0) { //result's size is zero means no more messages
                    noMore = 1
                    noMoreTime = System.currentTimeMillis()
                    MyApplication.toast("No More")
                    return
                }

                if (msgLines.size == 0) {   //msgLine's size is zero means no messages before, so just replace it
                    msgLines = result
                    MsgLinesNotifier.ChangeData()
                    return
                }

                val DupRemove = HashSet<String>()
                val msgsFinal = ArrayList<MsgBase>()

                for (m in result) {
                    msgsFinal.add(m)
                    DupRemove.add(m.Id)
                }

                for (m in msgLines) {
                    if (!DupRemove.contains(m.Id)) {
                        msgsFinal.add(m)
                        DupRemove.add(m.Id)
                    }
                }

                Collections.sort(msgsFinal)
                msgLines = msgsFinal.toTypedArray()
                MsgLinesNotifier.ChangeData()
            }

            override fun onFinish() {
                isFetching = false
            }
        }.execute()
    }
}
