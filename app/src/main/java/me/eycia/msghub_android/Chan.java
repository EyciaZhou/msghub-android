package me.eycia.msghub_android;

/**
 * Created by eycia on 2/28/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * A placeholder fragment containing a simple view.
 */
public class Chan extends Fragment {
    private ChanInfo chanInfo;
    private MsgInfo[] msgInfos;

    private Set<String> msgIds;

    private ListView lv;
    private SwipeRefreshLayout mrl;

    private boolean noMore;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Chan newInstance(ChanInfo chanInfo) {
        Chan fragment = new Chan();
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("chanInfo", chanInfo);

        fragment.msgInfos = new MsgInfo[0];
        fragment.noMore = false;
        fragment.msgIds = new HashSet<>();

        fragment.setArguments(mBundle);
        return fragment;
    }

    public Chan() {
    }

    private void getNew() {
        API.PageCallback(chanInfo.Id, 20, "0", -1, new API.Callback() {

            @Override
            public void Succ(final Object o) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mrl.setRefreshing(false);

                        if (o != null) {
                            MsgInfo[] msgs = (MsgInfo[])(o);

                            if (msgInfos == null || msgInfos.length == 0) {
                                msgInfos = msgs;
                                Update();
                                return;
                            }

                            msgIds.clear();
                            for (int i = 0; i < msgs.length; i++) {
                                msgIds.add(msgs[i].Id);
                            }

                            //other
                            Vector<MsgInfo> msgsFinal = new Vector<>();

                            for (int i = 0; i < msgs.length; i++) {
                                msgsFinal.add(msgs[i]); //add new first
                            }

                            for (int i = 0; i < msgInfos.length; i++) {
                                if (!msgIds.contains(msgInfos[i].Id)) {
                                    msgsFinal.add(msgInfos[i]);
                                    msgIds.add(msgInfos[i].Id);
                                }
                            }

                            Collections.sort(msgsFinal);

                            msgInfos = msgsFinal.toArray(new MsgInfo[msgsFinal.size()]);

                            Update();
                        }
                    }
                });
            }

            @Override
            public void Err(final Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mrl.setRefreshing(false);

                        Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private void getMore(MsgInfo lst) {
        API.PageCallback(chanInfo.Id, 20, lst.Id, lst.SnapTime, new API.Callback() {

            @Override
            public void Succ(final Object o) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mrl.setRefreshing(false);

                        if (o != null) {
                            MsgInfo[] msgs = (MsgInfo[])(o);

                            if (msgs == null || msgs.length == 0) {
                                noMore = true;
                                Toast.makeText(getActivity().getApplicationContext(), "No More", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (msgInfos == null || msgInfos.length == 0) {
                                msgInfos = msgs;
                                Update();
                                return;
                            }

                            Vector<MsgInfo> msgsFinal = new Vector<>();

                            for (int i = 0; i < msgs.length; i++) {
                                msgsFinal.add(msgs[i]);
                                msgIds.add(msgs[i].Id);
                            }

                            for (int i = 0; i < msgInfos.length; i++) {
                                msgsFinal.add(msgInfos[i]);
                            }

                            Collections.sort(msgsFinal);
                            msgInfos = msgsFinal.toArray(new MsgInfo[msgsFinal.size()]);

                            Update();
                        }
                    }
                });
            }

            @Override
            public void Err(final Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mrl.setRefreshing(false);

                        Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    public void Update() {
        ArrayList<HashMap<String, String>> ls = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < this.msgInfos.length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", this.msgInfos[i].Title);
            map.put("ItemText", this.msgInfos[i].SubTitle);
            ls.add(map);
        }

        int cachePos = lv.getFirstVisiblePosition();

        lv.setAdapter(new SimpleAdapter(getActivity(), ls, R.layout.msg_on_chan, new String[]{"ItemTitle", "ItemText"},
                new int[]{R.id.ItemTitle, R.id.ItemText}));

        lv.setSelectionFromTop(cachePos, 0);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArray("msgInfo", msgInfos);
        savedInstanceState.putBoolean("noMore", noMore);
        savedInstanceState.putSerializable("msgIds", (Serializable) msgIds);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.chanInfo = getArguments().getParcelable("chanInfo");

        View rootView = inflater.inflate(R.layout.fragment_msgs_display, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(chanInfo.Title);

        mrl = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        lv = (ListView) rootView.findViewById(R.id.listView2);


        SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNew();
            }
        };

        mrl.setOnRefreshListener(refreshListener);

        if (savedInstanceState == null) {
            mrl.setRefreshing(true);
            refreshListener.onRefresh();
        } else {
            this.msgInfos = (MsgInfo[])savedInstanceState.getParcelableArray("msgInfo");
            this.noMore = savedInstanceState.getBoolean("noMore");
            this.msgIds = (Set<String>) savedInstanceState.getSerializable("msgIds");
            Update();
        }


        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem >= totalItemCount) {
                    View lastItemView = view.getChildAt(view.getChildCount()-1);
                    if (lastItemView != null && view.getBottom() >= lastItemView.getBottom()) {
                        if (!noMore) {
                            //TODO: show more view
                            if (msgInfos.length > 0) {
                                getMore(msgInfos[msgInfos.length - 1]);
                            }

                        }
                    }
                }
            }
        });

        return rootView;
    }
}
