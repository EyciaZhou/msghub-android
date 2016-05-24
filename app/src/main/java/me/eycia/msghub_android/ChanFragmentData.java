package me.eycia.msghub_android;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import me.eycia.Notifier;
import me.eycia.api.API;
import me.eycia.api.ChanInfo;
import me.eycia.api.MsgLine;

/**
 * Created by eycia on 16/5/10.
 */
public class ChanFragmentData {
    private MsgLine[] msgLines = new MsgLine[0];
    private ChanInfo chanInfo;
    private boolean noMore;
    private boolean isFetching;

    public Notifier MsgLinesNotifier = new Notifier();

    public ChanFragmentData(ChanInfo chanInfo, Bundle savedInstanceState) {
        this.chanInfo = chanInfo;
        if (savedInstanceState != null) {
            this.msgLines = (MsgLine[]) savedInstanceState.getParcelableArray("msgLines");
            this.chanInfo = savedInstanceState.getParcelable("chanInfo");
            this.noMore = savedInstanceState.getBoolean("noMore");
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArray("msgLines", msgLines);
        savedInstanceState.putParcelable("chanInfo", chanInfo);
        savedInstanceState.putBoolean("noMore", noMore);
    }

    public int GetCount() {
        return msgLines.length;
    }

    public MsgLine GetItem(int index) {
        return msgLines[index];
    }

    public void GetNewer() {
        new API.Msgs.PageGetTask(chanInfo.Id, 20, "0", -1) {
            @Override
            protected void onSuccess(@NonNull MsgLine[] msgLines) {
                Arrays.sort(msgLines);
                ChanFragmentData.this.msgLines = msgLines;
            }

            @Override
            protected void onFinish() {
                MsgLinesNotifier.ChangeData();
            }
        }.execute();
    }

    public void GetOlder() {
        if (msgLines.length == 0) return;
        if (noMore || isFetching) return;
        isFetching = true;

        new API.Msgs.PageGetTask(chanInfo.Id, 20, msgLines[msgLines.length - 1].Id,
                msgLines[msgLines.length - 1].SnapTime) {
            @Override
            protected void onSuccess(@NonNull MsgLine[] msgs) {
                if (msgs.length == 0) {
                    noMore = true;
                    Looper.prepare();
                    Toast.makeText(MyApplication.getAppContext(), "No More", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (msgLines == null || msgLines.length == 0) {
                    msgLines = msgs;
                    MsgLinesNotifier.ChangeData();
                    return;
                }

                Set<String> DupRemove = new HashSet<>();

                Vector<MsgLine> msgsFinal = new Vector<>();

                for (MsgLine m : msgs) {
                    msgsFinal.add(m);
                    DupRemove.add(m.Id);
                }

                for (MsgLine m : msgLines) {
                    if (!DupRemove.contains(m.Id)) {
                        msgsFinal.add(m);
                        DupRemove.add(m.Id);
                    }
                }

                Collections.sort(msgsFinal);
                msgLines = msgsFinal.toArray(new MsgLine[msgsFinal.size()]);
                MsgLinesNotifier.ChangeData();
            }

            @Override
            protected void onFinish() {
                isFetching = false;
            }
        }.execute();
    }
}
