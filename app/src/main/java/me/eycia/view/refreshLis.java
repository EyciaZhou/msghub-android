package me.eycia.view;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

/**
 * Created by eycia on 2/25/16.
 */
public class refreshLis implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeLayout;
    private ListView mListView;

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
            }
        }, 5000);
    }

    public refreshLis(SwipeRefreshLayout swipeRefreshLayout) {
        this.mSwipeLayout = swipeRefreshLayout;
    }
}