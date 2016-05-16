package me.eycia.msghub_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import me.eycia.Notifier;
import me.eycia.api.API;
import me.eycia.api.ChanInfo;
import me.eycia.api.MsgLine;
import me.eycia.views.BaseView;
import me.eycia.views.NormalView;
import me.eycia.views.PictureView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChanFragment extends Fragment {
    private ListView lv;
    private SwipeRefreshLayout mrl;
    private ChanFragmentData mChanFragmentData;

    private ChanInfo chanInfo;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ChanFragment newInstance(ChanInfo chanInfo) {
        ChanFragment fragment = new ChanFragment();
        fragment.chanInfo = chanInfo;
        return fragment;
    }

    public ChanFragment() {
    }

    private BaseAdapter ListViewAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mChanFragmentData.GetCount();
        }

        @Override
        public Object getItem(int position) {
            return mChanFragmentData.GetItem(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MsgLine msgLine = (MsgLine) getItem(position);

            if (convertView == null || ((BaseView) convertView.getTag()).ViewType() != msgLine.ViewType) {
                if (msgLine.ViewType == API.VIEW_NORMAL) {
                    convertView = NormalView.GetView(getContext(), parent, getActivity());
                } else if (msgLine.ViewType == API.VIEW_PICTURE) {
                    convertView = PictureView.GetView(getContext(), parent, getActivity());
                }
            }

            if (convertView != null) {
                ((BaseView) convertView.getTag()).UpdateInfo(msgLine);
            }

            return convertView;
        }
    };

    public void UpdateView() {
        mrl.setRefreshing(false);
        ListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("chaninfo", chanInfo);
        mChanFragmentData.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            chanInfo = (ChanInfo) savedInstanceState.get("chaninfo");
        }

        mChanFragmentData = new ChanFragmentData(chanInfo, savedInstanceState);
        mChanFragmentData.MsgLinesNotifier.addOnDataChangeListener(new Notifier.OnDataChangeListener() {
            @Override
            public void OnDataChange() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateView();
                    }
                });
            }
        });

        View rootView = inflater.inflate(R.layout.fragment_msgs_display, container, false);

        mrl = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        lv = (ListView) rootView.findViewById(R.id.listView2);

        lv.setAdapter(ListViewAdapter);

        SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mChanFragmentData.GetNewer();
            }
        };

        mrl.setOnRefreshListener(refreshListener);

        if (savedInstanceState == null) {
            mrl.setRefreshing(true);
            refreshListener.onRefresh();
        } else {
            UpdateView();
        }

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem >= totalItemCount) {
                    View lastItemView = view.getChildAt(view.getChildCount() - 1);
                    if (lastItemView != null && view.getBottom() >= lastItemView.getBottom()) {
                        mChanFragmentData.GetOlder();
                    }
                }
            }
        });

        return rootView;
    }
}
