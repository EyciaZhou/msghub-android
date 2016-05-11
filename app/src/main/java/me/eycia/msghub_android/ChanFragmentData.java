package me.eycia.msghub_android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import me.eycia.api.API;
import me.eycia.api.ChanInfo;
import me.eycia.api.MsgLine;

/**
 * Created by eycia on 16/5/10.
 */
public class ChanFragmentData {
    private ChanFragment fragment;
    private MsgLine[] msgLines = new MsgLine[0];
    private ChanInfo chanInfo;
    private boolean noMore;

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    private void NotifyUI() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                fragment.UpdateView();
            }
        });
    }

    public ChanFragmentData(ChanFragment chanFragment, ChanInfo chanInfo, Bundle savedInstanceState) {
        this.fragment = chanFragment;
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
        API.PageCallback(chanInfo.Id, 20, "0", -1, new API.Callback() {

            @Override
            public void Successful(final Object o) {
                if (o != null) {
                    MsgLine[] msgs = (MsgLine[]) (o);
                    Arrays.sort(msgs);
                    msgLines = msgs;
                }
                NotifyUI();
            }

            @Override
            public void Error(final Exception e) {
                NotifyUI();
            }
        });
    }

    public void GetOlder() {
        if (msgLines.length == 0) return;

        API.PageCallback(chanInfo.Id, 20, msgLines[msgLines.length - 1].Id,
                msgLines[msgLines.length - 1].SnapTime, new API.Callback() {

                    @Override
                    public void Successful(final Object o) {
                        if (o != null) {
                            MsgLine[] msgs = (MsgLine[]) (o);

                            if (msgs.length == 0) {
                                noMore = true;
                                Toast.makeText(MyApplication.getAppContext(), "No More", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (msgLines == null || msgLines.length == 0) {
                                msgLines = msgs;
                                fragment.UpdateView();
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

                            NotifyUI();
                        }
                    }

                    @Override
                    public void Error(final Exception e) {

                    }
                });
    }
}