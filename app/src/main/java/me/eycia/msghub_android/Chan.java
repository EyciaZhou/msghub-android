package me.eycia.msghub_android;

/**
 * Created by eycia on 2/28/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.eycia.view.refreshLis;

/**
 * A placeholder fragment containing a simple view.
 */
public class Chan extends Fragment {
    private ChanInfo chanInfo;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Chan newInstance(ChanInfo chanInfo) {
        Chan fragment = new Chan();
        fragment.chanInfo = chanInfo;
        return fragment;
    }

    public Chan() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msgs_display, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(chanInfo.Title);

        SwipeRefreshLayout mrl = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);

        mrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        return rootView;
    }
}
