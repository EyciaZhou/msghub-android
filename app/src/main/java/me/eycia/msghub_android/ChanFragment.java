package me.eycia.msghub_android;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import me.eycia.Notifier;
import me.eycia.api.API;
import me.eycia.api.ChanInfo;
import me.eycia.views.BaseView;
import me.eycia.views.NormalView;
import me.eycia.views.PictureView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChanFragment extends Fragment {
    private RecyclerView lv;
    private SwipeRefreshLayout mrl;
    private ChanFragmentData mChanFragmentData;

    LinearLayoutManager mLayoutManager;

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

    private RecyclerView.Adapter<BaseView> RecyclerViewAdapter = new RecyclerView.Adapter<BaseView>() {
        @Override
        public BaseView onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == API.VIEW_NORMAL) {
                return NormalView.GetView(getContext(), parent, getActivity());
            } else if (viewType == API.VIEW_PICTURE) {
                return PictureView.GetView(getContext(), parent, getActivity());
            } else {
                return null;
            }
        }

        @Override
        public void onBindViewHolder(BaseView holder, int position) {
            holder.UpdateInfo(mChanFragmentData.GetItem(position));
        }

        @Override
        public int getItemCount() {
            return mChanFragmentData.GetCount();
        }

        @Override
        public int getItemViewType(int position) {
            return mChanFragmentData.GetItem(position).ViewType;
        }
    };

    public void UpdateView() {
        mrl.setRefreshing(false);
        RecyclerViewAdapter.notifyDataSetChanged();
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
        lv = (RecyclerView) rootView.findViewById(R.id.listView2);

        lv.setAdapter(RecyclerViewAdapter);

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

        lv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mrl.setEnabled(topRowVerticalPosition >= 0);


                if (dy > 0) {
                    int visibleItemCount = recyclerView.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        mChanFragmentData.GetOlder();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv.setLayoutManager(mLayoutManager);

        return rootView;
    }
}
