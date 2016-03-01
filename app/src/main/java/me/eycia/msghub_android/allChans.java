package me.eycia.msghub_android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by eycia on 2/27/16.
 */
public class allChans extends FragmentPagerAdapter  {
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    private ChanInfo[] chans;

    public allChans(FragmentManager fm) {
        super(fm);

        chans = new ChanInfo[0];
    }

    public void Update(ChanInfo[] cs) {
        this.chans = cs;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return Chan.newInstance(chans[position]);
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
