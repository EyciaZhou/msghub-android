package me.eycia.msghub_android;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.eycia.Notifier;
import me.eycia.api.API;
import me.eycia.api.ChanInfo;

/**
 * Created by eycia on 2/27/16.
 */
public class MainActivityChannelsAdapter extends FragmentPagerAdapter {
    private ChanInfo[] chans;

    private Handler cronHandler = new Handler();
    public Notifier ChansNotifier = new Notifier();

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArray("chanInfos", chans);
    }

    private void ChangeChansData(final ChanInfo[] cs) {
        chans = cs;
        notifyDataSetChanged();
        ChansNotifier.ChangeData();
    }

    private void CallAPI() {
        new API.ChansInfoTask() {
            @Override
            protected void onSuccess(@NonNull ChanInfo[] chanInfos) {
                ChangeChansData(chanInfos);
            }
        }.execute();
    }

    private Runnable cronUpdateChansInfo = new Runnable() {
        @Override
        public void run() {
            CallAPI();
            cronHandler.postDelayed(this, 10000);
        }
    };

    public MainActivityChannelsAdapter(FragmentManager fm, Bundle savedInstanceState) {
        super(fm);
        chans = new ChanInfo[0];
        cronHandler.post(cronUpdateChansInfo);

        if (savedInstanceState == null) {
            CallAPI();
        } else {
            ChangeChansData((ChanInfo[]) savedInstanceState.getParcelableArray("chanInfos"));
        }
    }

    @Override
    public ChanFragment getItem(int position) {
        ChanFragment cf = ChanFragment.newInstance(chans[position]);
        return cf;
    }

    @Override
    public int getCount() {
        return chans.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return chans[position].Title;
    }
}
