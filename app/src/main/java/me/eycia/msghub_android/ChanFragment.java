package me.eycia.msghub_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import me.eycia.api.API;
import me.eycia.api.ChanInfo;
import me.eycia.api.MsgLine;

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

    public void UpdateView() {
        mrl.setRefreshing(false);

        final int cachePos = lv.getFirstVisiblePosition();

        lv.setAdapter(new BaseAdapter() {
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

            class BaseViewHolder {
                int ViewType;
            }

            class NormalViewHolder extends BaseViewHolder {
                TextView ItemTitle;
                TextView ItemTime;
                TextView ItemText;
                SimpleDraweeView ListCover;

                NormalViewHolder() {
                    this.ViewType = API.VIEW_NORMAL;
                }
            }

            class PictureViewHolder extends BaseViewHolder {
                TextView ItemTitle;
                TextView ItemTime;
                TextView ItemText;
                SimpleDraweeView ListCover;

                SimpleDraweeView Pics[] = new SimpleDraweeView[9];

                PictureViewHolder() {
                    this.ViewType = API.VIEW_PICTURE;
                }
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                MsgLine msgLine = (MsgLine) getItem(position);
                NormalViewHolder nViewHolder = null;
                PictureViewHolder pViewHolder = null;

                if (convertView == null || ((BaseViewHolder) convertView.getTag()).ViewType != msgLine.ViewType) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());

                    if (msgLine.ViewType == API.VIEW_NORMAL) {
                        convertView = inflater.inflate(R.layout.msg_on_chan, null);

                        nViewHolder = new NormalViewHolder();

                        nViewHolder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
                        nViewHolder.ItemTime = (TextView) convertView.findViewById(R.id.ItemTime);
                        nViewHolder.ItemText = (TextView) convertView.findViewById(R.id.ItemText);
                        nViewHolder.ListCover = (SimpleDraweeView) convertView.findViewById(R.id.ListCover);
                    } else if (msgLine.ViewType == API.VIEW_PICTURE) {
                        convertView = inflater.inflate(R.layout.picture_view, null);

                        pViewHolder = new PictureViewHolder();

                        pViewHolder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
                        pViewHolder.ItemTime = (TextView) convertView.findViewById(R.id.ItemTime);
                        pViewHolder.ItemText = (TextView) convertView.findViewById(R.id.ItemText);
                        pViewHolder.ListCover = (SimpleDraweeView) convertView.findViewById(R.id.ListCover);
                        pViewHolder.Pics[0] = (SimpleDraweeView) convertView.findViewById(R.id.PIC11);
                        pViewHolder.Pics[1] = (SimpleDraweeView) convertView.findViewById(R.id.PIC12);
                        pViewHolder.Pics[2] = (SimpleDraweeView) convertView.findViewById(R.id.PIC13);
                        pViewHolder.Pics[3] = (SimpleDraweeView) convertView.findViewById(R.id.PIC21);
                        pViewHolder.Pics[4] = (SimpleDraweeView) convertView.findViewById(R.id.PIC22);
                        pViewHolder.Pics[5] = (SimpleDraweeView) convertView.findViewById(R.id.PIC23);
                        pViewHolder.Pics[6] = (SimpleDraweeView) convertView.findViewById(R.id.PIC31);
                        pViewHolder.Pics[7] = (SimpleDraweeView) convertView.findViewById(R.id.PIC32);
                        pViewHolder.Pics[8] = (SimpleDraweeView) convertView.findViewById(R.id.PIC33);
                    }
                } else {
                    if (msgLine.ViewType == API.VIEW_NORMAL) {
                        nViewHolder = (NormalViewHolder) convertView.getTag();
                    } else if (msgLine.ViewType == API.VIEW_PICTURE) {
                        pViewHolder = (PictureViewHolder) convertView.getTag();
                    }
                }

                if (msgLine.ViewType == API.VIEW_NORMAL) {
                    nViewHolder.ItemTitle.setText(msgLine.Title);
                    nViewHolder.ItemText.setText(msgLine.SubTitle);
                    nViewHolder.ItemTime.setText(new PrettyTime().format(new Date(msgLine.PubTime * 1000)));
                    nViewHolder.ListCover.setImageURI(Uri.parse(msgLine.CoverImg));

                    convertView.setTag(nViewHolder);
                } else if (msgLine.ViewType == API.VIEW_PICTURE) {
                    pViewHolder.ItemTitle.setText(msgLine.Title);
                    if (msgLine.Title.length() == 0) {
                        pViewHolder.ItemTitle.setText(msgLine.AuthorName);
                    }

                    pViewHolder.ItemText.setText(msgLine.SubTitle);
                    pViewHolder.ItemText.setAutoLinkMask(Linkify.WEB_URLS);
                    pViewHolder.ItemText.setMovementMethod(LinkMovementMethod.getInstance());

                    pViewHolder.ItemTime.setText(new PrettyTime().format(new Date(msgLine.PubTime * 1000)));
                    pViewHolder.ListCover.setImageURI(Uri.parse(msgLine.AuthorCoverImg));

                    if (msgLine.Pics == null) {
                        msgLine.Pics = new String[0];
                    }

                    for (int i = 0; i < msgLine.Pics.length; i++) {
                        ViewGroup.LayoutParams lp = pViewHolder.Pics[i].getLayoutParams();
                        lp.height = lp.width = parent.getWidth() / 3;
                        pViewHolder.Pics[i].setLayoutParams(lp);
                        pViewHolder.Pics[i].setImageURI(Uri.parse(msgLine.Pics[i]));

                    }

                    for (int i = msgLine.Pics.length; i < 9; i++) {
                        ViewGroup.LayoutParams lp = pViewHolder.Pics[i].getLayoutParams();
                        lp.height = lp.width = 0;
                        pViewHolder.Pics[i].setLayoutParams(lp);
                        pViewHolder.Pics[i].setImageURI(Uri.EMPTY);
                    }

                    convertView.setTag(pViewHolder);
                }

                return convertView;
            }
        });

        lv.setSelectionFromTop(cachePos, 0);
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

        mChanFragmentData = new ChanFragmentData(this, chanInfo, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_msgs_display, container, false);

        mrl = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        lv = (ListView) rootView.findViewById(R.id.listView2);

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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id < 0) {
                    return;
                }
                Intent intent = new Intent(getActivity(), MoreInfoActivity.class);
                intent.putExtra("msgid", mChanFragmentData.GetItem((int) id).Id);
                getActivity().startActivity(intent);
            }
        });


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
