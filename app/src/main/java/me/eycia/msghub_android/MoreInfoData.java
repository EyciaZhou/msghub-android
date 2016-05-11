package me.eycia.msghub_android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import me.eycia.api.API;
import me.eycia.api.Msg;

/**
 * Created by eycia on 16/5/11.
 */
public class MoreInfoData {
    private MoreInfoActivity activity;
    private Msg msg;

    private String msgid;

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    public MoreInfoData(MoreInfoActivity activity, String msgid, Bundle saved) {
        this.activity = activity;
        this.msgid = msgid;
        if (saved != null) {
            msg = (Msg) saved.get("msg");
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (msg != null) {
            savedInstanceState.putParcelable("msg", msg);
        }
    }

    private void NotifyDateUpdate() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                activity.NotifyDateUpdate();
            }
        });
    }

    public Msg GetMsg() {
        return msg;
    }

    public void UpdateMsg() {
        API.MsgCallback(msgid, new API.Callback() {
            @Override
            public void Successful(Object o) {
                msg = (Msg) o;
                NotifyDateUpdate();
            }

            @Override
            public void Error(Exception e) {
            }
        });
    }
}
