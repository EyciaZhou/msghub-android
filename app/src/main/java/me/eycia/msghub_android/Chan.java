package me.eycia.msghub_android;

/**
 * Created by eycia on 2/28/16.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import me.eycia.api.API;
import me.eycia.api.ChanInfo;
import me.eycia.api.MsgBase;
import me.eycia.api.MsgLine;

/**
 * A placeholder fragment containing a simple view.
 */
public class Chan extends Fragment {
    private ChanInfo chanInfo;
    private MsgLine[] msgLines;

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

        fragment.msgLines = new MsgLine[0];
        fragment.noMore = false;

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
                            MsgLine[] msgs = (MsgLine[]) (o);

                            Vector<MsgLine> forSort = new Vector<MsgLine>();
                            for (int i = 0; i < msgs.length; i++) {
                                forSort.add(msgs[i]);
                            }
                            Collections.sort(forSort);

                            msgLines = forSort.toArray(new MsgLine[forSort.size()]);

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
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private void getMore(MsgBase lst) {
        API.PageCallback(chanInfo.Id, 20, lst.Id, lst.SnapTime, new API.Callback() {

            @Override
            public void Succ(final Object o) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mrl.setRefreshing(false);

                        if (o != null) {
                            MsgLine[] msgs = (MsgLine[])(o);

                            if (msgs == null || msgs.length == 0) {
                                noMore = true;
                                Toast.makeText(getActivity().getApplicationContext(), "No More", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (msgLines == null || msgLines.length == 0) {
                                msgLines = msgs;
                                Update();
                                return;
                            }

                            Set<String> DupRemove = new HashSet<String>();

                            Vector<MsgLine> msgsFinal = new Vector<>();

                            for (int i = 0; i < msgs.length; i++) {
                                msgsFinal.add(msgs[i]);
                                DupRemove.add(msgs[i].Id);
                            }

                            for (int i = 0; i < msgLines.length; i++) {
                                if (!DupRemove.contains(msgLines[i].Id)) {
                                    msgsFinal.add(msgLines[i]);
                                    DupRemove.add(msgLines[i].Id);
                                }
                            }

                            Collections.sort(msgsFinal);
                            msgLines = msgsFinal.toArray(new MsgLine[msgsFinal.size()]);

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
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    public void Update() {
        ArrayList<HashMap<String, String>> ls = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < this.msgLines.length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", this.msgLines[i].Title);
            map.put("ItemText", this.msgLines[i].SubTitle);
            map.put("ListCover", this.msgLines[i].CoverImg);
            ls.add(map);
        }

        final int cachePos = lv.getFirstVisiblePosition();

        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return msgLines.length;
            }

            @Override
            public Object getItem(int position) {
                return msgLines[position];
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
                MsgLine msgLine = (MsgLine)getItem(position);
                NormalViewHolder nViewHolder = null;
                PictureViewHolder pViewHolder = null;

                if (convertView == null || ((BaseViewHolder)convertView.getTag()).ViewType != msgLine.ViewType) {
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
                        pViewHolder.Pics[0] = (SimpleDraweeView)convertView.findViewById(R.id.PIC11);
                        pViewHolder.Pics[1] = (SimpleDraweeView)convertView.findViewById(R.id.PIC12);
                        pViewHolder.Pics[2] = (SimpleDraweeView)convertView.findViewById(R.id.PIC13);
                        pViewHolder.Pics[3] = (SimpleDraweeView)convertView.findViewById(R.id.PIC21);
                        pViewHolder.Pics[4] = (SimpleDraweeView)convertView.findViewById(R.id.PIC22);
                        pViewHolder.Pics[5] = (SimpleDraweeView)convertView.findViewById(R.id.PIC23);
                        pViewHolder.Pics[6] = (SimpleDraweeView)convertView.findViewById(R.id.PIC31);
                        pViewHolder.Pics[7] = (SimpleDraweeView)convertView.findViewById(R.id.PIC32);
                        pViewHolder.Pics[8] = (SimpleDraweeView)convertView.findViewById(R.id.PIC33);
                    }
                } else {
                    if (msgLine.ViewType == API.VIEW_NORMAL) {
                        nViewHolder = (NormalViewHolder)convertView.getTag();
                    } else if (msgLine.ViewType == API.VIEW_PICTURE) {
                        pViewHolder = (PictureViewHolder)convertView.getTag();
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

                    if ( msgLine.Pics == null) {
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
                        pViewHolder.Pics[i].setImageURI(Uri.parse("http://7xtaud.com2.z0.glb.qiniucdn.com/8767"));
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
        savedInstanceState.putParcelableArray("msgInfo", msgLines);
        savedInstanceState.putBoolean("noMore", noMore);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.chanInfo = getArguments().getParcelable("chanInfo");

        View rootView = inflater.inflate(R.layout.fragment_msgs_display, container, false);

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
            this.msgLines = (MsgLine[])savedInstanceState.getParcelableArray("msgInfo");
            this.noMore = savedInstanceState.getBoolean("noMore");
            Update();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("msghub", String.valueOf(id));
                if (id < 0) {
                    return;
                }
                Intent intent = new Intent(getActivity(), MsgActivity.class);
                intent.putExtra("msgid", msgLines[((int) id)].Id);
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
                    View lastItemView = view.getChildAt(view.getChildCount()-1);
                    if (lastItemView != null && view.getBottom() >= lastItemView.getBottom()) {
                        if (!noMore) {
                            //TODO: show more view
                            if (msgLines.length > 0) {
                                getMore(msgLines[msgLines.length - 1]);
                            }

                        }
                    }
                }
            }
        });

        return rootView;
    }
}
