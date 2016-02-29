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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class Chan extends Fragment {
    private ChanInfo chanInfo;

    private MsgInfo[] msgInfos;

    private ListView lv;
    private SwipeRefreshLayout mrl;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Chan newInstance(ChanInfo chanInfo) {
        Chan fragment = new Chan();

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("chanInfo", chanInfo);

        fragment.setArguments(mBundle);

        return fragment;
    }

    public Chan() {
    }

    public void Update(MsgInfo[] msgInfos) {
        this.msgInfos = msgInfos;   //add not replace

        ArrayList<HashMap<String, String>> ls = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < this.msgInfos.length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", this.msgInfos[i].Title);
            map.put("ItemText", this.msgInfos[i].SubTitle);
            ls.add(map);
        }

        lv.setAdapter(new SimpleAdapter(getActivity(), ls, R.layout.msg_on_chan, new String[]{"ItemTitle", "ItemText"},
                new int[]{R.id.ItemTitle, R.id.ItemText}));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArray("msgInfo", msgInfos);
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
                API.PageCallback(chanInfo.Id, 20, "0", -1, new API.Callback() {

                    @Override
                    public void Succ(final Object o) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mrl.setRefreshing(false);

                                if (o != null) {
                                    Update((MsgInfo[])(o));
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
        };

        mrl.setOnRefreshListener(refreshListener);

        if (savedInstanceState == null) {
            mrl.setRefreshing(true);
            refreshListener.onRefresh();
        } else {
            Update((MsgInfo[])savedInstanceState.getParcelableArray("msgInfo"));
        }

        return rootView;
    }
}
