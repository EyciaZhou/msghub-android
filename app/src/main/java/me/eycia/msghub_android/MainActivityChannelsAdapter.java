package me.eycia.msghub_android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.eycia.api.API;
import me.eycia.api.ChanInfo;

/**
 * Created by eycia on 2/27/16.
 */
public class MainActivityChannelsAdapter extends FragmentPagerAdapter {
    private MainActivity activity;

    private ChanInfo[] chans;

    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private Handler cronHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArray("chanInfos", chans);
    }

    private void NotifyUI() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                activity.UpdateUI();
            }
        });
    }

    private void CallAPI() {
        API.ChansInfoCallback(new API.Callback() {
            @Override
            public void Successful(Object o) {
                chans = (ChanInfo[]) (o);
                NotifyUI();
            }

            @Override
            public void Error(Exception e) {
            }
        });
    }

    private Runnable cronUpdateChansInfo = new Runnable() {
        @Override
        public void run() {
            CallAPI();
            cronHandler.postDelayed(this, 10000);
        }
    };

    public MainActivityChannelsAdapter(FragmentManager fm, MainActivity activity, Bundle savedInstanceState) {
        super(fm);
        this.activity = activity;
        chans = new ChanInfo[0];
        cronHandler.post(cronUpdateChansInfo);

        if (savedInstanceState == null) {
            CallAPI();
        } else {
            chans = (ChanInfo[]) savedInstanceState.getParcelableArray("chanInfos");
            NotifyUI();
        }
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return ChanFragment.newInstance(chans[position]);
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
